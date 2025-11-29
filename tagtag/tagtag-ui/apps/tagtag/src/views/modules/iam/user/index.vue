<script lang="ts" setup>
import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { onMounted, ref } from 'vue';

import { Page, useVbenDrawer } from '@vben/common-ui';

import {
  Avatar as AAvatar,
  Button as AButton,
  Dropdown as ADropdown,
  Menu as AMenu,
  Popconfirm as APopconfirm,
  Switch as ASwitch,
  Tag as ATag,
  Tooltip as ATooltip,
  message,
  Modal,
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
  message.success('删除成功');
  gridApi.reload();
};

/**
 * 批量删除操作
 * 包含确认对话框、数据验证、错误处理和加载状态
 */
const handleBatchDelete = async () => {
  const selectedRows = gridApi.grid.getCheckboxRecords();
  if (selectedRows.length === 0) {
    message.warning('请选择要删除的用户');
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
      message.success(`成功删除 ${selectedRows.length} 个用户`);
      gridApi.grid.clearCheckboxRow();
      gridApi.reload();
      batchLoading.value = false;
    },
  });
};

/**
 * 状态切换操作
 * @param row 用户行数据
 */
const handleStatusToggle = async (row: Record<string, any>) => {
  const newStatus = row.status === 1 ? 0 : 1;
  const statusText = newStatus === 1 ? '启用' : '禁用';
  try {
    await updateUserStatus(row.id, newStatus);
    message.success(`用户${statusText}成功`);
    gridApi.reload();
  } catch {
    message.error(`用户${statusText}失败`);
  }
};

/**
 * 批量状态更新操作
 * 包含确认对话框、数据验证、错误处理和加载状态
 * @param status 目标状态（0-禁用，1-启用）
 */
