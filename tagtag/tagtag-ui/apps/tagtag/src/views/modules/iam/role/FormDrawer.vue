<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import {
  addRole,
  assignRoleMenus,
  editRole,
  getRoleByCode,
  getRoleByName,
  getRoleDetail,
  listRoleMenuIds,
} from '#/api/modules/iam/role';

import { formSchema } from './data';
import PermissionTree from './PermissionTree.vue';

defineOptions({
  name: 'RoleFormDrawer',
});

const emit = defineEmits(['success']);

const isUpdate = ref(true);
const selectedPermissions = ref<string[]>([]);
const roleId = ref<string>('');

const [Form, formApi] = useVbenForm({
  schema: formSchema,
  showDefaultActions: false,
  commonConfig: {
    labelWidth: 100,
  },
});

const [Drawer, drawerApi] = useVbenDrawer({
  onCancel() {
    drawerApi.close();
  },
  /**
   * 提交确认：校验表单、保存角色并分配权限
   */
  onConfirm: async () => {
    const { valid } = await formApi.validate();
    if (valid) {
      const formData = await formApi.getValues();

      // 异步唯一性校验：编码与名称
      const code = String(formData?.code || '').trim();
      const name = String(formData?.name || '').trim();
      const selfId = String(roleId.value || '');
      if (code) {
        const existedByCode = await getRoleByCode(code);
        if (
          existedByCode &&
          String(existedByCode.id || '') &&
          String(existedByCode.id) !== selfId
        ) {
          message.error('角色编码已存在');
          return;
        }
      }
      if (name) {
        const existedByName = await getRoleByName(name);
        if (
          existedByName &&
          String(existedByName.id || '') &&
          String(existedByName.id) !== selfId
        ) {
          message.error('角色名称已存在');
          return;
        }
      }

      // 保存角色基本信息并获取返回值（优先使用返回的 id）
      const saved = await (isUpdate.value
        ? editRole(formData)
        : addRole(formData));

      // 计算当前角色 ID（更新场景优先用已有 id；新增用返回值兜底编码查询）
      let currentRoleId = '';
      if (isUpdate.value) {
        currentRoleId = String(roleId.value || saved?.id || '');
      } else {
        currentRoleId = String(saved?.id || '');
        if (!currentRoleId && formData?.code) {
          const created = await getRoleByCode(String(formData.code));
          currentRoleId = String(created?.id || '');
        }
      }

      if (currentRoleId) {
        // 将字符串ID数组转换为数字数组，过滤掉无效值
        const menuIds = Array.isArray(selectedPermissions.value)
          ? selectedPermissions.value
              .map(Number)
              .filter((id) => !Number.isNaN(id) && id > 0)
          : [];

        // 始终调用权限分配，包括空数组（清空权限的情况）
        await assignRoleMenus(currentRoleId, menuIds);
      }

      message.success(isUpdate.value ? '角色更新成功' : '角色创建成功');
      drawerApi.close();
      // 新增模式下返回角色ID给父组件
      emit('success', isUpdate.value ? roleId.value : currentRoleId);
    }
  },
  /**
   * 抽屉打开变更：初始化编辑/新增模式与默认值
   */
  onOpenChange: async (isOpen: boolean) => {
    if (isOpen) {
      const { values } = drawerApi.getData<Record<string, any>>();
      isUpdate.value = values.isUpdate;
      roleId.value = values.id || '';

      // 重置表单和权限
      formApi.resetForm();
      selectedPermissions.value = [];

      if (isUpdate.value) {
        // 编辑模式：获取角色详情和权限
        const [roleDetail, menuIds = []] = await Promise.all([
          getRoleDetail(values.id),
          listRoleMenuIds(values.id).catch(() => []),
        ]);

        formApi.setValues(roleDetail);

        // 设置已分配的权限ID - 统一为字符串数组以驱动树选中
        selectedPermissions.value = Array.isArray(menuIds)
          ? menuIds
              .map((id: any) => String(Number(id)))
              .filter((s) => !!s && !Number.isNaN(Number(s)))
          : [];
      } else {
        // 新增模式：设置默认值
        const defaultValues = {
          status: 1,
          sort: 0,
          roleType: 2,
        };
        formApi.setValues(defaultValues);
      }

      drawerApi.setState({
        title: isUpdate.value ? '编辑角色' : '新增角色',
      });
    }
  },
});
</script>

<template>
  <Drawer>
    <div class="flex h-full flex-col">
      <div class="flex-1 overflow-y-auto">
        <Form />
        <div class="px-6 py-4">
          <PermissionTree
            :value="selectedPermissions"
            @update:value="selectedPermissions = $event"
          />
        </div>
      </div>
    </div>
  </Drawer>
</template>
