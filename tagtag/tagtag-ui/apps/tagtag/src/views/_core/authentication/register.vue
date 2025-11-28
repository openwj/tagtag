<script lang="ts" setup>
import type { VbenFormSchema } from '@vben/common-ui';
import type { Recordable } from '@vben/types';

import { computed, h, markRaw, ref } from 'vue';

import { AuthenticationRegister, ImageCaptchaInput, z } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { notification } from 'ant-design-vue';

import { registerApi } from '#/api/core/auth';
import { fetchImageCaptchaApi } from '#/api/core/captcha';
import { useAuthStore } from '#/store';

defineOptions({ name: 'Register' });

const loading = ref(false);
const authStore = useAuthStore();

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
        passwordStrength: true,
        placeholder: $t('authentication.password'),
      },
      fieldName: 'password',
      label: $t('authentication.password'),
      renderComponentContent() {
        return {
          strengthText: () => $t('authentication.passwordStrength'),
        };
      },
      rules: z.string().min(1, { message: $t('authentication.passwordTip') }),
    },
    {
      component: 'VbenInputPassword',
      componentProps: {
        placeholder: $t('authentication.confirmPassword'),
      },
      dependencies: {
        rules(values) {
          const { password } = values;
          return z
            .string({ required_error: $t('authentication.passwordTip') })
            .min(1, { message: $t('authentication.passwordTip') })
            .refine((value) => value === password, {
              message: $t('authentication.confirmPasswordTip'),
            });
        },
        triggerFields: ['password'],
      },
      fieldName: 'confirmPassword',
      label: $t('authentication.confirmPassword'),
    },
    {
      component: 'VbenCheckbox',
      fieldName: 'agreePolicy',
      renderComponentContent: () => ({
        default: () =>
          h('span', [
            $t('authentication.agree'),
            h(
              'a',
              {
                class: 'vben-link ml-1 ',
                href: '',
              },
              `${$t('authentication.privacyPolicy')} & ${$t('authentication.terms')}`,
            ),
          ]),
      }),
      rules: z.boolean().refine((value) => !!value, {
        message: $t('authentication.agreeTip'),
      }),
    },
    {
      component: markRaw(ImageCaptchaInput),
      componentProps: {
        placeholder: $t('authentication.code'),
        fetchImage: async () => {
          const { src, captchaId } = await fetchImageCaptchaApi();
          return { src, captchaId };
        },
      },
      fieldName: 'captcha',
      label: $t('authentication.code'),
      rules: z.object({
        code: z.string().min(5, { message: $t('authentication.codeTip', [5]) }),
        captchaId: z
          .string()
          .min(1, { message: $t('authentication.codeTip', [5]) }),
      }),
    },
  ];
});

/**
 * 提交注册，并自动登录
 * @param value 包含 `username`、`password`、`confirmPassword`、`agreePolicy`、`captcha`
 */
async function handleSubmit(value: Recordable<any>) {
  try {
    loading.value = true;
    const { username, password, captcha } = value ?? {};
    await registerApi({ username, password });
    await authStore.authLogin({ username, password, captcha });
  } catch (error: any) {
    const msg = error?.message || $t('authentication.registerFail');
    notification.error({
      message: $t('authentication.register'),
      description: msg,
      duration: 3,
    });
    throw error;
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <AuthenticationRegister
    :form-schema="formSchema"
    :loading="loading"
    @submit="handleSubmit"
  />
</template>
