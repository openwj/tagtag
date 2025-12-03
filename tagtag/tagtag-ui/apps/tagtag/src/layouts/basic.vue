<script lang="ts" setup>
import type { NotificationItem } from '@vben/layouts';

import type { MessageItem } from '#/api/modules/system/message';

import { computed, onUnmounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import { AuthenticationLoginExpiredModal } from '@vben/common-ui';
import { VBEN_DOC_URL, VBEN_GITHUB_URL } from '@vben/constants';
import { useWatermark } from '@vben/hooks';
import { BookOpenText, CircleHelp, SvgGithubIcon } from '@vben/icons';
import {
  BasicLayout,
  LockScreen,
  Notification,
  UserDropdown,
} from '@vben/layouts';
import { preferences } from '@vben/preferences';
import { useAccessStore, useUserStore } from '@vben/stores';
import { openWindow } from '@vben/utils';

import {
  clearAllMessages,
  getMessageList,
  markAllMessageRead,
  markMessageRead,
} from '#/api/modules/system/message';
import { $t } from '#/locales';
import { useAuthStore } from '#/store';
import LoginForm from '#/views/_core/authentication/login.vue';

const notifications = ref<NotificationItem[]>([]);

// 加载消息列表
const loadMessages = async () => {
  try {
    const list = await getMessageList();
    notifications.value = list.map((item: MessageItem) => ({
      id: String(item.id),
      title: item.title,
      message: item.message, // 后端content映射为message
      date: item.date, // 后端createTime映射为date
      avatar: item.avatar || 'https://avatar.vercel.sh/sys',
      isRead: item.isRead,
      link: item.link,
    }));
  } catch (error) {
    console.error('加载消息失败:', error);
  }
};

// 初始化加载并轮询
loadMessages();
const timer = setInterval(loadMessages, 60_000); // 每分钟轮询一次

onUnmounted(() => {
  clearInterval(timer);
});

const router = useRouter();
const userStore = useUserStore();
const authStore = useAuthStore();
const accessStore = useAccessStore();
const { destroyWatermark, updateWatermark } = useWatermark();
const showDot = computed(() =>
  notifications.value.some((item) => !item.isRead),
);

const menus = computed(() => [
  {
    handler: () => {
      router.push({ name: 'Profile' });
    },
    icon: 'lucide:user',
    text: $t('page.auth.profile.title'),
  },
  // {
  //   handler: () => {
  //     openWindow(VBEN_DOC_URL, {
  //       target: '_blank',
  //     });
  //   },
  //   icon: BookOpenText,
  //   text: $t('ui.widgets.document'),
  // },
  // {
  //   handler: () => {
  //     openWindow(VBEN_GITHUB_URL, {
  //       target: '_blank',
  //     });
  //   },
  //   icon: SvgGithubIcon,
  //   text: 'GitHub',
  // },
  // {
  //   handler: () => {
  //     openWindow(`${VBEN_GITHUB_URL}/issues`, {
  //       target: '_blank',
  //     });
  //   },
  //   icon: CircleHelp,
  //   text: $t('ui.widgets.qa'),
  // },
]);

const avatar = computed(() => {
  return userStore.userInfo?.avatar ?? preferences.app.defaultAvatar;
});

async function handleLogout() {
  await authStore.logout(false);
}

watch(
  () => ({
    enable: preferences.app.watermark,
    content: preferences.app.watermarkContent,
  }),
  async ({ enable, content }) => {
    if (enable) {
      await updateWatermark({
        content:
          content ||
          `${userStore.userInfo?.username} - ${userStore.userInfo?.nickname || userStore.userInfo?.username}`,
      });
    } else {
      destroyWatermark();
    }
  },
  {
    immediate: true,
  },
);
</script>

<template>
  <BasicLayout @clear-preferences-and-logout="handleLogout">
    <template #user-dropdown>
      <UserDropdown
        :avatar
        :menus
        :text="userStore.userInfo?.nickname || userStore.userInfo?.username"
        :description="userStore.userInfo?.username"
        tag-text="Pro"
        @logout="handleLogout"
      />
    </template>
    <template #notification>
      <Notification
        :dot="showDot"
        :notifications="notifications"
        @clear="
          async () => {
            await clearAllMessages();
            notifications = [];
          }
        "
        @make-all-read="
          async () => {
            await markAllMessageRead();
            notifications.forEach((item) => (item.isRead = true));
          }
        "
        @read="
          async (item: any) => {
            if (!item.isRead) {
              await markMessageRead(item.id);
              item.isRead = true;
            }
            if (item.link) {
              if (item.link.startsWith('http')) {
                globalThis.window.open(item.link, '_blank');
              } else {
                router.push(item.link);
              }
            }
          }
        "
        @view-all="
          () => {
            // 跳转到完整的消息中心页面
            router.push('/system/message/list');
          }
        "
      />
    </template>
    <template #extra>
      <AuthenticationLoginExpiredModal
        v-model:open="accessStore.loginExpired"
        :avatar
      >
        <LoginForm />
      </AuthenticationLoginExpiredModal>
    </template>
    <template #lock-screen>
      <LockScreen :avatar @to-login="handleLogout" />
    </template>
  </BasicLayout>
</template>
