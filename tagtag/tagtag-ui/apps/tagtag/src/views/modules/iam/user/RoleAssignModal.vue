<script setup lang="ts">
import { computed, ref, watch } from 'vue';

// import { SearchOutlined } from '@ant-design/icons-vue';
import { useVbenModal } from '@vben/common-ui';

import { Alert, Spin, Tag, Transfer } from 'ant-design-vue';

interface Props {
  user?: null | Record<string, any>;
  userCount?: number;
  roles: Record<string, any>[];
  assignedRoleIds?: (number | string)[];
  loading?: boolean;
}

interface Emits {
  (e: 'confirm', roleIds: (number | string)[]): void;
  (e: 'cancel'): void;
}

const props = withDefaults(defineProps<Props>(), {
  user: null,
  userCount: 0,
  assignedRoleIds: () => [],
  loading: false,
});

const emit = defineEmits<Emits>();

// 使用 VbenModal
const [Modal, modalApi] = useVbenModal({
  onConfirm: handleConfirm,
  onCancel: handleCancel,
});

// 响应式数据
const searchKeyword = ref('');
const selectedRoleIds = ref<string[]>([]);
// 简化：直接使用原始关键词进行筛选

// 计算属性
const isBatch = computed(() => !props.user && props.userCount > 0);

const modalTitle = computed(() => {
  return isBatch.value ? '批量分配角色' : '分配角色';
});

/**
 * 角色数据增强：标注已分配状态并统一ID类型为字符串
 */
const rolesWithAssignedStatus = computed(() => {
  const assignedSet = new Set((props.assignedRoleIds || []).map(String));
  return (props.roles || []).map((role: any) => {
    const idStr = String(role.id);
    return { ...role, id: idStr, isAssigned: assignedSet.has(idStr) };
  });
});

/**
 * 角色筛选（简化版）：仅按名称模糊匹配
 */
const filteredRoles = computed(() => {
  const kw = (searchKeyword.value || '').toLowerCase();
  const list = rolesWithAssignedStatus.value;
  if (!kw) return list;
  return list.filter((role: any) => {
    const name = (role.name || '').toLowerCase();
    return name.includes(kw);
  });
});

const transferData = computed(() => {
  return (rolesWithAssignedStatus.value || []).map((role: any) => ({
    key: String(role.id),
    title: role.name,
    description: role.description || '',
    isAssigned: role.isAssigned,
  }));
});

// 简化：总是允许提交，由服务端覆盖式分配处理差异

/**
 * 同步已分配角色（仅在弹框打开时）
 * 监听 props.assignedRoleIds 与 props.roles 变化，统一为字符串并过滤无效ID
 */
const modalState = modalApi.useStore?.();
watch(
  [
    () => modalState?.value?.isOpen,
    () => props.assignedRoleIds,
    () => props.roles,
  ],
  () => {
    if (!modalState?.value?.isOpen) return;
    const ids = (props.assignedRoleIds || []).map(String);
    const valid = new Set((props.roles || []).map((r: any) => String(r.id)));
    selectedRoleIds.value = ids.filter((id) => valid.has(id)).map(String);
  },
  { immediate: true },
);

// 方法
/**
 * 打开模态框
 * 重置搜索状态并设置已分配角色为选中状态
 * @returns {void}
 */
const openModal = () => {
  // 重置搜索关键词
  searchKeyword.value = '';
  // 设置已分配的角色为选中状态
  selectedRoleIds.value = (props.assignedRoleIds || []).map(String);

  modalApi.open();
};

/**
 * 关闭模态框
 */
const closeModal = () => {
  modalApi.close();
};

/**
 * 全选当前筛选的角色
 */
// 采用内置 Transfer 搜索与选择，不再保留自定义全选/清空/反选逻辑

/**
 * 确认分配角色
 * 向父组件发送选中的角色ID列表
 * @returns {void}
 */
function handleConfirm() {
  emit('confirm', selectedRoleIds.value);
}

/**
 * 取消分配角色
 * 向父组件发送取消事件并关闭模态框
 * @returns {void}
 */
function handleCancel() {
  emit('cancel');
  closeModal();
}

/**
 * 分配成功后自动关闭模态框
 * 供父组件调用，用于在角色分配成功后关闭模态框
 */
const handleAssignSuccess = () => {
  closeModal();
};

// 暴露方法给父组件
defineExpose({
  openModal,
  closeModal,
  handleAssignSuccess,
});
</script>

<template>
  <Modal
    v-bind="$attrs"
    :title="modalTitle"
    class="!min-w-[750px]"
    :body-style="{ padding: '16px 20px' }"
    :confirm-loading="loading"
    @confirm="handleConfirm"
    @cancel="handleCancel"
  >
    <!-- 用户信息显示 -->
    <div v-if="!isBatch && user" class="mb-4">
      <Alert type="info" show-icon>
        <template #message>
          <span class="font-medium">正在为用户 "{{ user.username }}" 分配角色</span>
        </template>
        <template #description>
          <div class="mt-2 grid grid-cols-2 gap-x-8 gap-y-2 text-sm">
            <div class="flex items-center gap-2">
              <span class="text-gray-500">姓名：</span>
              <span>{{ user.nickname }}</span>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-gray-500">部门：</span>
              <Tag v-if="user.deptName" color="purple" :bordered="false">{{
                user.deptName
              }}</Tag>
              <span v-else class="text-gray-400">-</span>
            </div>
          </div>
        </template>
      </Alert>
    </div>

    <!-- 批量用户提示 -->
    <div v-if="isBatch" class="mb-4">
      <Alert type="warning" show-icon>
        <template #message>
          <span class="font-medium">批量分配模式</span>
        </template>
        <template #description>
          <div class="mt-1">
            已选中 <span class="font-bold text-primary">{{ userCount }}</span> 个用户，
            <span class="text-xs text-gray-500">
              所有选中用户的角色将被重置为以下选择的角色。
            </span>
          </div>
        </template>
      </Alert>
    </div>

    <!-- 内置搜索由 Transfer 提供 -->

    <!-- 双列选择 -->

    <!-- 角色列表 -->
    <div class="max-h-96 overflow-y-auto rounded border border-gray-200 p-2">
      <!-- 加载状态 -->
      <div
        v-if="loading"
        class="flex flex-col items-center justify-center py-12"
      >
        <Spin size="large" class="mb-3" />
        <div class="text-sm text-gray-500">正在加载角色列表...</div>
      </div>

      <!-- 角色列表内容 -->
      <div v-else>
        <!-- 无角色提示 -->
        <div
          v-if="filteredRoles.length === 0"
          class="py-8 text-center text-gray-500"
        >
          <div class="mb-2">
            <span class="icon-[material-symbols--search-off] text-2xl"></span>
          </div>
          <div>
            {{ searchKeyword ? '未找到匹配的角色' : '暂无可分配的角色' }}
          </div>
        </div>

        <!-- 角色选择双列 -->
        <Transfer
          v-else
          :data-source="transferData"
          v-model:target-keys="selectedRoleIds"
          :show-search="true"
          :list-style="{ width: '50%', minHeight: '320px' }"
          :titles="['可选角色', '已选角色']"
          :render="(item: any) => item.title"
        />
      </div>
    </div>

    <!-- 选中统计 -->
    <div class="mt-3 text-sm text-gray-600">
      已选择 {{ selectedRoleIds.length }} 个角色
    </div>
  </Modal>
</template>
