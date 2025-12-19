<script lang="ts" setup>
import type { VxeGridProps } from '#/adapter/vxe-table';

import { useVbenDrawer } from '@vben/common-ui';

import { Button, message, Popconfirm, Tag, Tooltip } from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteBatchDictType,
  deleteDictType,
  getDictTypePage,
  refreshDictCache,
} from '#/api/modules/system/dict';

import { typeColumns, typeSearchFormSchema } from '../data';
import TypeDrawer from '../TypeDrawer.vue';

const emit = defineEmits<{
  (e: 'select', record: any): void;
}>();

const [TypeDrawerComponent, typeDrawerApi] = useVbenDrawer({
  connectedComponent: TypeDrawer,
});

const typeGridOptions: VxeGridProps = {
  columns: typeColumns,
  proxyConfig: {
    enabled: true,
    autoLoad: true,
    response: {
      result: 'list',
      total: 'total',
    },
    ajax: {
      query: async ({ page }, formValues) => {
        const { list, total } = await getDictTypePage(formValues, page);
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

const [TypeGrid, typeGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: typeSearchFormSchema,
    collapsed: true,
    showCollapseButton: true,
    baseColProps: { span: 24 },
    // labelWidth: 70,
    actionColOptions: { span: 24 },
    compact: true,
  } as any,
  gridOptions: typeGridOptions,
  gridEvents: {
    cellClick: ({ row }: any) => handleTypeSelect(row),
  },
});

function handleOpenTypeDrawer(isUpdate: boolean, record?: any) {
  typeDrawerApi.setData({ isUpdate, record });
  typeDrawerApi.open();
}

async function handleDeleteType(row: any) {
  await deleteDictType(row.id);
  message.success('删除成功');

  // 如果删除的是当前选中的行，清除选中状态
  const currentRecord = typeGridApi.grid.getCurrentRecord();
  if (currentRecord && currentRecord.id === row.id) {
    handleTypeSelect(null);
  }

  typeGridApi.reload();
}

async function handleBatchDeleteType() {
  const records = typeGridApi.grid.getCheckboxRecords();
  if (records.length === 0) {
    message.warning('请选择要删除的记录');
    return;
  }

  const ids = records.map((item: any) => item.id);
  await deleteBatchDictType(ids);
  message.success('批量删除成功');

  // 如果批量删除中包含当前选中的行，清除选中状态
  const currentRecord = typeGridApi.grid.getCurrentRecord();
  if (currentRecord && ids.includes(currentRecord.id)) {
    handleTypeSelect(null);
  }

  typeGridApi.reload();
}

async function handleRefreshCache() {
  await refreshDictCache();
  message.success('刷新成功');
}

/**
 * 选中字典类型后刷新右侧数据表格
 * @param row 选中的字典类型行
 */
function handleTypeSelect(row: any) {
  emit('select', row);
}
</script>

<template>
  <div
    class="flex h-full flex-col rounded-lg border border-gray-200 bg-white p-2 shadow-sm dark:border-gray-800 dark:bg-[#151515]"
  >
    <div
      class="mb-2 flex items-center justify-between border-l-4 border-primary px-2 py-1 text-base font-bold"
    >
      <span>字典类型</span>
    </div>
    <div class="flex-1 overflow-hidden">
      <TypeGrid>
        <template #toolbar_buttons>
          <div class="flex items-center gap-3">
            <Button type="primary" @click="handleOpenTypeDrawer(false)">
              <template #icon>
                <span class="icon-[lucide--plus]"></span>
              </template>
              新增
            </Button>
            <Popconfirm
              title="确认删除选中的记录？"
              @confirm="handleBatchDeleteType"
            >
              <Button danger>
                <template #icon>
                  <span class="icon-[lucide--trash-2]"></span>
                </template>
                删除
              </Button>
            </Popconfirm>
            <Button @click="handleRefreshCache">
              <template #icon>
                <span class="icon-[lucide--refresh-cw]"></span>
              </template>
              刷新缓存
            </Button>
          </div>
        </template>
        <template #name="{ row }">
          <span
            class="cursor-pointer font-medium text-primary hover:underline"
            @click.stop="handleTypeSelect(row)"
          >
            {{ row.name }}
          </span>
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
                @click.stop="
                  (handleTypeSelect(row), handleOpenTypeDrawer(true, row))
                "
              >
                <template #icon>
                  <span class="icon-[lucide--edit] text-blue-500"></span>
                </template>
              </Button>
            </Tooltip>
            <Popconfirm title="确认删除？" @confirm="handleDeleteType(row)">
              <Tooltip title="删除">
                <Button
                  class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                  danger
                  ghost
                  shape="circle"
                  size="small"
                  type="primary"
                  @click.stop
                >
                  <template #icon>
                    <span class="icon-[lucide--trash-2] text-red-500"></span>
                  </template>
                </Button>
              </Tooltip>
            </Popconfirm>
          </div>
        </template>
      </TypeGrid>
    </div>
    <TypeDrawerComponent @success="typeGridApi.reload()" />
  </div>
</template>
