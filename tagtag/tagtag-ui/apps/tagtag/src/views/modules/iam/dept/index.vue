<script lang="ts" setup>
import { Page, useVbenDrawer } from '@vben/common-ui';

import {
  Button,
  message,
  Popconfirm,
  Switch,
  Tag,
  Tooltip,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { deleteDept, getDeptTree, updateDeptStatus } from '#/api/modules/iam/dept';
// 注：不再依赖分页封装，页面内直接适配后端字段

import { columns, searchFormSchema } from './data';
import FormDrawer from './FormDrawer.vue';

const [Grid, gridApi] = useVbenVxeGrid({
  formOptions: {
    // 默认折叠搜索表单
    schema: searchFormSchema,
    collapsed: true,
    showCollapseButton: true,
  },
  gridOptions: {
    columns,
    height: 'auto',
    columnConfig: {
      minWidth: 120,
    },
    showOverflow: 'tooltip',
    pagerConfig: {
      enabled: false,
    },
    rowConfig: {
      keyField: 'id',
    },
    treeConfig: {
      // 树形结构配置
      childrenField: 'children',
      hasChild: 'hasChildren',
      expandAll: true,
      rowField: 'id',
    },
    toolbarConfig: {
      // 工具栏配置
      custom: true,
      export: true,
      refresh: true,
      zoom: true,
    },
    proxyConfig: {
      ajax: {
        query: async (_page: any, formValues: any) => {
          const data = await getDeptTree(formValues);

          return { items: data };
        },
      },
    },
  },
});

const [VFormDrawer, VFormDrawerApi] = useVbenDrawer({
  // 连接抽离的组件
  connectedComponent: FormDrawer,
});

const handleAdd = (row: Record<string, any>) => {
  const values = { isUpdate: false, parentId: 0 };
  if (row?.id) {
    values.parentId = row.id;
  }
  // 处理新增
  VFormDrawerApi.setData({
    // 表单值
    values,
  });
  VFormDrawerApi.open();
};

/**
 * 部门状态切换（Promise 风格，无 try/catch）
 * @param row 部门行数据
 * @param checked 目标状态（true=启用，false=禁用）
 */
const handleStatusChange = (
  row: { id: number | string; status: number; statusLoading?: boolean },
  checked: boolean,
) => {
  row.statusLoading = true;
  const target = checked ? 1 : 0;
  return updateDeptStatus(row.id, target)
    .then(() => {
      row.status = target;
      message.success({ content: '状态更新成功', duration: 2 });
      // 可选：如需强一致性，可刷新表格
      // gridApi.query();
    })
    .finally(() => {
      row.statusLoading = false;
    });
};

/**
 * 删除部门
 * @param id 部门ID
 */
const handleDelete = async (id: string) => {
  await deleteDept(id);
  message.success({ content: '删除成功', duration: 2 });
  await gridApi.query();
};

const handleEdit = (row: Record<string, any>) => {
  // 处理编辑
  VFormDrawerApi.setData({
    // 表单值
    values: { ...row, isUpdate: true },
  });
  VFormDrawerApi.open();
};

const handleSuccess = () => {
  gridApi.reload();
};
</script>

<template>
  <Page auto-content-height>
    <Grid
      :all-tree-expand="true"
      table-title="部门信息"
      table-title-help="公司组织架构信息"
    >
      <template #toolbar-tools>
        <div class="flex items-center gap-3">
          <Button
            class="flex items-center px-2"
            type="primary"
            v-access:code="'dept:create'"
            @click="handleAdd"
          >
            <template #icon>
              <span class="icon-[lucide--plus] mr-1"></span>
            </template>
            新增
          </Button>
        </div>
      </template>

      <template #name="{ row }">
        <span class="font-medium">{{ row.name }}</span>
      </template>

      <template #code="{ row }">
        <Tag color="purple" :bordered="false" class="font-mono">
          {{ row.code }}
        </Tag>
      </template>

      <template #status="{ row }">
        <Switch
          v-access:code="'dept:update'"
          :checked="row.status === 1"
          :loading="row.statusLoading"
          :disabled="row.statusLoading"
          checked-children="启用"
          un-checked-children="禁用"
          @change="
            (checked: boolean | string | number) =>
              handleStatusChange(row, Boolean(checked))
          "
        />
      </template>

      <template #createTime="{ row }">
        <span class="text-gray-500">{{ row.createTime?.replace('T', ' ') }}</span>
      </template>

      <template #action="{ row }">
        <div class="flex items-center justify-center gap-1.5">
          <Tooltip title="新增子部门">
            <Button
              class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
              shape="circle"
              size="small"
              type="primary"
              v-access:code="'dept:create'"
              @click="handleAdd(row)"
            >
              <template #icon>
                <span class="icon-[lucide--plus] text-white"></span>
              </template>
            </Button>
          </Tooltip>

          <Tooltip title="编辑">
            <Button
              class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
              ghost
              shape="circle"
              size="small"
              type="primary"
              v-access:code="'dept:update'"
              @click="handleEdit(row)"
            >
              <template #icon>
                <span class="icon-[lucide--edit] text-blue-500"></span>
              </template>
            </Button>
          </Tooltip>

          <Popconfirm
            cancel-text="取消"
            ok-text="确定"
            placement="left"
            title="确定删除此数据?"
            @confirm="handleDelete(row.id)"
          >
            <Tooltip title="删除">
              <Button
                class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                danger
                ghost
                shape="circle"
                size="small"
                type="primary"
                v-access:code="'dept:delete'"
              >
                <template #icon>
                  <span class="icon-[lucide--trash-2] text-red-500"></span>
                </template>
              </Button>
            </Tooltip>
          </Popconfirm>
        </div>
      </template>
    </Grid>

    <VFormDrawer @success="handleSuccess" />
  </Page>
</template>
