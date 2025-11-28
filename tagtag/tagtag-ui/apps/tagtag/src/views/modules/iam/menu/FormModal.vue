<script lang="ts" setup>
import type { MenuApiParams } from '#/api/modules/iam/menu';

import { ref } from 'vue';

import { useVbenModal } from '@vben/common-ui';

import { useVbenForm } from '#/adapter/form';
import {
  addMenu,
  editMenu,
  getMenuById,
  getMenuTree,
} from '#/api/modules/iam/menu';

import { formSchema } from './data';

defineOptions({
  name: 'FormModel',
});

const emit = defineEmits(['success']);

const isUpdate = ref(true);

/**
 * 布尔值转数字的辅助函数
 * @param value 布尔值或其他值
 * @param defaultValue 默认值
 * @returns 转换后的数字
 */
function boolToNumber(value: any, defaultValue = 0): number {
  if (value === true) return 1;
  if (value === false) return 0;
  return Number(value) || defaultValue;
}

/**
 * 数字转布尔值的辅助函数
 * @param value 数字值
 * @returns 转换后的布尔值
 */
function numberToBool(value: any): boolean {
  return value === 1;
}

/**
 * 默认表单值
 */
const defaultFormValues = {
  menuType: 0,
  parentId: 0,
  menuName: '',
  menuCode: '',
  icon: '',
  path: '',
  component: '',
  sort: 0,
  isHidden: false,
  isExternal: false,
  isKeepalive: false,
  externalUrl: '',
  remark: '',
  status: true,
};

// function onSubmit(values: Record<string, any>) {
//   message.info(JSON.stringify(values)); // 只会执行一次
// }

const [Form, formApi] = useVbenForm({
  // handleSubmit: onSubmit,
  schema: formSchema,
  showDefaultActions: false,
  // 大屏一行显示3个，中屏一行显示2个，小屏一行显示1个
  wrapperClass: 'grid-cols-1 md:grid-cols-2',
});

const [Modal, modalApi] = useVbenModal({
  fullscreenButton: false,
  onCancel() {
    modalApi.close();
  },
  onConfirm: async () => {
    await formApi.validateAndSubmitForm();

    const { valid } = await formApi.validate();
    if (valid) {
      const formData = await formApi.getValues();
      // 使用辅助函数简化数据转换
      const data = {
        ...formData,
        status: boolToNumber(formData.status, 1),
        menuType: Number(formData.menuType || 0),
        isHidden: boolToNumber(formData.isHidden),
        isExternal: boolToNumber(formData.isExternal),
        isKeepalive: boolToNumber(formData.isKeepalive),
        sort: Number(formData.sort || 0),
        parentId: Number(formData.parentId) || 0,
      };

      const payload = data as MenuApiParams.MenuForm;
      await (isUpdate.value ? editMenu(payload) : addMenu(payload));
      modalApi.close();
      emit('success');
    }
  },
  onOpenChange: async (isOpen: boolean) => {
    if (isOpen) {
      // 重置表单，确保所有字段都有默认值
      await formApi.resetForm();

      const { values } = modalApi.getData<Record<string, any>>();

      // 先获取菜单树数据
      const tree = await getMenuTree({});
      const treeData = [
        {
          id: 0,
          menuName: '顶级菜单',
          children: tree || [],
        },
      ];

      // 更新父级菜单选择器的数据
      await formApi.updateSchema([
        {
          componentProps: {
            treeData,
          },
          fieldName: 'parentId',
        },
      ]);

      if (values) {
        isUpdate.value = values.isUpdate;

        if (values.isUpdate && values.id) {
          // 编辑模式：获取菜单数据并设置表单值
          const menu = await getMenuById(values.id);
          if (menu) {
            // 使用展开运算符和辅助函数简化赋值
            const formValues = {
              ...menu,
              // 数字转布尔值
              isHidden: numberToBool(menu.isHidden),
              isExternal: numberToBool(menu.isExternal),
              isKeepalive: numberToBool(menu.isKeepalive),
              status: numberToBool(menu.status),
              // 确保数字类型
              menuType: Number(menu.menuType || 0),
              parentId: Number(menu.parentId || 0),
              sort: Number(menu.sort || 0),
            };
            await formApi.setValues(formValues);
          }
        } else {
          // 新增模式：设置默认值
          const newValues = {
            ...defaultFormValues,
            parentId: values.parentId ? Number(values.parentId) : 0,
          };
          await formApi.setValues(newValues);
        }
      } else {
        // 没有传入values时的默认值设置
        isUpdate.value = false;
        await formApi.setValues(defaultFormValues);
      }

      modalApi.setState({
        title: isUpdate.value ? '编辑菜单' : '新增菜单',
      });
    }
  },
});
</script>
<template>
  <Modal>
    <Form />
  </Modal>
</template>
