<script lang="ts" setup>
import { Page } from '@vben/common-ui';
import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  getDictTypePage,
  deleteDictType,
  refreshDictCache,
  getDictDataPage,
  deleteDictData,
} from '#/api/modules/system/dict';
import { typeColumns, typeSearchFormSchema, dataColumns, dataSearchFormSchema } from './data';
import { ref, reactive } from 'vue';
import { Button, Popconfirm, message, Tag } from 'ant-design-vue';
import TypeDrawer from './TypeDrawer.vue';
import DataDrawer from './DataDrawer.vue';
import { useVbenDrawer } from '@vben/common-ui';

const currentDictType = ref<any>(null);

// ============ 字典类型 ============

const [TypeDrawerComponent, typeDrawerApi] = useVbenDrawer({
  connectedComponent: TypeDrawer,
});

const typeGridOptions = reactive({
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
        const { list, total } = await getDictTypePage(
          { ...formValues },
          { pageNo: page.currentPage, pageSize: page.pageSize },
        );
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
  listeners: {
    cellClick: ({ row }) => {
      handleTypeSelect(row);
    },
  },
  rowConfig: {
    isCurrent: true,
    isHover: true,
  },
});

const [TypeGrid, typeGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: typeSearchFormSchema,
  },
  gridOptions: typeGridOptions,
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

async function handleRefreshCache() {
  await refreshDictCache();
  message.success('刷新成功');
}

function handleTypeSelect(row: any) {
  currentDictType.value = row;
  dataGridApi.reload();
}

// ============ 字典数据 ============

const [DataDrawerComponent, dataDrawerApi] = useVbenDrawer({
  connectedComponent: DataDrawer,
});

const dataGridOptions = reactive({
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
        if (!currentDictType.value) {
          return { list: [], total: 0 };
        }
        const { list, total } = await getDictDataPage(
          { typeCode: currentDictType.value.code, ...formValues },
          { pageNo: page.currentPage, pageSize: page.pageSize },
        );
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
});

const [DataGrid, dataGridApi] = useVbenVxeGrid({
  formOptions: {
    schema: dataSearchFormSchema,
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
    dictType: currentDictType.value?.code 
  });
  dataDrawerApi.open();
}

async function handleDeleteData(row: any) {
  await deleteDictData(row.id);
  message.success('删除成功');
  dataGridApi.reload();
}

</script>

<template>
  <Page auto-content-height>
    <div class="flex h-full gap-4">
      <!-- 左侧：字典类型 -->
      <div class="w-1/2 h-full overflow-hidden">
        <TypeGrid>
          <template #toolbar_buttons>
            <Button type="primary" @click="handleOpenTypeDrawer(false)">新增类型</Button>
            <Button class="ml-2" @click="handleRefreshCache">刷新缓存</Button>
          </template>
          <template #status="{ row }">
            <Tag :color="row.status === 1 ? 'green' : 'red'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </Tag>
          </template>
          <template #action="{ row }">
            <Button type="link" size="small" @click.stop="handleOpenTypeDrawer(true, row)">编辑</Button>
            <Popconfirm title="确认删除？" @confirm="handleDeleteType(row)">
              <Button type="link" size="small" danger @click.stop>删除</Button>
            </Popconfirm>
          </template>
        </TypeGrid>
        <TypeDrawerComponent @success="typeGridApi.reload()" />
      </div>

      <!-- 右侧：字典数据 -->
      <div class="w-1/2 h-full overflow-hidden">
        <DataGrid>
          <template #toolbar_buttons>
            <Button type="primary" :disabled="!currentDictType" @click="handleOpenDataDrawer(false)">新增数据</Button>
          </template>
          <template #status="{ row }">
            <Tag :color="row.status === 1 ? 'green' : 'red'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </Tag>
          </template>
          <template #action="{ row }">
            <Button type="link" size="small" @click="handleOpenDataDrawer(true, row)">编辑</Button>
            <Popconfirm title="确认删除？" @confirm="handleDeleteData(row)">
              <Button type="link" size="small" danger>删除</Button>
            </Popconfirm>
          </template>
        </DataGrid>
        <DataDrawerComponent @success="dataGridApi.reload()" />
      </div>
    </div>
  </Page>
</template>
