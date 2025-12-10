<script lang="ts" setup>
import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { onMounted, ref } from 'vue';

import { Page, useVbenDrawer } from '@vben/common-ui';

import {
  Avatar,
  Button,
  Dropdown,
  Menu,
  message,
  Modal,
  Popconfirm,
  Switch,
  Tag,
  Tooltip,
} from 'ant-design-vue';

import { useVbenVxeGrid } from '#/adapter/vxe-table';
import { getDeptTree } from '#/api/modules/iam/dept';
import { getAllRoles } from '#/api/modules/iam/role';
import {
  assignUserRoles,
  batchAssignRoles,
  batchDeleteUsers,
  batchUpdateUserStatus,
  deleteUser,
  getUserPage,
  getUserRoles,
  resetUserPassword,
  updateUserStatus,
} from '#/api/modules/iam/user';

import { columns, deptTreeData, searchFormSchema } from './data';
import DeptTree from './DeptTree.vue';
import FormDrawer from './FormDrawer.vue';
import RoleAssignModal from './RoleAssignModal.vue';

// 响应式状态变量：当前选中的部门ID
const selectedDeptId = ref<string>('');
// 部门数据加载状态
const deptLoading = ref<boolean>(false);
// 批量操作加载状态
const batchLoading = ref<boolean>(false);

// 角色分配对话框状态
const roleAssignLoading = ref(false);
const selectedUserForRole = ref<null | Record<string, any>>(null);
const selectedUsersForBatchRole = ref<Record<string, any>[]>([]);
const allRoles = ref<any[]>([]);
const selectedRoleIds = ref<(number | string)[]>([]);
// 角色分配模态框引用
const roleAssignModalRef = ref();
const deptTreeRef = ref();

// 取消筛选条件的前端缓存

const formOptions: VbenFormProps = {
  schema: searchFormSchema,
  collapsed: true,
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
  emptyText: '暂无用户数据，点击“新增”创建首个用户',
  scrollY: { enabled: true, gt: 100 },
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
    // 配置数据字段映射（与角色页保持一致）
    response: {
      result: 'list',
      total: 'total',
    },
    ajax: {
      query: async ({ page }, formValues) => {
        const query = selectedDeptId.value
          ? { ...formValues, deptId: selectedDeptId.value }
          : formValues || {};
        const { list, total } = await getUserPage(query, page);
        return { list, total };
      },
    },
  },
  exportConfig: {},
  toolbarConfig: {
    custom: true,
    export: true,
    refresh: true,
    zoom: true,
  },
};

const [Grid, gridApi] = useVbenVxeGrid({ formOptions, gridOptions });

/**
 * 加载数据
 */
const loadData = async () => {
  gridApi.reload();
};

/**
 * 加载部门树数据
 */
const loadDeptData = async () => {
  try {
    deptLoading.value = true;
    const deptTree = await getDeptTree({});

    // 直接更新响应式的部门数据变量
    deptTreeData.value = deptTree;
  } finally {
    deptLoading.value = false;
  }
};

/**
 * 组件挂载时加载部门数据
 */
onMounted(() => {
  loadDeptData();
});

const [VFormDrawer, VFormDrawerApi] = useVbenDrawer({
  // 连接抽离的组件
  connectedComponent: FormDrawer,
});

/**
 * 处理新增用户操作
 * 如果左侧选中了部门，会自动将部门ID设置为表单默认值
 */
const handleAdd = () => {
  // 构建表单默认值
  const defaultValues: Record<string, any> = { isUpdate: false };

  // 如果选中了部门，将部门ID设置为默认值
  if (selectedDeptId.value) {
    defaultValues.deptId = selectedDeptId.value;
  }

  // 处理新增
  VFormDrawerApi.setData({
    // 表单值
    values: defaultValues,
  });
  VFormDrawerApi.open();
};

/**
 * 单个删除操作
 * @param id 用户ID
 */
const handleDelete = async (id: string) => {
  await deleteUser(id);
  message.success({ content: '删除成功', duration: 2 });
  gridApi.reload();
};

/**
 * 批量删除操作
 * 包含确认对话框、数据验证、错误处理和加载状态
 */
