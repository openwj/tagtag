<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';

import { useVbenForm } from '#/adapter/form';
import { addUser, editUser } from '#/api/modules/iam/user';

import { formSchema } from './data';

defineOptions({
  name: 'FormDrawer',
});

const emit = defineEmits(['success']);

const isUpdate = ref(true);

const [Form, formApi] = useVbenForm({
  schema: formSchema,
  showDefaultActions: false,
  commonConfig: {
    labelWidth: 80,
  },
});
const [Drawer, drawerApi] = useVbenDrawer({
  onCancel() {
    drawerApi.close();
  },
  onConfirm: async () => {
    const { valid } = await formApi.validate();
    if (valid) {
      const data = await formApi.getValues();
      await (isUpdate.value ? editUser(data) : addUser(data));
      drawerApi.close();
      emit('success');
    }
  },
  onOpenChange: async (isOpen: boolean) => {
    if (isOpen) {
      const { values } = drawerApi.getData<Record<string, any>>();
      isUpdate.value = values.isUpdate;
      // 新增模式：先清空表单，然后设置默认值
      formApi.resetForm();
      // 根据操作类型设置表单值
      if (isUpdate.value) {
        // 编辑模式：设置完整的用户数据
        formApi.setValues(values);
      } else {
        const defaultValues: Record<string, any> = {};
        if (values.deptId) {
          defaultValues.deptId = values.deptId;
        }
        formApi.setValues(defaultValues);
      }

      drawerApi.setState({
        title: isUpdate.value ? '编辑用户' : '新增用户',
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
