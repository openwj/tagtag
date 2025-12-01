<script lang="ts" setup>
import type { VbenFormSchema } from '@vben/common-ui';
import type { Recordable } from '@vben/types';

import { computed, h, markRaw, ref } from 'vue';
import { useRouter } from 'vue-router';

import { AuthenticationRegister, ImageCaptchaInput, z } from '@vben/common-ui';
import { $t } from '@vben/locales';

import { notification } from 'ant-design-vue';

import { registerApi } from '#/api/core/auth';
import { fetchImageCaptchaApi } from '#/api/core/captcha';

defineOptions({ name: 'Register' });

const loading = ref(false);
const router = useRouter();

const formSchema = computed((): VbenFormSchema[] => {
  return [
    {
      component: 'VbenInput',
      componentProps: {
        placeholder: $t('page.auth.usernameTip'),
        name: 'username',
        autocomplete: 'username',
      },
      fieldName: 'username',
      label: $t('page.auth.username'),
      rules: z.string().min(1, { message: $t('page.auth.usernameTip') }),
    },
    {
      component: 'VbenInputPassword',
      componentProps: {
        passwordStrength: true,
        placeholder: $t('page.auth.password'),
        name: 'password',
        autocomplete: 'new-password',
      },
      fieldName: 'password',
      label: $t('page.auth.password'),
      renderComponentContent() {
        return {
          strengthText: () => $t('page.auth.passwordStrength'),
        };
      },
      rules: z
        .string({ required_error: $t('page.auth.passwordTip') })
        .regex(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/, {
          message: $t('page.auth.passwordStrength'),
        }),
    },
    {
      component: 'VbenInputPassword',
      componentProps: {
        placeholder: $t('page.auth.confirmPassword'),
        name: 'confirm-password',
        autocomplete: 'new-password',
      },
      dependencies: {
        rules(values) {
          const { password } = values;
          return z
            .string({ required_error: $t('page.auth.passwordTip') })
            .min(1, { message: $t('page.auth.passwordTip') })
            .refine((value) => value === password, {
              message: $t('page.auth.confirmPasswordTip'),
            });
        },
        triggerFields: ['password'],
      },
      fieldName: 'confirmPassword',
      label: $t('page.auth.confirmPassword'),
    },
    {
      component: markRaw(ImageCaptchaInput),
      componentProps: {
        placeholder: $t('page.auth.code'),
        autocomplete: 'off',
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
      label: $t('page.auth.code'),
      rules: z.object({
        code: z.string().min(5, { message: $t('page.auth.codeTip', [5]) }),
      }),
    },
    {
      component: 'VbenCheckbox',
      fieldName: 'agreePolicy',
      renderComponentContent: () => ({
        default: () =>
          h('span', [
            $t('page.auth.agree'),
            h(
              'a',
              {
                class: 'vben-link ml-1 ',
                href: '',
              },
              `${$t('page.auth.privacyPolicy')} & ${$t('page.auth.terms')}`,
            ),
          ]),
      }),
      rules: z.boolean().refine((value) => !!value, {
        message: $t('page.auth.agreeTip'),
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
    await registerApi({ username, password, captcha });

    let count = 5;
    const key = `register-success-${Date.now()}`;
    const renderDesc = () => $t('page.auth.registerSuccess', [count]);

    notification.success({
      message: $t('page.auth.register'),
      description: renderDesc(),
      duration: 0,
      key,
    });

    const timer = setInterval(() => {
      count -= 1;
      if (count <= 0) {
        clearInterval(timer);
        notification.close(key);
        router.push({ name: 'Login' });
      } else {
        notification.success({
          message: $t('page.auth.register'),
          description: renderDesc(),
          duration: 0,
          key,
        });
      }
    }, 1000);
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
