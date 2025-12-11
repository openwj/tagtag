<script lang="ts" setup>
import type { VbenFormSchema } from '@vben/common-ui';

import { computed, markRaw, ref } from 'vue';

import { AuthenticationLogin, ImageCaptchaInput, z } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { fetchImageCaptchaApi } from '#/api/core/captcha';
import { useAuthStore } from '#/store';

defineOptions({ name: 'Login' });

const authStore = useAuthStore();
const captchaKey = ref(Date.now());

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      component: 'VbenInput',
      componentProps: {
        placeholder: $t('authentication.usernameTip'),
        name: 'username',
        autocomplete: 'username',
      },
      fieldName: 'username',
      label: $t('authentication.username'),
      rules: z.string().min(1, { message: $t('authentication.usernameTip') }),
    },
    {
      component: 'VbenInputPassword',
      componentProps: {
        placeholder: $t('authentication.password'),
        name: 'password',
        autocomplete: 'current-password',
      },
      fieldName: 'password',
      label: $t('authentication.password'),
      rules: z.string().min(1, { message: $t('authentication.passwordTip') }),
    },
    {
      component: markRaw(ImageCaptchaInput),
      componentProps: {
        key: captchaKey.value,
        placeholder: $t('authentication.code'),
        autocomplete: 'off',
        /**
         * 拉取后端验证码图片
         */
        fetchImage: async () => {
          const { src, captchaId } = await fetchImageCaptchaApi();
          return { src, captchaId };
        },
      },
      formFieldProps: {
        validateOnModelUpdate: false,
        validateOnInput: true,
      },
      fieldName: 'captcha',
      label: $t('authentication.code'),
      rules: z.object({
        code: z
          .string({ required_error: $t('authentication.codeTip', [5]) })
          .min(5, { message: $t('authentication.codeTip', [5]) })
      }),
    },
  ];
});

async function handleLogin(values: any) {
  try {
    await authStore.authLogin(values);
  } catch {
    captchaKey.value = Date.now();
  }
}
</script>

<template>
  <AuthenticationLogin
    :form-schema="formSchema"
    :loading="authStore.loginLoading"
    @submit="handleLogin"
  />
</template>
