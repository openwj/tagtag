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

import { ref } from 'vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteUser, editUser, getUserPage } from '#/api/modules/iam/user';
import DeptTree from '../components/DeptTree.vue';
// 注：不再依赖分页封装，页面内直接适配后端字段

import { columns, searchFormSchema } from './data';
import FormDrawer from './FormDrawer.vue';

// 选中的部门ID（undefined 表示全部）
const selectedDeptId = ref<number | undefined>(undefined);
/**
 * 部门选择变化处理：更新选中部门并刷新列表
 * @param id 选中的部门ID，undefined 表示全部
 */
function onDeptChange(id: number | undefined) {
  selectedDeptId.value = id;
  gridApi.reload();
}

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    schema: searchFormSchema,
    collapsed: true,
    showCollapseButton: true,
  },
  gridOptions: {
    columns,
    height: '100%',
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
         * 用户分页查询
         * @param page 底层分页参数（含 pageNumber 与 pageSize）
         * @param formValues 搜索表单值
         */
        query: async ({ page }: any, formValues: any) => {
          // 合并左侧部门筛选
          const finalQuery = { ...formValues, deptId: selectedDeptId.value };
          const { list, total } = await getUserPage(finalQuery, page);
          return { list, total };
        },
      },
    },
  },
});

const [VFormDrawer, VFormDrawerApi] = useVbenDrawer({
  connectedComponent: FormDrawer,
});

/**
 * 新增用户
 * @param row 可选：基于选中行初始化表单
 */
const handleAdd = (row?: Record<string, any>) => {
  const values = { isUpdate: false };
  if (row?.deptId) (values as any).deptId = row.deptId;
  else if (selectedDeptId.value) (values as any).deptId = selectedDeptId.value;
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
const handleDelete = async (id: number | string) => {
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

//
</script>

<template>
  <Page auto-content-height>
    <div class="h-full flex gap-4">
      <!-- 左侧部门列表 -->
      <div class="w-72 h-full">
        <div class="h-full rounded-md border p-4 overflow-auto">
          <div class="mb-3 text-sm font-medium">部门列表</div>
          <DeptTree v-model:selectedId="selectedDeptId" :status="1" @change="onDeptChange" />
        </div>
      </div>

      <!-- 右侧用户表格 -->
      <div class="flex-1 h-full">
        <Grid class="h-full" table-title="用户信息" table-title-help="系统用户信息管理">
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
          @change="
            (checked: boolean | string | number) => {
              const isChecked = Boolean(checked);
              row.status = isChecked ? 1 : 0;
              handleStatusChange(row);
            }
          "
        />
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
                <div class="icon-[material-symbols--edit-square-rounded]"></div>
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
    </div>

    <VFormDrawer @success="handleSuccess" />
  </Page>
</template>