const handleBatchDelete = async () => {
  const selectedRows = gridApi.grid.getCheckboxRecords();
  if (selectedRows.length === 0) {
    message.warning({ content: '请选择要删除的用户', duration: 3 });
    return;
  }
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除选中的 ${selectedRows.length} 个用户吗？此操作不可恢复。`,
    okText: '确定',
    cancelText: '取消',
    type: 'warning',
    onOk: async () => {
      if (batchLoading.value) return;
      batchLoading.value = true;
      const ids = selectedRows.map((row: any) => row.id);
      await batchDeleteUsers(ids);
      message.success({
        content: `成功删除 ${selectedRows.length} 个用户`,
        duration: 2,
      });
      gridApi.grid.clearCheckboxRow();
      gridApi.reload();
      batchLoading.value = false;
    },
  });
};


/**
 * 状态切换（失败仅回滚，不重复错误提示）
 * @param row 用户行数据
 */
const handleStatusToggle = (row: Record<string, any>) => {
  const prevStatus = row.status;
  const newStatus = row.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? '启用' : '禁用';
  row.statusLoading = true;
  return updateUserStatus(row.id, newStatus)
    .then(() => {
      row.status = newStatus;
      message.success({ content: `用户${statusText}成功`, duration: 2 });
    })
    .catch(() => {
      row.status = prevStatus;
    })
    .finally(() => {
      row.statusLoading = false;
    });
};

/**
 * 批量状态更新操作
 * 包含确认对话框、数据验证、错误处理和加载状态
 * @param status 目标状态（0-禁用，1-启用）
 */
const handleBatchStatusUpdate = async (status: number) => {
  const selectedRows = gridApi.grid.getCheckboxRecords();
  if (selectedRows.length === 0) {
    message.warning({ content: '请选择要更新状态的用户', duration: 3 });
    return;
  }
  const needUpdateRows = selectedRows.filter(
    (row: any) => row.status !== status,
  );
  if (needUpdateRows.length === 0) {
    const statusText = status === 1 ? '启用' : '禁用';
    message.info({ content: `选中的用户已经是${statusText}状态`, duration: 2 });
    return;
  }
  const actionText = status === 1 ? '启用' : '禁用';
  Modal.confirm({
    title: `确认${actionText}`,
    content: `确定要${actionText}选中的 ${needUpdateRows.length} 个用户吗？`,
    okText: '确定',
    cancelText: '取消',
    type: 'info',
    onOk: async () => {
      if (batchLoading.value) return;
      try {
        batchLoading.value = true;
        const ids = needUpdateRows.map((row: any) => row.id);
        await batchUpdateUserStatus(ids, status);
        message.success({
          content: `成功${actionText} ${needUpdateRows.length} 个用户`,
          duration: 2,
        });
        gridApi.grid.clearCheckboxRow();
        gridApi.reload();
      } finally {
        batchLoading.value = false;
      }
    },
  });
};

/**
 * 重置密码操作
 * 包含二次确认、加载状态、错误处理和友好提示
 * @param row 用户行数据
 */
const handleResetPassword = async (row: Record<string, any>) => {
  Modal.confirm({
    title: '确认重置密码',
    content: `确定要重置用户 "${row.username || row.name}" 的密码吗？\n\n重置后的默认密码为：123456\n\n请提醒用户及时修改密码以确保账户安全。`,
    okText: '确认重置',
    cancelText: '取消',
    type: 'warning',
    onOk: async () => {
      const loadingMessage = message.loading('正在重置密码...', 0);
      const defaultPassword = '123456';
      await resetUserPassword(row.id, defaultPassword);
      loadingMessage();
      message.success({
        content: '密码重置成功！默认密码：123456',
        duration: 5,
      });
      Modal.success({
        title: '密码重置成功',
        content: `用户 "${row.username || row.name}" 的密码已重置为：123456\n\n请及时通知用户修改密码。`,
        okText: '知道了',
      });
    },
  });
};

/**
 * 打开角色选择对话框
 * 为单个用户分配角色，加载角色列表和用户已分配角色
 * @param row 用户行数据
 */
/**
 * 打开角色分配对话框（加载角色与用户角色）
 */
const handleAssignRoles = async (row: Record<string, any>) => {
  const loadingMessage = message.loading('正在加载角色信息...', 0);
  try {
    selectedUserForRole.value = row;
    selectedUsersForBatchRole.value = [];
    selectedRoleIds.value = [];

    const rolesData = await getAllRoles();
    allRoles.value = rolesData || [];

    if (row && row.id) {
      const userRolesData = await getUserRoles(row.id);
      selectedRoleIds.value = userRolesData?.map((role: any) => role.id) || [];
    }

    roleAssignModalRef.value?.openModal();
  } finally {
    loadingMessage();
  }
};

/**
 * 批量分配角色操作
 * 为多个选中用户批量分配角色，验证选择状态并加载角色列表
 */
/**
 * 批量分配角色（加载角色列表）
 */
const handleBatchAssignRoles = async () => {
  const selectedRows = gridApi.grid.getCheckboxRecords();
  if (!selectedRows || selectedRows.length === 0) {
    message.warning({ content: '请先选择要分配角色的用户', duration: 3 });
    return;
  }
  const loadingMessage = message.loading('正在加载角色信息...', 0);
  try {
    selectedUserForRole.value = null;
    selectedUsersForBatchRole.value = selectedRows;
    selectedRoleIds.value = [];

    const rolesData = await getAllRoles();
    allRoles.value = rolesData || [];

    roleAssignModalRef.value?.openModal();
  } finally {
    loadingMessage();
  }
};

/**
 * 确认分配角色
 * 根据当前操作模式（单个或批量）执行角色分配
 * @param roleIds 选中的角色ID数组
 */
const handleConfirmAssignRoles = async (roleIds: (number | string)[]) => {
  try {
    roleAssignLoading.value = true;

    if (selectedUserForRole.value) {
      await assignUserRoles(selectedUserForRole.value.id, roleIds as number[]);
      message.success({ content: '角色分配成功', duration: 2 });
    } else if (selectedUsersForBatchRole.value.length > 0) {
      const userIds = selectedUsersForBatchRole.value.map((user) => user.id);
      await batchAssignRoles(userIds, roleIds as number[]);
      message.success({
        content: `成功为 ${selectedUsersForBatchRole.value.length} 个用户分配角色`,
        duration: 2,
      });

      gridApi.grid.clearCheckboxRow();
    }

    // 角色分配成功后自动关闭模态框
    roleAssignModalRef.value?.handleAssignSuccess();

    // 重置状态
    handleCancelAssignRoles();

    // 刷新用户列表
    await loadData();
  } finally {
    roleAssignLoading.value = false;
  }
};

/**
 * 取消分配角色
 * 重置角色分配相关的状态变量
 */
const handleCancelAssignRoles = () => {
  selectedUserForRole.value = null;
  selectedUsersForBatchRole.value = [];
  selectedRoleIds.value = [];
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

/**
 * 重置筛选：清空本地保存的筛选项并重载
 */
// 去掉“重置筛选”方法

/**
 * 处理部门选择事件
 * 根据选中的部门过滤用户列表
 * @param deptId 部门ID，默认为空字符串表示显示所有部门用户
 */
function handleSelect(deptId = '') {
  // 更新选中的部门ID状态
  selectedDeptId.value = deptId;
  // 重新加载表格数据
  gridApi.reload();
}

function handleClearDept() {
  // 清空部门树选中并重置筛选
  // 使用可选链避免空引用
  deptTreeRef.value?.clearSelection?.();
  handleSelect('');
}
</script>

<template>
  <div>
    <Page auto-content-height content-class="flex gap-3">
      <div class="w-1/6">
        <DeptTree ref="deptTreeRef" @select="handleSelect" />
      </div>
      <div class="w-5/6">
        <Grid table-title="用户信息" table-title-help="系统用户信息">
          <template #toolbar-tools>
            <div class="flex items-center gap-3">
              <div v-if="selectedDeptId" class="flex items-center gap-2">
                <Tag color="blue">已按部门筛选</Tag>
                <Tooltip title="清除部门筛选">
                  <Button size="small" type="link" @click="handleClearDept">
                    清除
                  </Button>
                </Tooltip>
              </div>
              <!-- 新增按钮单独放置 -->
              <Button
                class="flex items-center px-4"
                type="primary"
                v-access:code="'user:create'"
                @click="handleAdd"
                aria-label="新增用户"
              >
                <template #icon>
                  <span class="icon-[lucide--plus] mr-1"></span>
                </template>
                新增
              </Button>

              <!-- 批量操作下拉（参考菜单管理的分割样式） -->
              <Dropdown.Button :disabled="batchLoading">
                批量操作
                <template #overlay>
                  <Menu>
                    <Menu.Item
                      key="delete"
                      v-access:code="'user:delete'"
                      @click="handleBatchDelete"
                    >
                      <span class="icon-[lucide--trash-2] mr-2"></span>
                      批量删除
                    </Menu.Item>
                    <Menu.Item
                      key="enable"
                      v-access:code="'user:update'"
                      @click="handleBatchStatusUpdate(1)"
                    >
                      <span class="icon-[lucide--check-circle] mr-2"></span>
                      批量启用
                    </Menu.Item>
                    <Menu.Item
                      key="disable"
                      v-access:code="'user:update'"
                      @click="handleBatchStatusUpdate(0)"
                    >
                      <span class="icon-[lucide--x-circle] mr-2"></span>
                      批量禁用
                    </Menu.Item>
                    <Menu.Item
                      key="roles"
                      v-access:code="'user:assignRole'"
                      @click="handleBatchAssignRoles"
                    >
                      <span class="icon-[lucide--users] mr-2"></span>
                      批量分配角色
                    </Menu.Item>
                  </Menu>
                </template>
              </Dropdown.Button>
            </div>
          </template>
          <template #status="{ row }">
            <div class="flex items-center gap-2">
              <Switch
                v-access:code="'user:update'"
                :checked="row.status === 1"
                :disabled="row.statusLoading"
                :loading="row.statusLoading"
                checked-children="启用"
                un-checked-children="禁用"
                @change="handleStatusToggle(row)"
              />
            </div>
          </template>

          <template #gender="{ row }">
            <Tag v-if="row.gender === 1" color="blue" :bordered="false">
              <span class="icon-[lucide--user] mr-1 align-text-bottom"></span>男
            </Tag>
            <Tag v-else-if="row.gender === 2" color="pink" :bordered="false">
              <span class="icon-[lucide--user-check] mr-1 align-text-bottom"></span>女
            </Tag>
            <Tag v-else color="default" :bordered="false">
              <span class="icon-[lucide--help-circle] mr-1 align-text-bottom"></span>未知
            </Tag>
          </template>

          <template #deptName="{ row }">
            <Tag v-if="row.deptName" color="purple" :bordered="false">
              {{ row.deptName }}
            </Tag>
            <span v-else class="text-gray-400">-</span>
          </template>

          <template #lastLoginTime="{ row }">
            <span class="text-gray-500">{{
              row.lastLoginTime?.replace('T', ' ') || '-'
            }}</span>
          </template>

          <template #createTime="{ row }">
            <span class="text-gray-500">{{
              row.createTime?.replace('T', ' ')
            }}</span>
          </template>

          <template #userCell="{ row }">
            <div class="flex items-center gap-2">
              <Avatar
                size="small"
                class="bg-primary/10 text-primary border-primary/20 border"
              >
                {{ (row.nickname || row.username || '?').slice(0, 1).toUpperCase() }}
              </Avatar>
              <div class="flex flex-col leading-tight">
                <span class="text-sm font-medium">{{ row.username }}</span>
                <span class="text-xs text-gray-500 dark:text-gray-400">{{
                  row.nickname
                }}</span>
              </div>
            </div>
          </template>
          <template #action="{ row }">
            <div class="flex items-center justify-center gap-1.5">
              <Tooltip title="编辑">
                <Button
                  class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                  ghost
                  shape="circle"
                  size="small"
                  type="primary"
                  v-access:code="'user:update'"
                  @click="handleEdit(row)"
                  aria-label="编辑用户"
                >
                  <template #icon>
                    <span class="icon-[lucide--edit] text-blue-500"></span>
                  </template>
                </Button>
              </Tooltip>

              <Tooltip title="重置密码">
                <Button
                  class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                  shape="circle"
                  size="small"
                  type="default"
                  v-access:code="'user:update'"
                  @click="handleResetPassword(row)"
                  aria-label="重置密码"
                >
                  <template #icon>
                    <span class="icon-[lucide--lock] text-orange-500"></span>
                  </template>
                </Button>
              </Tooltip>

              <Tooltip title="分配角色">
                <Button
                  class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                  shape="circle"
                  size="small"
                  type="default"
                  v-access:code="'user:assignRole'"
                  @click="handleAssignRoles(row)"
                  aria-label="分配角色"
                >
                  <template #icon>
                    <span class="icon-[lucide--user-plus] text-purple-500"></span>
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
                    v-access:code="'user:delete'"
                    aria-label="删除用户"
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
      </div>
    </Page>

    <!-- 角色分配对话框 -->
    <RoleAssignModal
      ref="roleAssignModalRef"
      :user="selectedUserForRole"
      :user-count="selectedUsersForBatchRole.length"
      :roles="allRoles"
      :assigned-role-ids="selectedRoleIds"
      :loading="roleAssignLoading"
      @confirm="handleConfirmAssignRoles"
      @cancel="handleCancelAssignRoles"
    />
  </div>
</template>