const handleBatchStatusUpdate = async (status: number) => {
  const selectedRows = gridApi.grid.getCheckboxRecords();
  if (selectedRows.length === 0) {
    message.warning('请选择要更新状态的用户');
    return;
  }
  const needUpdateRows = selectedRows.filter(
    (row: any) => row.status !== status,
  );
  if (needUpdateRows.length === 0) {
    const statusText = status === 1 ? '启用' : '禁用';
    message.info(`选中的用户已经是${statusText}状态`);
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
        message.success(`成功${actionText} ${needUpdateRows.length} 个用户`);
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
const handleAssignRoles = async (row: Record<string, any>) => {
  try {
    const loadingMessage = message.loading('正在加载角色信息...', 0);
    selectedUserForRole.value = row;
    selectedUsersForBatchRole.value = [];
    selectedRoleIds.value = [];

    const rolesData = await getAllRoles();
    allRoles.value = rolesData || [];

    if (row && row.id) {
      const userRolesData = await getUserRoles(row.id);
      selectedRoleIds.value = userRolesData?.map((role: any) => role.id) || [];
    }

    loadingMessage();
    roleAssignModalRef.value?.openModal();
  } catch {
    message.info('角色分配暂不可用：角色查询接口不可用');
  }
};

/**
 * 批量分配角色操作
 * 为多个选中用户批量分配角色，验证选择状态并加载角色列表
 */
const handleBatchAssignRoles = async () => {
  try {
    const selectedRows = gridApi.grid.getCheckboxRecords();
    if (!selectedRows || selectedRows.length === 0) {
      message.warning('请先选择要分配角色的用户');
      return;
    }
    const loadingMessage = message.loading('正在加载角色信息...', 0);
    selectedUserForRole.value = null;
    selectedUsersForBatchRole.value = selectedRows;
    selectedRoleIds.value = [];

    const rolesData = await getAllRoles();
    allRoles.value = rolesData || [];

    loadingMessage();
    roleAssignModalRef.value?.openModal();
  } catch {
    message.info('批量分配角色暂不可用：角色查询接口不可用');
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
      message.success('角色分配成功');
    } else if (selectedUsersForBatchRole.value.length > 0) {
      const userIds = selectedUsersForBatchRole.value.map((user) => user.id);
      await batchAssignRoles(userIds, roleIds as number[]);
      message.success(
        `成功为 ${selectedUsersForBatchRole.value.length} 个用户分配角色`,
      );

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
            <div class="flex items-center gap-4">
              <div v-if="selectedDeptId" class="flex items-center gap-2">
                <ATag color="blue">已按部门筛选</ATag>
                <ATooltip title="清除部门筛选">
                  <AButton size="small" type="link" @click="handleClearDept">
                    清除
                  </AButton>
                </ATooltip>
              </div>
              <!-- 新增按钮单独放置 -->
              <AButton
                class="flex items-center px-4"
                type="primary"
                @click="handleAdd"
              >
                <template #icon>
                  <span class="icon-[material-symbols--add-circle] mr-1"></span>
                </template>
                新增
              </AButton>

              <!-- 批量操作下拉（参考菜单管理的分割样式） -->
              <ADropdown.Button :disabled="batchLoading">
                批量操作
                <template #overlay>
                  <AMenu>
                    <AMenu.Item key="delete" @click="handleBatchDelete">
                      <div
                        class="icon-[material-symbols--delete-rounded] mr-2"
                      ></div>
                      批量删除
                    </AMenu.Item>
                    <AMenu.Item
                      key="enable"
                      @click="handleBatchStatusUpdate(1)"
                    >
                      <div
                        class="icon-[material-symbols--check-circle] mr-2"
                      ></div>
                      批量启用
                    </AMenu.Item>
                    <AMenu.Item
                      key="disable"
                      @click="handleBatchStatusUpdate(0)"
                    >
                      <div class="icon-[material-symbols--cancel] mr-2"></div>
                      批量禁用
                    </AMenu.Item>
                    <AMenu.Item key="roles" @click="handleBatchAssignRoles">
                      <div
                        class="icon-[material-symbols--group-add] mr-2"
                      ></div>
                      批量分配角色
                    </AMenu.Item>
                  </AMenu>
                </template>
              </ADropdown.Button>
            </div>
          </template>
          <template #status="{ row }">
            <div class="flex items-center gap-2">
              <ASwitch
                :checked="row.status === 1"
                checked-children="启用"
                un-checked-children="禁用"
                @change="handleStatusToggle(row)"
              />
            </div>
          </template>
          <template #userCell="{ row }">
            <div class="flex items-center gap-2">
              <AAvatar size="small">
                {{ (row.username || '?').slice(0, 1).toUpperCase() }}
              </AAvatar>
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
              <ATooltip title="编辑">
                <AButton
                  class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                  ghost
                  shape="circle"
                  size="small"
                  type="primary"
                  @click="handleEdit(row)"
                >
                  <template #icon>
                    <div
                      class="icon-[material-symbols--edit-square-rounded] text-blue-500"
                    ></div>
                  </template>
                </AButton>
              </ATooltip>

              <ATooltip title="重置密码">
                <AButton
                  class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                  shape="circle"
                  size="small"
                  type="default"
                  @click="handleResetPassword(row)"
                >
                  <template #icon>
                    <div
                      class="icon-[material-symbols--lock-reset] text-orange-500"
                    ></div>
                  </template>
                </AButton>
              </ATooltip>

              <ATooltip title="分配角色">
                <AButton
                  class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                  shape="circle"
                  size="small"
                  type="default"
                  @click="handleAssignRoles(row)"
                >
                  <template #icon>
                    <div
                      class="icon-[material-symbols--person-add] text-purple-500"
                    ></div>
                  </template>
                </AButton>
              </ATooltip>

              <APopconfirm
                cancel-text="取消"
                ok-text="确定"
                placement="left"
                title="确定删除此数据?"
                @confirm="handleDelete(row.id)"
              >
                <ATooltip title="删除">
                  <AButton
                    class="flex h-7 w-7 items-center justify-center p-0 transition-transform hover:scale-110 hover:shadow-sm"
                    danger
                    ghost
                    shape="circle"
                    size="small"
                    type="primary"
                  >
                    <template #icon>
                      <div
                        class="icon-[material-symbols--delete-rounded] text-red-500"
                      ></div>
                    </template>
                  </AButton>
                </ATooltip>
              </APopconfirm>
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
