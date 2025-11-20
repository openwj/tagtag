<script lang="ts" setup>
import { ref } from 'vue';
import { Page, useVbenDrawer } from '@vben/common-ui';

import {
  Button as AButton,
  Divider as ADivider,
  Popconfirm as APopconfirm,
  Tooltip as ATooltip,
  message,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  deleteMenu,
  getMenuPage,
  listMenusByParent,
} from '#/api/modules/iam/menu';
// 注：不再依赖分页封装，页面内直接适配后端字段

import { columns, searchFormSchema } from './data';
import FormDrawer from './FormDrawer.vue';
import MenuTree from './MenuTree.vue';

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: searchFormSchema,
    collapsed: true,
    showCollapseButton: true,
  },
  gridOptions: {
    columns,
    height: 'auto',
    columnConfig: { minWidth: 120 },
    showOverflow: 'tooltip',
    pagerConfig: { enabled: true },
    rowConfig: { keyField: 'id' },
    toolbarConfig: { custom: true, export: true, refresh: true, zoom: true },
    proxyConfig: {
      enabled: true,
      autoLoad: true,
      response: { result: 'list', total: 'total' },
      ajax: {
        /**
         * 菜单分页查询
         * @param page 底层分页参数（含 pageNumber 与 pageSize）
         * @param formValues 搜索表单值
         */
        query: async ({ page }: any, formValues: any) => {
          const query = { ...formValues };
          if (currentParentId.value != null) {
            (query as any).parentId = currentParentId.value;
          }
          const { list, total } = await getMenuPage(query, page);
          return { list, total };
        },
      },
    },
  },
});

const [VFormDrawer, VFormDrawerApi] = useVbenDrawer({
  connectedComponent: FormDrawer,
});

const currentParentId = ref<null | number>(null);

/**
 * 左侧树选择父级，右侧列表以服务端分页展示
 * @param parentId 父菜单ID，null 表示全部
 */
const handleSelectParent = async (parentId: null | number) => {
  currentParentId.value = parentId;
  await gridApi.query();
};

/**
 * 新增菜单
 * @param row 可选：基于选中行初始化表单
 */
const handleAdd = (row?: Record<string, any>) => {
  const values = { isUpdate: false };
  VFormDrawerApi.setData({ values });
  VFormDrawerApi.open();
};

/**
 * 删除菜单
 * @param id 菜单ID
 */
const handleDelete = async (id: number | string) => {
  await deleteMenu(id);
  message.success('删除成功');
  await gridApi.query();
};

/**
 * 编辑菜单
 * @param row 行数据
 */
const handleEdit = (row: Record<string, any>) => {
  VFormDrawerApi.setData({ values: { ...row, isUpdate: true } });
  VFormDrawerApi.open();
};

const handleSuccess = () => {
  gridApi.reload();
};
</script>

<template>
  <Page auto-content-height>
    <div class="grid grid-cols-[280px_1fr] gap-4">
      <div class="rounded bg-[var(--custom-block-bg)] p-2">
        <MenuTree @select="handleSelectParent" />
      </div>
      <Grid table-title="菜单管理" table-title-help="系统菜单配置">
        <template #toolbar-tools>
          <AButton
            class="flex items-center"
            type="primary"
            @click="handleAdd()"
          >
            <template #icon>
              <span class="icon-[material-symbols--add-circle] mr-1"></span>
            </template>
            新增
          </AButton>
        </template>

        <template #action="{ row }">
          <div class="flex items-center justify-center">
            <ATooltip title="编辑">
              <AButton
                class="flex items-center justify-center"
                ghost
                shape="circle"
                size="small"
                type="primary"
                @click="handleEdit(row)"
              >
                <template #icon>
                  <div
                    class="icon-[material-symbols--edit-square-rounded]"
                  ></div>
                </template>
              </AButton>
            </ATooltip>

            <ADivider type="vertical" />

            <APopconfirm
              cancel-text="取消"
              ok-text="确定"
              placement="left"
              title="确定删除此数据?"
              @confirm="handleDelete(row.id)"
            >
              <ATooltip title="删除">
                <AButton
                  class="flex items-center justify-center"
                  danger
                  ghost
                  shape="circle"
                  size="small"
                  type="primary"
                >
                  <template #icon>
                    <div class="icon-[material-symbols--delete-rounded]"></div>
                  </template>
                </AButton>
              </ATooltip>
            </APopconfirm>
          </div>
        </template>
      </Grid>
    </div>

    <VFormDrawer @success="handleSuccess" />
  </Page>
</template>
