<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer, useVbenForm } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { addDept, editDept, getDeptTree } from '#/api/modules/iam/dept';

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
  commonConfig: {
    labelWidth: 70,
  },
});
/**
 * 部门表单抽屉
 * 负责新增/编辑部门，并在打开时加载部门树作为上级选择
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

    console.log("保存"+ isUpdate.value)
    if (valid) {
      const data = await formApi.getValues();
      await (isUpdate.value ? editDept(data) : addDept(data));
      message.success(isUpdate.value ? '编辑部门成功' : '新增部门成功');
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

      // 同步外部传入的新增/编辑标记到本地状态
      isUpdate.value = Boolean(values?.isUpdate);

      console.log(values)
      if (isUpdate.value) {
        formApi.setValues(values);
      } else {
        await formApi.resetForm();
        formApi.setValues({ parentId: values.parentId });
      }

      drawerApi.setState({
        title: isUpdate.value ? '编辑部门' : '新增部门',
      });
      const tree = await getDeptTree({});

      const treeData = [
        {
          id: 0,
          name: '顶级组织架构',
          children: tree,
        },
      ];
      // console.log(treedata);
      formApi.updateSchema([
        {
          componentProps: {
            treeData,
          },
          fieldName: 'parentId',
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
