<script lang="ts" setup>
import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { ref } from 'vue';
import { Page } from '@vben/common-ui';
import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { Button, Dropdown, Menu, message, Modal, Popconfirm, Upload } from 'ant-design-vue';
import { batchDeleteFiles, deleteFile, getFilePage, uploadFile, getDownloadToken } from '#/api/modules/storage/file';

const uploadLoading = ref(false);

const searchFormSchema: VbenFormProps['schema'] = [
  {
    component: 'Input',
    fieldName: 'name',
    label: '文件名',
    componentProps: { placeholder: '请输入文件名' },
  },
  {
    component: 'Input',
    fieldName: 'mimeType',
    label: '类型(MIME)',
    componentProps: { placeholder: '如 image/png' },
  },
  {
    component: 'Input',
    fieldName: 'ext',
    label: '扩展名',
    componentProps: { placeholder: '如：png、pdf' },
  },
];

const formOptions: VbenFormProps = {
  schema: searchFormSchema,
  collapsed: true,
};

const columns: VxeGridProps['columns'] = [
  { field: 'name', title: '文件名' },
  { field: 'mimeType', title: '类型' },
  { field: 'ext', title: '扩展名', width: 120 },
  { field: 'size', title: '大小(B)', width: 140 },
  { field: 'storageType', title: '存储', width: 120 },
  { field: 'createTime', title: '创建时间', width: 180 },
  { title: '操作', width: 200, slots: { default: 'action' } },
];

const gridOptions: VxeGridProps = {
  stripe: true,
  checkboxConfig: { highlight: true },
  columns,
  height: 'auto',
  emptyText: '暂无文件，点击“上传”添加文件',
  scrollY: { enabled: true, gt: 100 },
  pagerConfig: {
    enabled: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100],
    layouts: ['PrevPage', 'JumpNumber', 'NextPage', 'FullJump', 'Sizes', 'Total'],
    perfect: true,
  },
  proxyConfig: {
    enabled: true,
    autoLoad: true,
    response: { result: 'list', total: 'total' },
    ajax: {
      /**
       * 分页查询文件
       */
      query: async ({ page }, formValues) => {
        const { list, total } = await getFilePage(formValues || {}, page);
        return { list, total } as any;
      },
    },
  },
  toolbarConfig: { custom: true, export: false, refresh: true, zoom: true },
};

const [Grid, gridApi] = useVbenVxeGrid({ formOptions, gridOptions });

/**
 * 处理上传文件
 */
const handleUpload = async (file: File) => {
  uploadLoading.value = true;
  try {
    await uploadFile(file);
    message.success({ content: '上传成功', duration: 2 });
    gridApi.reload();
  } finally {
    uploadLoading.value = false;
  }
};

/**
 * 删除单个文件
 */
const handleDelete = async (id: string | number) => {
  await deleteFile(id);
  message.success({ content: '删除成功', duration: 2 });
  gridApi.reload();
};

/**
 * 批量删除选中文件
 */
const handleBatchDelete = async () => {
  const selectedRows = gridApi.grid.getCheckboxRecords();
  if (!selectedRows || selectedRows.length === 0) {
    message.warning({ content: '请选择要删除的文件', duration: 3 });
    return;
  }
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRows.length} 个文件吗？`,
    okText: '确定',
    cancelText: '取消',
    type: 'warning',
    onOk: async () => {
      const ids = selectedRows.map((row: any) => row.id);
      await batchDeleteFiles(ids);
      message.success({ content: `成功删除 ${selectedRows.length} 个文件`, duration: 2 });
      gridApi.grid.clearCheckboxRow();
      gridApi.reload();
    },
  });
};

/**
 * 判断是否为图片类型
 */
const isImage = (row: any) => {
  const m = (row.mimeType || '').toLowerCase();
  return m.startsWith('image/');
};

/**
 * 预览文件（新窗口打开）
 */
const preview = (row: any) => {
  const url = row.url;
  if (!url) return;
  window.open(url, '_blank');
};

/**
 * 下载文件（令牌换取临时链接）
 */
const download = async (row: any) => {
  const publicId = row.publicId;
  if (!publicId) return;
  const { url } = await getDownloadToken(publicId);
  if (!url) return;
  const a = document.createElement('a');
  a.href = url as string;
  a.click();
};
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="文件管理" table-title-help="存储文件统一管理">
      <template #toolbar-tools>
        <div class="flex items-center gap-3">
          <Upload
            :maxCount="1"
            :showUploadList="false"
            :beforeUpload="() => false"
            @change="(e:any) => e?.file?.originFileObj && handleUpload(e.file.originFileObj)"
          >
            <Button type="primary" :loading="uploadLoading">
              <template #icon>
                <span class="icon-[lucide--upload] mr-1"></span>
              </template>
              上传
            </Button>
          </Upload>

          <Dropdown.Button>
            批量操作
            <template #overlay>
              <Menu>
                <Menu.Item key="delete" @click="handleBatchDelete">
                  <div class="icon-[lucide--trash-2] mr-2"></div>
                  批量删除
                </Menu.Item>
              </Menu>
            </template>
          </Dropdown.Button>
        </div>
      </template>

      <template #action="{ row }">
        <div class="flex items-center justify-center gap-1.5">
          <Button
            class="flex h-7 w-7 items-center justify-center p-0"
            size="small"
            type="default"
            @click="preview(row)"
          >
            <template #icon>
              <div class="icon-[lucide--image] text-green-500"></div>
            </template>
          </Button>
          <Button
            class="flex h-7 w-7 items-center justify-center p-0"
            size="small"
            type="default"
            @click="download(row)"
          >
            <template #icon>
              <div class="icon-[lucide--download] text-blue-500"></div>
            </template>
          </Button>
          <Popconfirm cancel-text="取消" ok-text="确定" title="确定删除此文件?" @confirm="handleDelete(row.id)">
            <Button
              class="flex h-7 w-7 items-center justify-center p-0"
              danger
              ghost
              shape="circle"
              size="small"
              type="primary"
            >
              <template #icon>
                <div class="icon-[lucide--trash-2] text-red-500"></div>
              </template>
            </Button>
          </Popconfirm>
        </div>
      </template>
    </Grid>
  </Page>
</template>

