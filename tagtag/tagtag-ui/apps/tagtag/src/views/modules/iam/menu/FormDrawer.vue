<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer, useVbenForm } from '@vben/common-ui';

import { message } from 'ant-design-vue';

import { addMenu, editMenu, existsByCode, getMenuTree } from '#/api/modules/iam/menu';

import { formSchema } from './data';

interface Props {
  rowData?: any;
}

defineProps<Props>();
const emit = defineEmits(['success']);

const isUpdate = ref(true);

/**
 * 菜单表单抽屉
 * 负责新增/编辑菜单，并在打开时加载菜单树作为上级选择
 */
const [Form, formApi] = useVbenForm({
  schema: formSchema,
  showDefaultActions: false,
  commonConfig: { labelWidth: 70 },
});

/**
 * 异步唯一性校验：菜单编码不可重复
 */
async function validateMenuCodeUnique(code: string, id?: number) {
  if (!code) return Promise.resolve();
  const exists = await existsByCode(code);
  // 编辑时允许与自身相同
  if (exists && !id) {
    return Promise.reject(new Error('菜单编码已存在'));
  }
  return Promise.resolve();
}

/**
 * 抽屉与表单容器
 * - onConfirm：校验后调用新增/编辑接口
 * - onOpenChange：打开时加载树与初始值
 */
const [Drawer, drawerApi] = useVbenDrawer({
  onCancel() {
    drawerApi.close();
  },
  async onConfirm() {
    const { valid } = await formApi.validate();
    if (!valid) return;
    const data = await formApi.getValues();
    // 追加异步唯一性校验
    await validateMenuCodeUnique(String(data?.menuCode ?? ''), Number(data?.id ?? 0) || undefined);
    await (isUpdate.value ? editMenu(data) : addMenu(data));
    message.success(isUpdate.value ? '编辑菜单成功' : '新增菜单成功');
    drawerApi.close();
    emit('success');
  },
  async onOpenChange(isOpen: boolean) {
    if (!isOpen) return;
    const { values } = drawerApi.getData<Record<string, any>>();
    isUpdate.value = Boolean(values?.isUpdate);
    if (isUpdate.value) {
      formApi.setValues(values);
    } else {
      await formApi.resetForm();
      formApi.setValues({ parentId: values?.parentId ?? 0, status: 1, sort: 0, menuType: 1 });
    }
    drawerApi.setState({ title: isUpdate.value ? '编辑菜单' : '新增菜单' });

    const tree = await getMenuTree({});
    const treeData = [{ id: 0, menuName: '顶级菜单', children: Array.isArray(tree) ? tree : [] }];
    formApi.updateSchema([
      { fieldName: 'parentId', componentProps: { treeData } },
    ]);
  },
});
</script>
<template>
  <Drawer>
    <Form />
  </Drawer>
</template>