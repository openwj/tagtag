<script setup lang="ts">
import type { VbenFormSchema } from '#/adapter/form';

import { computed, onMounted, ref, h } from 'vue';

import { ProfileBaseSetting, z } from '@vben/common-ui';
import { Icon } from '@iconify/vue';

import { getUserInfoApi } from '#/api';
import { updateMe } from '#/api/modules/iam/user';
import { useUserStore } from '@vben/stores';
import Upload from 'ant-design-vue/es/upload';
import { preferences } from '@vben/preferences';
import { message } from 'ant-design-vue';

const profileBaseSettingRef = ref();
const userStore = useUserStore();

const avatarUrl = ref<string>('');
const uploadingAvatar = ref<boolean>(false);
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
      componentProps: {
        placeholder: '请输入您的昵称',
      },
      rules: z.string().min(1, { message: '请输入昵称' }),
      renderComponentContent: () => ({
        prefix: () => h(Icon, { icon: 'lucide:user', class: 'text-muted-foreground' }),
      }),
    },
    {
      fieldName: 'username',
      component: 'Input',
      label: '用户名',
      componentProps: { disabled: true }, // 用户名通常不可修改
      renderComponentContent: () => ({
        prefix: () => h(Icon, { icon: 'lucide:shield', class: 'text-muted-foreground' }),
      }),
    },
    {
      fieldName: 'email',
      component: 'Input',
      label: '电子邮箱',
      componentProps: { type: 'email', placeholder: '请输入邮箱地址' },
      rules: z.string().email({ message: '请输入正确的邮箱格式' }).optional().or(z.literal('')),
      renderComponentContent: () => ({
        prefix: () => h(Icon, { icon: 'lucide:mail', class: 'text-muted-foreground' }),
      }),
    },
    {
      fieldName: 'phone',
      component: 'Input',
      label: '手机号码',
      componentProps: { placeholder: '请输入手机号码' },
      renderComponentContent: () => ({
        prefix: () => h(Icon, { icon: 'lucide:phone', class: 'text-muted-foreground' }),
      }),
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
 * @param values 表单值
 */
/**
 * 提交基本设置并保存到后端
 * @param values 表单值
 */
async function handleSubmit(values: Record<string, any>) {
  const uid = userStore.userInfo?.id ?? userStore.userInfo?.userId;
  if (!uid) {
    message.error('用户ID缺失，无法更新');
    return;
  }
  const res: any = await updateMe({ id: Number(uid), avatar: avatarUrl.value, ...values });
  const fresh = res?.data ?? res;
  if (fresh) {
    userStore.setUserInfo(fresh);
  } else {
    const reload = await getUserInfoApi();
    userStore.setUserInfo(reload);
  }
  message.success('更新成功');
}

/**
 * 头像文件选择前置处理：阻止组件自动上传，并手动走一次上传
 * @param file 选择的头像文件
 * @returns false 阻止 Upload 组件内部上传流程
 */
/**
 * 头像文件选择前置处理：阻止组件自动上传，并手动走一次上传
 * @param file 选择的头像文件
 * @returns false 阻止 Upload 组件内部上传流程
 */
async function handleAvatarBeforeUpload(file: File) {
  if (uploadingAvatar.value) return false;
  uploadingAvatar.value = true;
  try {
    const { uploadFile } = await import('#/api/modules/storage/file');
    const res = await uploadFile(file);
    const url = res?.url ?? res?.data?.url ?? '';
    if (url) {
      avatarUrl.value = url;
      message.success('头像上传成功');
    }
  } finally {
    uploadingAvatar.value = false;
  }
  return false;
}
</script>
<template>
  <div class="bg-card border-border rounded-lg border p-6">
    <div class="mb-6 border-b pb-4">
      <div class="text-lg font-semibold">{{$t('page.auth.profile.baseSettingTitle')}}</div>
      <div class="text-muted-foreground mt-1 text-sm">
        管理您的个人资料信息
      </div>
    </div>

    <div class="flex flex-col-reverse gap-10 md:flex-row">
      <!-- 左侧：表单区域 -->
      <div class="flex-1">
        <ProfileBaseSetting
          ref="profileBaseSettingRef"
          :form-schema="formSchema"
          @submit="handleSubmit"
        />
      </div>

      <!-- 右侧：头像区域 -->
      <div class="flex w-full flex-col items-center gap-6 md:w-72 md:border-l md:pl-10">
        <div class="text-muted-foreground text-sm font-medium self-start md:self-center">{{$t('page.auth.profile.avatar')}}</div>

        <div class="group relative size-32 overflow-hidden rounded-full border-4 border-background shadow-md transition-all hover:shadow-xl">
          <img
            v-if="displayedAvatar"
            :src="displayedAvatar"
            class="h-full w-full object-cover"
            alt="Avatar"
          />
          <div v-else class="bg-muted flex h-full w-full items-center justify-center text-muted-foreground">
            <Icon icon="lucide:user" class="size-16" />
          </div>

          <!-- 悬浮遮罩层 -->
          <div v-if="uploadingAvatar" class="absolute inset-0 z-20 flex items-center justify-center bg-black/50 text-white">
            <Icon icon="lucide:loader-2" class="size-8 animate-spin" />
          </div>

          <div class="absolute inset-0 flex cursor-pointer items-center justify-center bg-black/40 opacity-0 transition-opacity duration-300 group-hover:opacity-100">
            <Upload
              :max-count="1"
              accept="image/*"
              :show-upload-list="false"
              :before-upload="handleAvatarBeforeUpload"
              class="flex h-full w-full items-center justify-center"
            >
              <div class="flex flex-col items-center text-white">
                <Icon icon="lucide:camera" class="mb-1 size-6" />
                <span class="text-xs">更换</span>
              </div>
            </Upload>
          </div>
        </div>

        <div class="text-center">
          <div class="text-sm text-muted-foreground">
            支持 jpg、png、jpeg 格式
            <br />
            文件大小不超过 2MB
          </div>

          <!-- 移动端的备用上传按钮 -->
          <div class="mt-4 md:hidden">
            <Upload
              :max-count="1"
              accept="image/*"
              :show-upload-list="false"
              :before-upload="handleAvatarBeforeUpload"
            >
              <button class="btn btn-sm btn-outline">{{$t('page.auth.profile.uploadAvatar')}}</button>
            </Upload>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
