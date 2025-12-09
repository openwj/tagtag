<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { Alert, message } from 'ant-design-vue';

import { assignRoleMenus, listRoleMenuIds } from '#/api/modules/iam/role';

import PermissionTree from './PermissionTree.vue';

const emit = defineEmits(['success']);

const roleId = ref<number | string>('');
const roleName = ref<string>('');
const checkedMenuIds = ref<string[]>([]);

const [Modal, modalApi] = useVbenModal({
  title: '分配权限',
  draggable: true,
  onConfirm: async () => {
    try {
      modalApi.setState({ confirmLoading: true });
      const menuIds = checkedMenuIds.value.map(Number);
      await assignRoleMenus(roleId.value, menuIds);
      message.success('权限分配成功');
      modalApi.close();
      emit('success');
    } finally {
      modalApi.setState({ confirmLoading: false });
    }
  },
  onOpenChange: async (isOpen: boolean) => {
    if (isOpen) {
      const data = modalApi.getData<Record<string, any>>();
      if (data) {
        roleId.value = data.id;
        roleName.value = data.name || '';
        modalApi.setState({
          title: `分配权限 - ${data.name}`,
          loading: true,
        });

        try {
          // 获取角色已有的菜单权限
          const ids = await listRoleMenuIds(roleId.value);
          checkedMenuIds.value = ids.map(String);
        } finally {
          modalApi.setState({ loading: false });
        }
      }
    } else {
      // 关闭时重置
      checkedMenuIds.value = [];
      roleId.value = '';
      roleName.value = '';
    }
  },
});
</script>

<template>
  <Modal>
    <div class="flex flex-col gap-4 p-4">
      <Alert
        type="info"
        show-icon
        message="操作提示"
        description="请勾选需要分配给该角色的菜单和按钮权限。修改后即时生效。"
        class="mb-2"
      >
        <template #icon>
          <span class="icon-[lucide--info] text-blue-500"></span>
        </template>
      </Alert>

      <div
        class="flex flex-col overflow-hidden rounded-lg border border-gray-200 bg-white dark:border-gray-700 dark:bg-gray-800"
      >
        <PermissionTree
          v-model:value="checkedMenuIds"
          :role-id="String(roleId)"
          class="h-full"
        />
      </div>
    </div>
  </Modal>
</template>
