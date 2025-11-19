<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer, useVbenForm } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { addUser, editUser } from '#/api/modules/iam/user';
import { getDeptTree } from '#/api/modules/iam/dept';

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
 * 用户表单抽屉
 * 负责新增/编辑用户，并在打开时加载部门树作为部门选择
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
      await (isUpdate.value ? editUser(data) : addUser(data));
      message.success(isUpdate.value ? '编辑用户成功' : '新增用户成功');
      drawerApi.close();
      emit('success');
    }
  },
  /**
   * 抽屉开关：打开时加载部门树并设置表单初始值
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
        title: isUpdate.value ? '编辑用户' : '新增用户',
      });

      const tree = await getDeptTree({});
      const treeData = [
        { id: 0, name: '顶级组织架构', children: tree },
      ];
      formApi.updateSchema([
        {
          componentProps: { treeData },
          fieldName: 'deptId',
        },
      ]);
    }
  },
});
</script>
<template>
  <Drawer>
    <Form />
  </Drawer>
</template>