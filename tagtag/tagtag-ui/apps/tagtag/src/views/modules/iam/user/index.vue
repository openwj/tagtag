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
import { deleteUser, editUser, getUserPage } from '#/api/modules/iam/user';

import { columns, searchFormSchema } from './data';
import FormDrawer from './FormDrawer.vue';

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
      ajax: {
        /**
         * 用户分页查询
         * @param _params 底层分页参数
         * @param formValues 搜索表单值
         */
        query: async (_params: any, formValues: any) => {
          const page = { pageNo: _params.page.pageNumber, pageSize: _params.page.pageSize };
          const data = await getUserPage(formValues, page);
          const items = data?.records ?? data?.list ?? [];
          const total = Number(data?.total ?? data?.totalCount ?? 0);
          return { items, total };
        },
      },
    },
  },
});

const [VFormDrawer, VFormDrawerApi] = useVbenDrawer({ connectedComponent: FormDrawer });

/**
 * 新增用户
 * @param row 可选：基于选中行初始化表单
 */
const handleAdd = (row?: Record<string, any>) => {
  const values = { isUpdate: false };
  if (row?.deptId) (values as any).deptId = row.deptId;
  VFormDrawerApi.setData({ values });
  VFormDrawerApi.open();
};

/**
 * 切换用户状态
 * @param record 用户记录
 */
const handleStatusChange = async (record: any) => {
  try {
    const statusValue = record.status ? 1 : 0;
    await editUser({ id: record.id, status: statusValue });
    message.success('状态更新成功');
  } finally {
    await gridApi.query();
  }
};

/**
 * 删除用户
 * @param id 用户ID
 */
const handleDelete = async (id: string | number) => {
  await deleteUser(id);
  message.success('删除成功');
  await gridApi.query();
};

/**
 * 编辑用户
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
    <Grid table-title="用户信息" table-title-help="系统用户信息管理">
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