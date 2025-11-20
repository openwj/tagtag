<script lang="ts" setup>
import { Page, useVbenDrawer } from '@vben/common-ui';

import {
  Button as AButton,
  Divider as ADivider,
  Popconfirm as APopconfirm,
  Switch as ASwitch,
  Tag as ATag,
  Tooltip as ATooltip,
  message,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { addMenu, deleteMenu, editMenu, getMenuTree } from '#/api/modules/iam/menu';

import { columns, searchFormSchema } from './data';
import FormDrawer from './FormDrawer.vue';

/**
 * 菜单管理页
 * - 树形表格展示所有菜单
 * - 支持搜索筛选、状态切换、增删改
 */
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
    pagerConfig: { enabled: false },
    rowConfig: { keyField: 'id' },
    treeConfig: {
      childrenField: 'children',
      hasChild: 'hasChildren',
      expandAll: true,
      rowField: 'id',
    },
    toolbarConfig: { custom: true, export: true, refresh: true, zoom: true },
    proxyConfig: {
      enabled: true,
      autoLoad: true,
      response: { result: 'list', total: 'total' },
      ajax: {
        /**
         * 菜单树查询（不分页）
         * @param _page 未使用的分页参数
         * @param formValues 搜索表单值
         */
        query: async (_page: any, formValues: any) => {
          const tree = await getMenuTree(formValues);
          const list = Array.isArray(tree) ? tree : [];
          const total = list.length;
          return { list, total };
        },
      },
    },
  },
});

const [VFormDrawer, VFormDrawerApi] = useVbenDrawer({ connectedComponent: FormDrawer });

/**
 * 新增菜单：可选父节点
 */
const handleAdd = (row?: Record<string, any>) => {
  const values = { isUpdate: false, parentId: 0 };
  if (row?.id) values.parentId = row.id;
  VFormDrawerApi.setData({ values });
  VFormDrawerApi.open();
};

/**
 * 切换菜单状态
 * @param record 菜单记录
 */
const handleStatusChange = async (record: any) => {
  try {
    const statusValue = record.status ? 1 : 0;
    await editMenu({ id: record.id, status: statusValue });
    message.success('状态更新成功');
  } finally {
    await gridApi.query();
  }
};

/**
 * 删除菜单
 * @param id 菜单ID
 */
const handleDelete = async (id: string | number) => {
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
    <Grid table-title="菜单管理" table-title-help="系统导航与权限节点配置">
      <template #toolbar-tools>
        <AButton class="flex items-center" type="primary" @click="handleAdd()">
          <template #icon>
            <span class="icon-[material-symbols--add-circle] mr-1"></span>
          </template>
          新增
        </AButton>
      </template>

      <template #menuType="{ row }">
        <ATag :color="row.menuType === 0 ? 'blue' : row.menuType === 1 ? 'green' : 'orange'">
          {{ row.menuType === 0 ? '目录' : row.menuType === 1 ? '菜单' : '按钮' }}
        </ATag>
      </template>

      <template #icon="{ row }">
        <ATooltip :title="row.icon">
          <span v-if="row.icon" class="mr-1">{{ row.icon }}</span>
        </ATooltip>
      </template>

      <template #status="{ row }">
        <ASwitch :checked="row.status === 1" checked-children="启用" un-checked-children="禁用"
          @change="(checked: boolean | string | number) => { const isChecked = Boolean(checked); row.status = isChecked ? 1 : 0; handleStatusChange(row); }" />
      </template>

      <template #action="{ row }">
        <div class="flex items-center justify-center">
          <ATooltip title="新增子菜单">
            <AButton class="flex items-center justify-center" ghost shape="circle" size="small" type="primary" @click="handleAdd(row)">
              <template #icon>
                <div class="icon-[material-symbols--add-circle]"></div>
              </template>
            </AButton>
          </ATooltip>

          <ADivider type="vertical" />

          <ATooltip title="编辑">
            <AButton class="flex items-center justify-center" ghost shape="circle" size="small" type="primary" @click="handleEdit(row)">
              <template #icon>
                <div class="icon-[material-symbols--edit-square-rounded]"></div>
              </template>
            </AButton>
          </ATooltip>

          <ADivider type="vertical" />

          <APopconfirm cancel-text="取消" ok-text="确定" placement="left" title="确定删除此数据?" @confirm="handleDelete(row.id)">
            <ATooltip title="删除">
              <AButton class="flex items-center justify-center" danger ghost shape="circle" size="small" type="primary">
                <template #icon>
                  <div class="icon-[material-symbols--delete-rounded]"></div>
                </template>
              </AButton>
            </ATooltip>
          </APopconfirm>
        </div>
      </template>
    </Grid>

    <VFormDrawer @success="handleSuccess" />
  </Page>
</template>