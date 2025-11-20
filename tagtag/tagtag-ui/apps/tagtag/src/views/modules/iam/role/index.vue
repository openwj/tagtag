<script lang="ts" setup>
import { Page, useVbenDrawer } from '@vben/common-ui';

import {
  Button as AButton,
  Divider as ADivider,
  Popconfirm as APopconfirm,
  Switch as ASwitch,
  Tooltip as ATooltip,
  message,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteRole, editRole, getRolePage } from '#/api/modules/iam/role';
// 注：不再依赖分页封装，页面内直接适配后端字段

import { columns, searchFormSchema } from './data';
import FormDrawer from './FormDrawer.vue';
import AssignMenuDrawer from './AssignMenuDrawer.vue';

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
         * 角色分页查询
         * @param page 底层分页参数（含 pageNumber 与 pageSize）
         * @param formValues 搜索表单值
         */
        query: async ({ page }: any, formValues: any) => {
          const { list, total } = await getRolePage(formValues, page);
          return { list, total };
        },
      },
    },
  },
});

const [VFormDrawer, VFormDrawerApi] = useVbenDrawer({ connectedComponent: FormDrawer });
const [VAssignMenuDrawer, VAssignMenuDrawerApi] = useVbenDrawer({ connectedComponent: AssignMenuDrawer });

/**
 * 新增角色
 * @param row 可选：基于选中行初始化表单
 */
const handleAdd = (row?: Record<string, any>) => {
  const values = { isUpdate: false };
  VFormDrawerApi.setData({ values });
  VFormDrawerApi.open();
};

/**
 * 切换角色状态
 * @param record 角色记录
 */
const handleStatusChange = async (record: any) => {
  try {
    const statusValue = record.status ? 1 : 0;
    await editRole({ id: record.id, status: statusValue });
    message.success('状态更新成功');
  } finally {
    await gridApi.query();
  }
};

/**
 * 删除角色
 * @param id 角色ID
 */
const handleDelete = async (id: string | number) => {
  await deleteRole(id);
  message.success('删除成功');
  await gridApi.query();
};

/**
 * 编辑角色
 * @param row 行数据
 */
const handleEdit = (row: Record<string, any>) => {
  VFormDrawerApi.setData({ values: { ...row, isUpdate: true } });
  VFormDrawerApi.open();
};

/**
 * 为角色分配菜单
 * @param row 行数据
 */
const handleAssignMenus = (row: Record<string, any>) => {
  VAssignMenuDrawerApi.setData({ values: { roleId: row.id, roleName: row.name } });
  VAssignMenuDrawerApi.open();
};

const handleSuccess = () => {
  gridApi.reload();
};
</script>

<template>
  <Page auto-content-height>
    <Grid table-title="角色管理" table-title-help="系统角色与权限配置">
      <template #toolbar-tools>
        <AButton class="flex items-center" type="primary" @click="handleAdd()">
          <template #icon>
            <span class="icon-[material-symbols--add-circle] mr-1"></span>
          </template>
          新增
        </AButton>
      </template>

      <template #status="{ row }">
        <ASwitch
          :checked="row.status === 1"
          checked-children="启用"
          un-checked-children="禁用"
          @change="(checked: boolean | string | number) => { const isChecked = Boolean(checked); row.status = isChecked ? 1 : 0; handleStatusChange(row); }"
        />
      </template>

      <template #action="{ row }">
        <div class="flex items-center justify-center">
          <ATooltip title="编辑">
            <AButton class="flex items-center justify-center" ghost shape="circle" size="small" type="primary" @click="handleEdit(row)">
              <template #icon>
                <div class="icon-[material-symbols--edit-square-rounded]"></div>
              </template>
            </AButton>
          </ATooltip>

          <ADivider type="vertical" />

          <ATooltip title="权限分配">
            <AButton class="flex items-center justify-center" ghost shape="circle" size="small" type="primary" @click="handleAssignMenus(row)">
              <template #icon>
                <div class="icon-[material-symbols--shield-person]"></div>
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
    <VAssignMenuDrawer />
  </Page>
  
</template>