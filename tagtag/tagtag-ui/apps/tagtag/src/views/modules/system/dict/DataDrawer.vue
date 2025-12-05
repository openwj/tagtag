<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { useVbenForm } from '#/adapter/form';
import { saveDictData, updateDictData } from '#/api/modules/system/dict';

const emit = defineEmits(['success']);

const isUpdate = ref(false);
const recordId = ref<string>();
const currentDictType = ref<string>();

const [Form, formApi] = useVbenForm({
  schema: [
    {
      fieldName: 'typeCode',
      label: '字典类型',
      component: 'Input',
      componentProps: {
        disabled: true,
      },
      rules: 'required',
    },
    {
      fieldName: 'itemName',
      label: '字典标签',
      component: 'Input',
      rules: 'required',
    },
    {
      fieldName: 'itemCode',
      label: '字典键值',
      component: 'Input',
      rules: 'required',
    },
    {
      fieldName: 'sort',
      label: '排序',
      component: 'InputNumber',
      defaultValue: 0,
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
   * 提交确认：校验并保存字典数据
   */
  onConfirm: async () => {
    try {
      await formApi.validate();
      const values = await formApi.getValues();
      drawerApi.setState({ confirmLoading: true });

      await (isUpdate.value
        ? updateDictData({ ...values, id: recordId.value })
        : saveDictData(values));

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
      const {
        isUpdate: update,
        record,
        dictType,
      } = drawerApi.getData<any>() || {};
      isUpdate.value = !!update;
      recordId.value = record?.id;
      currentDictType.value = dictType;

      if (update && record) {
        formApi.setValues(record);
      } else {
        formApi.resetForm();
        formApi.setFieldValue('typeCode', dictType);
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
  <Drawer :title="isUpdate ? '编辑字典数据' : '新增字典数据'">
    <Form />
  </Drawer>
</template>
