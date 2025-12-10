<script lang="ts" setup>
import type { VxeGridProps } from '#/adapter/vxe-table';

import { watch } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';

import {
  Button,
  message,
  Popconfirm,
  Tag,
  Tooltip,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteBatchDictData,
  deleteDictData,
  getDictDataPage,
} from '#/api/modules/system/dict';

import { dataColumns, dataSearchFormSchema } from '../data';
import DataDrawer from '../DataDrawer.vue';

interface Props {
  dictType: any;
}

const props = defineProps<Props>();

const [DataDrawerComponent, dataDrawerApi] = useVbenDrawer({
  connectedComponent: DataDrawer,
});

const dataGridOptions: VxeGridProps = {
  columns: dataColumns,
  proxyConfig: {
    enabled: true,
    autoLoad: false, // 默认不加载，等选中类型后再加载
    response: {
      result: 'list',
      total: 'total',
    },
    ajax: {
      query: async ({ page }, formValues) => {
        const mergedQuery = props.dictType
          ? { ...formValues, typeCode: props.dictType.code }
          : formValues || {};
        // 如果没有选中类型，且没有查询条件，不查数据或者返回空？
        // 原逻辑是：const mergedQuery = currentDictType.value ? ... : formValues || {};
        // 如果 currentDictType 为空，就只传 formValues。
        // 但是通常需要 typeCode。如果没有 typeCode，后端可能查所有或者报错。
        // 这里的逻辑保持原样，如果 props.dictType 为空，则不带 typeCode。
        const { list, total } = await getDictDataPage(mergedQuery, page);
        return { list, total };
      },
    },
  },
  toolbarConfig: {
    custom: true,
    refresh: true,
    slots: {
      buttons: 'toolbar_buttons',
    },
  },
  height: 'auto',
  pagerConfig: {
    enabled: true,
  },
  rowConfig: {
    keyField: 'id',
    isCurrent: true,
    isHover: true,
  },
  checkboxConfig: {
    reserve: true,
    highlight: true,
  },
};

const [DataGrid, dataGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: dataSearchFormSchema,
    actionWrapperClass: 'col-span-1',
    // 是否可展开
    showCollapseButton: false,
  },
  gridOptions: dataGridOptions,
});

watch(
  () => props.dictType,
  (val) => {
    if (val) {
      dataGridApi.reload();
    } else {
      dataGridApi.setGridOptions({ data: [] });
    }
  },
);

function handleOpenDataDrawer(isUpdate: boolean, record?: any) {
  if (!props.dictType && !isUpdate) {
    message.warning('请先选择字典类型');
    return;
  }
  dataDrawerApi.setData({
    isUpdate,
    record,
    dictType: props.dictType?.code,
  });
  dataDrawerApi.open();
}

async function handleDeleteData(row: any) {
  await deleteDictData(row.id);
  message.success('删除成功');
  dataGridApi.reload();
}

async function handleBatchDeleteData() {
  const records = dataGridApi.grid.getCheckboxRecords();
  if (records.length === 0) {
    message.warning('请选择要删除的记录');
    return;
  }
  const ids = records.map((item: any) => item.id);
  await deleteBatchDictData(ids);
  message.success('批量删除成功');
  dataGridApi.reload();
}
</script>

<template>
  <div
    class="flex h-full w-7/12 flex-col rounded-lg border border-gray-200 bg-white p-2 shadow-sm dark:border-gray-800 dark:bg-[#151515]"
  >
    <div
      class="mb-2 flex items-center border-l-4 border-primary px-2 py-1 text-base font-bold"
    >
      <span>字典数据</span>
      <span
        v-if="props.dictType"
        class="ml-2 rounded bg-gray-100 px-2 py-0.5 text-sm font-normal text-gray-500 dark:bg-gray-800"
      >
        {{ props.dictType.name }}
      </span>
    </div>
    <div class="flex-1 overflow-hidden">
      <DataGrid>
        <template #toolbar_buttons>
          <div class="flex items-center gap-3">
            <Button
              type="primary"
              :disabled="!props.dictType"
              @click="handleOpenDataDrawer(false)"
            >
              <template #icon>
                <span class="icon-[lucide--plus]"></span>
              </template>
              新增
            </Button>
            <Popconfirm
              title="确认删除选中的记录？"
              @confirm="handleBatchDeleteData"
            >
              <Button danger :disabled="!props.dictType">
                <template #icon>
                  <span class="icon-[lucide--trash-2]"></span>
                </template>
                删除
              </Button>
            </Popconfirm>
          </div>
        </template>
        <template #status="{ row }">
          <Tag :color="row.status === 1 ? 'green' : 'red'" :bordered="false">
            {{ row.status === 1 ? '正常' : '停用' }}
          </Tag>
        </template>
        <template #action="{ row }">
          <div class="flex items-center gap-1.5">
            <Tooltip title="编辑">
              <Button
                class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                ghost
                shape="circle"
                size="small"
                type="primary"
                @click="handleOpenDataDrawer(true, row)"
              >
                <template #icon>
                  <span class="icon-[lucide--edit] text-blue-500"></span>
                </template>
              </Button>
            </Tooltip>
            <Popconfirm title="确认删除？" @confirm="handleDeleteData(row)">
              <Tooltip title="删除">
                <Button
                  class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                  danger
                  ghost
                  shape="circle"
                  size="small"
                  type="primary"
                >
                  <template #icon>
                    <span class="icon-[lucide--trash-2] text-red-500"></span>
                  </template>
                </Button>
              </Tooltip>
            </Popconfirm>
          </div>
        </template>
      </DataGrid>
    </div>
    <DataDrawerComponent @success="dataGridApi.reload()" />
  </div>
</template>
