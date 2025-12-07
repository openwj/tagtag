<script lang="ts" setup>
import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { ref } from 'vue';

import { Page } from '@vben/common-ui';

import { Button, Dropdown, Menu, message, Modal, Popconfirm } from 'ant-design-vue';
import Upload from 'ant-design-vue/es/upload';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  batchDeleteFiles,
  deleteFile,
  getDownloadToken,
  getFilePage,
  downloadByPublicId,
  uploadFile,
} from '#/api/modules/storage/file';

const uploadLoading = ref(false);
// 上传大小上限（与后端 spring.servlet.multipart 保持一致）
const MAX_UPLOAD_SIZE = 50 * 1024 * 1024;

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
    layouts: [
      'PrevPage',
      'JumpNumber',
      'NextPage',
      'FullJump',
      'Sizes',
      'Total',
    ],
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
 * @param file 原始文件对象
 */
const handleUpload = async (file: File) => {
  uploadLoading.value = true;
  try {
    await uploadFile(file);
    message.success({ content: '上传成功', duration: 2 });
    gridApi.reload();
  } catch (e) {
    message.error({ content: '上传失败', duration: 3 });
  } finally {
    uploadLoading.value = false;
  }
};

/**
 * 自定义上传逻辑：避免 Upload 多次触发导致重复请求
 * @param options Upload 自定义请求参数
 */
const onCustomUpload = async (options: any) => {
  const file = options?.file as File | undefined;
  if (!file) {
    options?.onError?.(new Error('未选择文件'));
    return;
  }
  if (file.size > MAX_UPLOAD_SIZE) {
    message.error({ content: '文件大小超过 50MB 限制', duration: 3 });
    options?.onError?.(new Error('文件大小超过限制'));
    return;
  }
  uploadLoading.value = true;
  try {
    await uploadFile(file);
    message.success({ content: '上传成功', duration: 2 });
    gridApi.reload();
    options?.onSuccess?.({}, file);
  } catch (e) {
    message.error({ content: '上传失败', duration: 3 });
    options?.onError?.(e);
  } finally {
    uploadLoading.value = false;
  }
};

/**
 * 删除单个文件
 */
const handleDelete = async (id: number | string) => {
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
      message.success({
        content: `成功删除 ${selectedRows.length} 个文件`,
        duration: 2,
      });
      gridApi.grid.clearCheckboxRow();
      gridApi.reload();
    },
  });
};

/**
 * 预览文件（新窗口打开）
 */
const preview = async (row: any) => {
  const publicId = row.publicId;
  if (!publicId) return;
  try {
    const blob = await downloadByPublicId(publicId);
    const objectUrl = URL.createObjectURL(blob as Blob);
    window.open(objectUrl, '_blank');
    // 可选：稍后释放对象URL
    setTimeout(() => URL.revokeObjectURL(objectUrl), 60_000);
  } catch (e) {
    message.error({ content: '预览失败', duration: 3 });
  }
};

/**
 * 下载文件（令牌换取临时链接）
 */
const download = async (row: any) => {
  const publicId = row.publicId;
  if (!publicId) return;
  try {
    const blob = await downloadByPublicId(publicId);
    const objectUrl = URL.createObjectURL(blob as Blob);
    const a = document.createElement('a');
    a.href = objectUrl;
    a.download = (row.originalName || row.name || 'file') as string;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    setTimeout(() => URL.revokeObjectURL(objectUrl), 60_000);
  } catch (e) {
    message.error({ content: '下载失败', duration: 3 });
  }
};
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="文件管理" table-title-help="存储文件统一管理">
      <template #toolbar-tools>
        <div class="flex items-center gap-3">
          <Upload
            :max-count="1"
            :show-upload-list="false"
            :custom-request="onCustomUpload"
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
          <Popconfirm
            cancel-text="取消"
            ok-text="确定"
            title="确定删除此文件?"
            @confirm="handleDelete(row.id)"
          >
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
