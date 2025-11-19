<script lang="ts" setup>
import type { VbenFormSchema } from '@vben/common-ui';

import { computed, markRaw, ref } from 'vue';

import { AuthenticationLogin, ImageCaptchaInput, z } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { fetchImageCaptchaApi } from '#/api/core/captcha';
import { useAuthStore } from '#/store';

defineOptions({ name: 'Login' });

const authStore = useAuthStore();
const loginRef = ref<any>(null);

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      component: 'VbenInput',
      componentProps: {
        placeholder: $t('authentication.usernameTip'),
      },
      fieldName: 'username',
      label: $t('authentication.username'),
      rules: z.string().min(1, { message: $t('authentication.usernameTip') }),
    },
    {
      component: 'VbenInputPassword',
      componentProps: {
        placeholder: $t('authentication.password'),
      },
      fieldName: 'password',
      label: $t('authentication.password'),
      rules: z.string().min(1, { message: $t('authentication.passwordTip') }),
    },
    {
      component: markRaw(ImageCaptchaInput),
      componentProps: {
        placeholder: $t('authentication.code'),
        /**
         * 拉取后端验证码图片
         */
        fetchImage: async () => {
          const { src, captchaId } = await fetchImageCaptchaApi();
          return { src, captchaId };
        },
      },
      fieldName: 'captcha',
      label: $t('authentication.code'),
      rules: z.object({
        code: z
          .string()
          .min(5, { message: $t('authentication.codeTip', [5]) })
          .optional(),
        captchaId: z.string().optional(),
      }),
    },
  ];
});
</script>

<template>
  <AuthenticationLogin
    ref="loginRef"
    :form-schema="formSchema"
    :loading="authStore.loginLoading"
    @submit="(p) => authStore.authLogin(p)"
  />
</template>
