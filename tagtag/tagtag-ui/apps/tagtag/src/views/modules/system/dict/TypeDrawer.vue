<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { saveDictType, updateDictType } from '#/api/modules/system/dict';

const emit = defineEmits(['success']);

const isUpdate = ref(false);
const recordId = ref<string>();

const [Form, formApi] = useVbenForm({
  schema: [
    {
      fieldName: 'name',
      label: '字典名称',
      component: 'Input',
      rules: 'required',
    },
    {
      fieldName: 'code',
      label: '字典类型',
      component: 'Input',
      rules: 'required',
    },
    {
      fieldName: 'status',
      label: '状态',
      component: 'RadioGroup',
      defaultValue: 1,
      componentProps: {
        options: [
          { label: '正常', value: 1 },
          { label: '停用', value: 0 },
        ],
      },
    },
    {
      fieldName: 'remark',
      label: '备注',
      component: 'Textarea',
    },
  ],
  // 关闭表单默认动作按钮，统一由抽屉底部按钮提交
  showDefaultActions: false,
});

const [Drawer, drawerApi] = useVbenDrawer({
  /**
   * 提交确认：校验并保存字典类型
   */
  onConfirm: async () => {
    try {
      await formApi.validate();
      const values = await formApi.getValues();
      drawerApi.setState({ confirmLoading: true });

      await (isUpdate.value
        ? updateDictType({ ...values, id: recordId.value })
        : saveDictType(values));

      message.success(isUpdate.value ? '修改成功' : '新增成功');
      emit('success');
      drawerApi.close();
    } finally {
      drawerApi.setState({ confirmLoading: false });
    }
  },
  /**
   * 抽屉打开变更：初始化编辑/新增模式与默认值
   */
  onOpenChange: (isOpen) => {
    if (isOpen) {
      const { isUpdate: update, record } = drawerApi.getData<any>() || {};
      isUpdate.value = !!update;
      recordId.value = record?.id;

      if (update && record) {
        formApi.setValues(record);
      } else {
        formApi.resetForm();
      }
    }
  },
  /**
   * 取消关闭抽屉
   */
  onCancel() {
    drawerApi.close();
  },
});
</script>

<template>
  <Drawer :title="isUpdate ? '编辑字典类型' : '新增字典类型'">
    <Form />
  </Drawer>
</template>
