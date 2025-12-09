<script lang="ts" setup>
import type { VxeGridProps } from '#/adapter/vxe-table';

import { ref } from 'vue';

import { Page, useVbenDrawer } from '@vben/common-ui';

import {
  Button,
  message,
  Popconfirm,
  Space,
  Tag,
  Tooltip,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteBatchDictData,
  deleteBatchDictType,
  deleteDictData,
  deleteDictType,
  getDictDataPage,
  getDictTypePage,
  refreshDictCache,
} from '#/api/modules/system/dict';

import {
  dataColumns,
  dataSearchFormSchema,
  typeColumns,
  typeSearchFormSchema,
} from './data';
import DataDrawer from './DataDrawer.vue';
import TypeDrawer from './TypeDrawer.vue';

const currentDictType = ref<any>(null);

// ============ 字典类型 ============

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
    actionWrapperClass: 'col-span-1',
    collapsed: true,
    showCollapseButton: true,
  },
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
  typeGridApi.reload();
  if (currentDictType.value?.id === row.id) {
    currentDictType.value = null;
    dataGridApi.setGridOptions({ data: [] });
  }
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
  typeGridApi.reload();
  if (records.some((item: any) => item.id === currentDictType.value?.id)) {
    currentDictType.value = null;
    dataGridApi.setGridOptions({ data: [] });
  }
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
  currentDictType.value = row;
  dataGridApi.reload();
}

// ============ 字典数据 ============

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
        const mergedQuery = currentDictType.value
          ? { ...formValues, typeCode: currentDictType.value.code }
          : formValues || {};
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

function handleOpenDataDrawer(isUpdate: boolean, record?: any) {
  if (!currentDictType.value && !isUpdate) {
    message.warning('请先选择字典类型');
    return;
  }
  dataDrawerApi.setData({
    isUpdate,
    record,
    dictType: currentDictType.value?.code,
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
  <Page auto-content-height>
    <div class="flex h-full gap-4">
      <!-- 左侧：字典类型 -->
      <div
        class="flex h-full w-5/12 flex-col rounded-lg border border-gray-200 bg-white p-2 shadow-sm dark:border-gray-800 dark:bg-[#151515]"
      >
        <div
          class="mb-2 flex items-center justify-between border-l-4 border-primary px-2 py-1 text-base font-bold"
        >
          <span>字典类型</span>
        </div>
        <div class="flex-1 overflow-hidden">
          <TypeGrid>
            <template #toolbar_buttons>
              <Space>
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
              </Space>
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
              <Tag :color="row.status === 1 ? 'green' : 'red'">
                {{ row.status === 1 ? '正常' : '停用' }}
              </Tag>
            </template>
            <template #action="{ row }">
              <div class="flex items-center gap-2">
                <Tooltip title="编辑">
                  <Button
                    type="link"
                    size="small"
                    @click.stop="
                      (handleTypeSelect(row), handleOpenTypeDrawer(true, row))
                    "
                    class="!p-0"
                  >
                    <template #icon>
                      <span
                        class="icon-[lucide--edit] text-lg text-blue-500"
                      ></span>
                    </template>
                  </Button>
                </Tooltip>
                <Popconfirm title="确认删除？" @confirm="handleDeleteType(row)">
                  <Tooltip title="删除">
                    <Button
                      type="link"
                      size="small"
                      danger
                      @click.stop
                      class="!p-0"
                    >
                      <template #icon>
                        <span
                          class="icon-[lucide--trash-2] text-lg text-red-500"
                        ></span>
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

      <!-- 右侧：字典数据 -->
      <div
        class="flex h-full w-7/12 flex-col rounded-lg border border-gray-200 bg-white p-2 shadow-sm dark:border-gray-800 dark:bg-[#151515]"
      >
        <div
          class="mb-2 flex items-center border-l-4 border-primary px-2 py-1 text-base font-bold"
        >
          <span>字典数据</span>
          <span
            v-if="currentDictType"
            class="ml-2 rounded bg-gray-100 px-2 py-0.5 text-sm font-normal text-gray-500 dark:bg-gray-800"
          >
            {{ currentDictType.name }}
          </span>
        </div>
        <div class="flex-1 overflow-hidden">
          <DataGrid>
            <template #toolbar_buttons>
              <Space>
                <Button
                  type="primary"
                  :disabled="!currentDictType"
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
                  <Button danger :disabled="!currentDictType">
                    <template #icon>
                      <span class="icon-[lucide--trash-2]"></span>
                    </template>
                    删除
                  </Button>
                </Popconfirm>
              </Space>
            </template>
            <template #status="{ row }">
              <Tag :color="row.status === 1 ? 'green' : 'red'">
                {{ row.status === 1 ? '正常' : '停用' }}
              </Tag>
            </template>
            <template #action="{ row }">
              <div class="flex items-center gap-2">
                <Tooltip title="编辑">
                  <Button
                    type="link"
                    size="small"
                    @click="handleOpenDataDrawer(true, row)"
                    class="!p-0"
                  >
                    <template #icon>
                      <span
                        class="icon-[lucide--edit] text-lg text-blue-500"
                      ></span>
                    </template>
                  </Button>
                </Tooltip>
                <Popconfirm title="确认删除？" @confirm="handleDeleteData(row)">
                  <Tooltip title="删除">
                    <Button type="link" size="small" danger class="!p-0">
                      <template #icon>
                        <span
                          class="icon-[lucide--trash-2] text-lg text-red-500"
                        ></span>
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
    </div>
  </Page>
</template>
