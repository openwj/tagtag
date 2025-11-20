<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer, useVbenForm } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { addRole, editRole } from '#/api/modules/iam/role';

import { formSchema } from './data';

interface Props {
  rowData?: any;
}

defineProps<Props>();
const emit = defineEmits(['success']);

const isUpdate = ref(true);

const [Form, formApi] = useVbenForm({
  schema: formSchema,
  showDefaultActions: false,
  commonConfig: { labelWidth: 80 },
});

/**
 * 角色表单抽屉
 * 负责新增/编辑角色
 */
const [Drawer, drawerApi] = useVbenDrawer({
  onCancel() {
    drawerApi.close();
  },
  /**
   * 提交表单：校验通过后调用后端创建或更新接口
   */
  onConfirm: async () => {
    const { valid } = await formApi.validate();
    if (valid) {
      const data = await formApi.getValues();
      await (isUpdate.value ? editRole(data) : addRole(data));
      message.success(isUpdate.value ? '编辑角色成功' : '新增角色成功');
      drawerApi.close();
      emit('success');
    }
  },
  /**
   * 抽屉开关：打开时设置表单初始值
   */
  onOpenChange: async (isOpen: boolean) => {
    if (isOpen) {
      const { values } = drawerApi.getData<Record<string, any>>();
      isUpdate.value = Boolean(values?.isUpdate);

      if (isUpdate.value) {
        formApi.setValues(values);
      } else {
        await formApi.resetForm();
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
    <Form />
  </Drawer>
  
</template>