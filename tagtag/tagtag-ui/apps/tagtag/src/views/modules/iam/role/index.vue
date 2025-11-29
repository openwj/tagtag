<script lang="ts" setup>
import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { ref } from 'vue';

import { Page, useVbenDrawer } from '@vben/common-ui';

import {
  Button,
  Divider,
  Modal,
  Popconfirm,
  Switch,
  Tooltip,
  Dropdown,
  Menu,
  message,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import {
  batchDeleteRole,
  batchUpdateRoleStatus,
  deleteRole,
  getRolePage,
  updateRoleStatus,
} from '#/api/modules/iam/role';

import { columns, searchFormSchema } from './data';
import FormDrawer from './FormDrawer.vue';

const formOptions: VbenFormProps = {
  collapsed: true,
  schema: searchFormSchema,
  showCollapseButton: true,
  commonConfig: {
    labelWidth: 90,
  },
};

const gridOptions: VxeGridProps = {
  stripe: true,
  checkboxConfig: {
    highlight: true,
    // labelField: 'name',
  },
  columns,
  height: 'auto',
  keepSource: true,
  emptyText: '暂无角色数据，点击“新增”创建首个角色',
  pagerConfig: {
    enabled: true,
    pageSize: 10,
    pageSizes: [10, 20, 50, 100],
    layouts: [
      'PrevPage',
      'JumpNumber',
      'NextPage',
      'FullJump',
      'Sizes',
      'Total',
    ],
    perfect: true,
  },
  proxyConfig: {
    enabled: true, // 启用数据代理
    autoLoad: true, // 自动加载数据
    // 配置数据字段映射
    response: {
      result: 'list', // 指定数据列表字段（与后端 PageResult 保持一致）
      total: 'total', // 指定总数字段
    },
    ajax: {
      query: async ({ page }, formValues) => {
        const { list, total } = await getRolePage(formValues, page);
        return { list, total };
      },
    },
  },
  exportConfig: {
    type: 'xlsx',
    sheetName: '角色信息',
    filename: `角色信息_${new Date().toISOString().slice(0, 10)}`,
    columnFilterMethod: ({ column }: any) => column.field !== 'action',
    mode: 'current',
  },
  // importConfig: {},
  toolbarConfig: {
    custom: true,
    export: true,
    // import: true,
    refresh: true,
    zoom: true,
  },
};

const [Grid, gridApi] = useVbenVxeGrid({ formOptions, gridOptions });

// 加载状态管理
const loading = ref(false);

// 批量操作加载状态
const batchLoading = ref(false);

/**
 * 获取选中的行数据
 * @returns 选中的行数据数组
 */
const getSelectedRows = () => {
  return gridApi.grid?.getCheckboxRecords() || [];
};

/**
 * 状态切换（乐观更新，失败回滚）
 * @param row 行数据
 * @param checked 新状态
 */
const handleStatusChange = async (row: any, checked: boolean) => {
  const prevStatus = row.status;
  // 乐观更新到目标状态
  row.status = checked ? 1 : 0;
  row.statusLoading = true;
  try {
    // API的disabled参数：true表示禁用，false表示启用
    // checked为true表示启用，所以disabled应该为!checked
    await updateRoleStatus(row.id, !checked);
    message.success({ content: '状态更新成功', duration: 2 });
  } catch {
    // 失败回滚到原状态
    row.status = prevStatus;
  } finally {
    row.statusLoading = false;
  }
};

/**
 * 批量删除角色
 */
const handleBatchDelete = async () => {
  const selectedRows = getSelectedRows();
  if (selectedRows.length === 0) {
    message.warning({ content: '请选择要删除的角色', duration: 3 });
    return;
  }

  batchLoading.value = true;
  try {
    const roleIds = selectedRows.map((row: any) => row.id);
    await batchDeleteRole(roleIds);
    message.success({ content: `成功删除 ${selectedRows.length} 个角色`, duration: 2 });
    gridApi.grid?.clearCheckboxRow();
    gridApi.reload();
  } finally {
    batchLoading.value = false;
  }
};

/**
 * 批量启用角色
 */
const handleBatchEnable = async () => {
  const selectedRows = getSelectedRows();
  if (selectedRows.length === 0) {
    message.warning({ content: '请选择要启用的角色', duration: 3 });
    return;
  }

  batchLoading.value = true;
  try {
    const roleIds = selectedRows.map((row: any) => row.id);
    // API的disabled参数：false表示启用
    await batchUpdateRoleStatus(roleIds, false);
    message.success({ content: `成功启用 ${selectedRows.length} 个角色`, duration: 2 });
    gridApi.grid?.clearCheckboxRow();
    gridApi.reload();
  } finally {
    batchLoading.value = false;
  }
};

/**
 * 批量禁用角色
 */
const handleBatchDisable = async () => {
  const selectedRows = getSelectedRows();
  if (selectedRows.length === 0) {
    message.warning({ content: '请选择要禁用的角色', duration: 3 });
    return;
  }

  batchLoading.value = true;
  try {
    const roleIds = selectedRows.map((row: any) => row.id);
    // API的disabled参数：true表示禁用
    await batchUpdateRoleStatus(roleIds, true);
    message.success({ content: `成功禁用 ${selectedRows.length} 个角色`, duration: 2 });
    gridApi.grid?.clearCheckboxRow();
    gridApi.reload();
  } finally {
    batchLoading.value = false;
  }
};

const [VFormDrawer, VFormDrawerApi] = useVbenDrawer({
  // 连接抽离的组件
  connectedComponent: FormDrawer,
});

const handleAdd = () => {
  // 处理新增
  VFormDrawerApi.setData({
    // 表单值
    values: { isUpdate: false },
  });
  VFormDrawerApi.open();
};

/**
 * 删除角色
 * @param id 角色ID
 */
const handleDelete = async (id: string) => {
  loading.value = true;
  try {
    await deleteRole(id);
    message.success({ content: '角色删除成功', duration: 2 });
    gridApi.reload();
  } finally {
    loading.value = false;
  }
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
    <Grid table-title="角色信息" table-title-help="系统角色信息">
      <template #toolbar-tools>
        <div class="flex items-center gap-2">
          <Button
            class="flex items-center px-2"
            type="primary"
            v-access:code="'role:create'"
            @click="handleAdd"
          >
            <template #icon>
              <span class="icon-[lucide--plus] mr-1"></span>
            </template>
            新增
          </Button>
          <Dropdown.Button class="px-2" :disabled="batchLoading">
            批量操作
            <template #icon>
              <span class="icon-[lucide--chevrons-down]"></span>
            </template>
            <template #overlay>
              <Menu>
                <Menu.Item
                  key="enable"
                  v-access:code="'role:update'"
                  @click="
                    () =>
                      Modal.confirm({
                        title: '批量启用',
                        content: '确定要启用选中的角色吗？',
                        okText: '确定',
                        cancelText: '取消',
                        onOk: handleBatchEnable,
                      })
                  "
                >
                  <span class="icon-[lucide--check-circle] mr-1"></span>
                  启用
                </Menu.Item>
                <Menu.Item
                  key="disable"
                  v-access:code="'role:update'"
                  @click="
                    () =>
                      Modal.confirm({
                        title: '批量禁用',
                        content: '确定要禁用选中的角色吗？',
                        okText: '确定',
                        cancelText: '取消',
                        onOk: handleBatchDisable,
                      })
                  "
                >
                  <span class="icon-[lucide--x-circle] mr-1"></span>
                  禁用
                </Menu.Item>
                <Menu.Item
                  key="delete"
                  v-access:code="'role:delete'"
                  @click="
                    () =>
                      Modal.confirm({
                        title: '批量删除',
                        content: '确定要删除选中的角色吗？',
                        okText: '确定',
                        cancelText: '取消',
                        onOk: handleBatchDelete,
                      })
                  "
                >
                  <span class="icon-[lucide--trash-2] mr-1"></span>
                  删除
                </Menu.Item>
              </Menu>
            </template>
          </Dropdown.Button>
        </div>
      </template>
      <template #name="{ row }">
        <div class="flex items-center gap-2">
          <span class="icon-[lucide--user-circle] text-blue-500"></span>
          <span>{{ row.name }}</span>
        </div>
      </template>

      <template #roleType="{ row }">
        <Button v-if="row.roleType === 1" type="primary" size="small">
          <template #icon>
            <span class="icon-[lucide--shield] mr-1"></span>
          </template>
          系统角色
        </Button>

        <Button v-else-if="row.roleType === 2" type="default" size="small">
          <template #icon>
            <span class="icon-[lucide--briefcase] mr-1"></span>
          </template>
          业务角色
        </Button>

        <Button v-else size="small">
          <template #icon>
            <span class="icon-[lucide--help-circle] mr-1"></span>
          </template>
          未知
        </Button>
      </template>

      <template #status="{ row }">
        <Switch
          v-access:code="'role:update'"
          :checked="row.status === 1"
          :loading="row.statusLoading"
          :disabled="row.statusLoading"
          checked-children="启用"
          un-checked-children="禁用"
          @change="
            (checked: any) =>
              handleStatusChange(row, checked === true || checked === 'true')
          "
        />
      </template>
      <template #action="{ row }">
        <div class="flex items-center justify-center gap-0.5">
          <Tooltip title="编辑角色">
            <Button
              class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
              ghost
              shape="circle"
              size="small"
              type="primary"
              v-access:code="'role:update'"
              @click="handleEdit(row)"
              aria-label="编辑角色"
            >
              <template #icon>
                <div class="icon-[lucide--edit] text-xs"></div>
              </template>
            </Button>
          </Tooltip>

          <Divider type="vertical" class="mx-1 h-4" />

          <Popconfirm
            cancel-text="取消"
            ok-text="确定"
            placement="left"
            :title="`确定要删除角色 '${row.name}' 吗？`"
            @confirm="handleDelete(row.id)"
          >
            <Tooltip title="删除角色">
              <Button
                class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                danger
                ghost
                shape="circle"
                size="small"
                type="primary"
                v-access:code="'role:delete'"
                :loading="loading"
                aria-label="删除角色"
              >
                <template #icon>
                  <div class="icon-[lucide--trash-2] text-xs"></div>
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
