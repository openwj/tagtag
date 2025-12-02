<script setup lang="ts">
import type { VbenFormSchema } from '#/adapter/form';
import type { UploadChangeParam } from 'ant-design-vue';

import { computed, onMounted, ref } from 'vue';

import { ProfileBaseSetting } from '@vben/common-ui';

import { getUserInfoApi } from '#/api';
import { editUser } from '#/api/modules/iam/user';
import { useUserStore } from '@vben/stores';
import Upload from 'ant-design-vue/es/upload';
import { preferences } from '@vben/preferences';

const profileBaseSettingRef = ref();
const userStore = useUserStore();

const avatarUrl = ref<string>('');
const displayedAvatar = computed(() => {
  return (
    avatarUrl.value ||
    userStore.userInfo?.avatar ||
    preferences.app.defaultAvatar ||
    ''
  );
});

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      fieldName: 'nickname',
      component: 'Input',
      label: '昵称',
    },
    {
      fieldName: 'username',
      component: 'Input',
      label: '用户名',
    },
    {
      fieldName: 'email',
      component: 'Input',
      label: '电子邮箱',
      componentProps: { type: 'email', placeholder: '请输入邮箱地址' },
    },
    {
      fieldName: 'phone',
      component: 'Input',
      label: '手机号码',
      componentProps: { placeholder: '请输入手机号码' },
    },
    {
      fieldName: 'gender',
      component: 'Select',
      label: '性别',
      componentProps: {
        options: [
          { label: '未知', value: 0 },
          { label: '男', value: 1 },
          { label: '女', value: 2 },
        ],
        allowClear: true,
        placeholder: '请选择性别',
      },
    },
    {
      fieldName: 'birthday',
      component: 'DatePicker',
      label: '生日',
      componentProps: {
        format: 'YYYY-MM-DD',
        valueFormat: 'YYYY-MM-DD',
        placeholder: '请选择生日',
        class: 'w-full',
      },
    },
    {
      fieldName: 'remark',
      component: 'Textarea',
      label: '备注',
      componentProps: { rows: 3, placeholder: '请输入备注' },
    },
  ];
});

onMounted(async () => {
  const data = await getUserInfoApi();
  const preset = {
    ...data,
    remark: data?.desc ?? '',
  };
  profileBaseSettingRef.value.getFormApi().setValues(preset);
  avatarUrl.value = data?.avatar || '';
});

/**
 * 提交基本设置并保存到后端
 * @param values 表单值（昵称/用户名/角色/个人简介等）
 */
async function handleSubmit(values: Record<string, any>) {
  const uid = userStore.userInfo?.id;
  if (!uid) return;
  await editUser({ id: uid, avatar: avatarUrl.value, ...values });
}

/**
 * 头像上传变更事件处理（Ant Upload）
 * @param info Upload 变更参数
 */
async function handleAvatarChange(info: UploadChangeParam) {
  const raw = info?.file?.originFileObj as File | undefined;
  if (!raw) return;
  const res = await (await import('#/api/modules/storage/file')).uploadFile(raw);
  // 约定返回结构含 url 字段
  const url = res?.url ?? res?.data?.url ?? '';
  if (url) {
    avatarUrl.value = url;
  }
}
</script>
<template>
  <div class="mb-6">
    <div class="mb-2 text-sm text-muted-foreground">用户头像</div>
    <div class="flex items-center gap-4">
      <img v-if="displayedAvatar" :src="displayedAvatar" class="size-20 rounded-full object-cover" />
      <div v-else class="size-20 rounded-full bg-muted flex items-center justify-center text-xs text-muted-foreground">
        无头像
      </div>
      <Upload
        list-type="picture-card"
        :max-count="1"
        accept="image/*"
        :show-upload-list="false"
        @change="handleAvatarChange"
      >
        上传头像
      </Upload>
    </div>
  </div>
  <ProfileBaseSetting
    ref="profileBaseSettingRef"
    :form-schema="formSchema"
    @submit="handleSubmit"
  />
</template>
