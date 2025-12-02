<script setup lang="ts">
import type { VbenFormSchema } from '#/adapter/form';

import { computed, ref } from 'vue';

import { ProfilePasswordSetting, z } from '@vben/common-ui';
import { Icon } from '@iconify/vue';

import { message } from 'ant-design-vue';
import { changeMyPassword } from '#/api/modules/iam/user';

const profilePasswordSettingRef = ref();
const changeOk = ref(false);

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      fieldName: 'oldPassword',
      label: '旧密码',
      component: 'VbenInputPassword',
      componentProps: {
        placeholder: '请输入旧密码',
        name: 'old-password',
        autocomplete: 'current-password',
      },
    },
    {
      fieldName: 'newPassword',
      label: '新密码',
      component: 'VbenInputPassword',
      componentProps: {
        passwordStrength: true,
        placeholder: '请输入新密码',
        name: 'new-password',
        autocomplete: 'new-password',
      },
    },
    {
      fieldName: 'confirmPassword',
      label: '确认密码',
      component: 'VbenInputPassword',
      componentProps: {
        passwordStrength: true,
        placeholder: '请再次输入新密码',
        name: 'confirm-password',
        autocomplete: 'new-password',
      },
      dependencies: {
        rules(values) {
          const { newPassword } = values;
          return z
            .string({ required_error: '请再次输入新密码' })
            .min(1, { message: '请再次输入新密码' })
            .refine((value) => value === newPassword, {
              message: '两次输入的密码不一致',
            });
        },
        triggerFields: ['newPassword'],
      },
    },
  ];
});

/**
 * 提交修改密码并调用后端接口
 */
async function handleSubmit() {
  const values = await profilePasswordSettingRef.value
    .getFormApi()
    .getValues();
  await changeMyPassword(values.oldPassword, values.newPassword);
  message.success('密码修改成功');
  changeOk.value = true;
}

function handleReset() {
  changeOk.value = false;
  // 重置表单逻辑如果需要的话
}
</script>
<template>
  <div class="bg-card border-border rounded-lg border p-6">
    <div class="mb-6 border-b pb-4">
      <div class="text-lg font-semibold">{{$t('page.auth.profile.securityTitle')}}</div>
      <div class="text-muted-foreground mt-1 text-sm">
        定期更新密码以保护您的账户安全
      </div>
    </div>

    <div class="grid grid-cols-1 gap-10 lg:grid-cols-3">
      <!-- 左侧：修改密码表单或成功状态 -->
      <div class="lg:col-span-2">
        <template v-if="!changeOk">
          <ProfilePasswordSetting
            ref="profilePasswordSettingRef"
            :form-schema="formSchema"
            @submit="handleSubmit"
          />
        </template>
        <template v-else>
          <div class="flex flex-col items-center justify-center gap-4 py-12 border-border border rounded-lg bg-muted/10">
            <div class="size-20 rounded-full bg-green-100 text-green-600 flex items-center justify-center dark:bg-green-900/30 dark:text-green-400">
              <Icon icon="lucide:check" class="size-10" />
            </div>
            <div class="text-xl font-semibold">{{$t('page.auth.profile.passwordUpdated')}}</div>
            <div class="text-muted-foreground text-center max-w-xs">
              {{$t('page.auth.profile.passwordUpdatedDesc')}}
            </div>
            <button
              class="bg-primary text-primary-foreground hover:bg-primary/90 h-10 px-4 py-2 rounded-md text-sm font-medium transition-colors"
              @click="handleReset"
            >
              返回修改
            </button>
          </div>
        </template>
      </div>

      <!-- 右侧：安全建议 -->
      <div class="lg:col-span-1">
        <div class="bg-muted/30 rounded-lg p-5 border border-border/50">
          <div class="flex items-center gap-2 mb-4 text-foreground font-medium">
            <Icon icon="lucide:shield-check" class="size-5 text-primary" />
            <span>安全建议</span>
          </div>
          <ul class="space-y-4 text-sm text-muted-foreground">
            <li class="flex gap-3">
              <Icon icon="lucide:check-circle-2" class="size-4 text-green-500 flex-none mt-0.5" />
              <span>密码长度至少为 8 个字符，建议包含大写字母、小写字母、数字和特殊符号。</span>
            </li>
            <li class="flex gap-3">
              <Icon icon="lucide:check-circle-2" class="size-4 text-green-500 flex-none mt-0.5" />
              <span>请勿使用容易被猜到的密码，如生日、电话号码等。</span>
            </li>
            <li class="flex gap-3">
              <Icon icon="lucide:check-circle-2" class="size-4 text-green-500 flex-none mt-0.5" />
              <span>不要在多个网站使用相同的密码，以免一个账号泄露导致其他账号被盗。</span>
            </li>
            <li class="flex gap-3">
              <Icon icon="lucide:check-circle-2" class="size-4 text-green-500 flex-none mt-0.5" />
              <span>定期更换密码可以有效降低账号被盗的风险。</span>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>
