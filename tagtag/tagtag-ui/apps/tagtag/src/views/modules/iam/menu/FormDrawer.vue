<script lang="ts" setup>
import { ref } from 'vue';

import { useVbenDrawer } from '@vben/common-ui';
import { message } from 'ant-design-vue';

import { addMenu, editMenu, getMenuByCode } from '#/api/modules/iam/menu';

const formRef = ref();
const formModel = ref<Record<string, any>>({ id: null, menuCode: '', menuName: '', remark: '' });

const isUpdate = ref(false);

/**
 * 提交菜单表单
 * @param values 表单当前值
 */
async function submit(values: Record<string, any>) {
  if (isUpdate.value) {
    await editMenu(values);
    message.success('编辑成功');
  } else {
    await addMenu(values);
    message.success('新增成功');
  }
}

/**
 * 校验菜单编码是否已存在
 * @param code 菜单编码
 */
async function validateCodeExists(code: string) {
  if (!code) return true;
  try {
    const data = await getMenuByCode(code);
    // 若能查到记录则表示存在
    return !data || !data.id;
  } catch (e) {
    // 查询不存在会返回错误或空，视为可用
    return true;
  }
}

/**
 * 菜单表单抽屉
 */
const [Drawer, drawerApi] = useVbenDrawer({
  onCancel: () => drawerApi.close(),
  onConfirm: async () => {
    const values = formModel.value;
    await submit(values);
    drawerApi.close();
    drawerApi.emit('success');
  },
  onOpenChange: (isOpen: boolean) => {
    if (!isOpen) return;
    const { values } = drawerApi.getData<Record<string, any>>();
    isUpdate.value = Boolean(values?.isUpdate);
    formModel.value = {
      id: values?.id ?? null,
      menuCode: values?.menuCode ?? '',
      menuName: values?.menuName ?? '',
      remark: values?.remark ?? '',
    };
    drawerApi.setState({ title: isUpdate.value ? '编辑菜单' : '新增菜单' });
  },
});
</script>

<template>
  <Drawer>
    <div class="p-4">
      <a-form ref="formRef" :model="formModel" label-align="left" label-col="{ span: 6 }" wrapper-col="{ span: 16 }">
        <a-form-item label="菜单编码" name="menuCode" :rules="[{ required: true, message: '请输入菜单编码' }, { validator: async (_: any, value: string) => { const ok = await validateCodeExists(value); return ok ? Promise.resolve() : Promise.reject('菜单编码已存在'); } }]">
          <a-input v-model:value="formModel.menuCode" placeholder="请输入菜单编码" />
        </a-form-item>
        <a-form-item label="菜单名称" name="menuName" :rules="[{ required: true, message: '请输入菜单名称' }]">
          <a-input v-model:value="formModel.menuName" placeholder="请输入菜单名称" />
        </a-form-item>
        <a-form-item label="备注" name="remark">
          <a-textarea v-model:value="formModel.remark" :rows="4" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </div>
  </Drawer>
</template>