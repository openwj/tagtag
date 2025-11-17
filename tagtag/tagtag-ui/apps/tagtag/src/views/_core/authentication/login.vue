<script lang="ts" setup>
import type { VbenFormSchema } from '@vben/common-ui';

import { computed, markRaw } from 'vue';

import { AuthenticationLogin, SliderTranslateCaptcha, z } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { useAuthStore } from '#/store';
import { getTranslateCaptcha, verifyTranslateCaptcha } from '#/api/core/captcha';

defineOptions({ name: 'Login' });

const authStore = useAuthStore();
let captchaToken: string | null = null;

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
      component: markRaw(SliderTranslateCaptcha),
      fieldName: 'captcha',
      componentProps: {
        serverVerify: true,
        lazyLoad: true,
        /**
         * 拉取服务端拼图挑战
         * @function fetchChallenge
         */
        fetchChallenge: async () => {
          const c = await getTranslateCaptcha();
          return {
            src: c.imageUrl,
            diffDistance: c.diffDistance,
            pieceX: c.pieceX,
            pieceY: c.pieceY,
            challengeId: c.challengeId,
          };
        },
        /**
         * 提交服务端校验，并在通过后缓存一次性令牌
         * @function verifyChallenge
         */
        verifyChallenge: async (payload: {
          challengeId: string;
          pieceX: number;
          moveX: number;
          time: number;
        }) => {
          const r = await verifyTranslateCaptcha(payload);
          if (r.passed) {
            captchaToken = r.captchaToken ?? null;
          } else {
            captchaToken = null;
          }
          return r.passed;
        },
      },
      rules: z.boolean().refine((value) => value, {
        message: $t('authentication.verifyRequiredTip'),
      }),
    },
  ];
});

/** 提交登录表单 */
function onSubmit(values: any) {
  const { username, password } = values ?? {};
  return authStore.authLogin({ username, password, captchaToken });
}
</script>

<template>
  <AuthenticationLogin
    :form-schema="formSchema"
    :loading="authStore.loginLoading"
    @submit="onSubmit"
  />
</template>
