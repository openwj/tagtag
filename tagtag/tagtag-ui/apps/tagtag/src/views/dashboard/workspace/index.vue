<script lang="ts" setup>
import type {
  AnalysisOverviewItem,
  WorkbenchQuickNavItem,
  WorkbenchTrendItem,
} from '@vben/common-ui';

import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import {
  AnalysisChartCard,
  WorkbenchHeader,
  WorkbenchQuickNav,
  WorkbenchTrends,
} from '@vben/common-ui';
import {
  SvgBellIcon,
  SvgCakeIcon,
  SvgCardIcon,
  SvgDownloadIcon,
} from '@vben/icons';
import { preferences } from '@vben/preferences';
import { useAccessStore, useUserStore } from '@vben/stores';
import { openWindow } from '@vben/utils';

import dayjs from 'dayjs';

import { getMessagePage } from '#/api/modules/system/message';
import {
  loadFileDistribution,
  loadOverview,
  type StatisticsOverview,
} from '#/api/modules/system/statistics';
import { $t } from '#/locales';

import AnalyticsVisitsSource from '../analytics/analytics-visits-source.vue';

const userStore = useUserStore();
const accessStore = useAccessStore();
const router = useRouter();

const overviewItems = ref<AnalysisOverviewItem[]>([]);
const trendItems = ref<WorkbenchTrendItem[]>([]);
const loading = ref(true);
const fileDistribution = ref<any[]>([]);
const statistics = ref<StatisticsOverview>();

// Quick Navigation Items
const quickNavItems = ref<WorkbenchQuickNavItem[]>([]);

const currentDate = computed(() => {
  return dayjs().format('YYYY年MM月DD日 dddd');
});

const greeting = computed(() => {
  const hour = dayjs().hour();
  if (hour < 6) return '夜深了，注意休息';
  if (hour < 9) return '早上好';
  if (hour < 12) return '上午好';
  if (hour < 14) return '中午好';
  if (hour < 17) return '下午好';
  if (hour < 19) return '傍晚好';
  return '晚上好';
});

const welcomeMessage = computed(() => {
  const hour = dayjs().hour();
  if (hour < 12) return '开始您一天的工作吧！';
  if (hour < 18) return '继续您一天的工作吧！';
  return '工作辛苦了，注意休息！';
});

onMounted(async () => {
  try {
    const [ov, msgs, fileDist] = await Promise.all([
      loadOverview(),
      getMessagePage({}, { pageNo: 1, pageSize: 5 }),
      loadFileDistribution('type'),
    ]);
    renderOverview(ov);
    statistics.value = ov;
    renderTrends(msgs?.list || []);
    fileDistribution.value = fileDist;
    renderQuickNav();
  } catch (error) {
    console.error('Failed to load dashboard data:', error);
  } finally {
    loading.value = false;
  }
});

function renderQuickNav() {
  const menus = accessStore.accessMenus;
  const flatMenus: any[] = [];

  function flatten(items: any[]) {
    for (const item of items) {
      if (item.children && item.children.length > 0) {
        flatten(item.children);
      } else if (item.path && !item.meta?.hideInMenu) {
        // Exclude Analytics and Dashboard/Workspace from Quick Nav
        const isExcluded =
          item.path === '/analytics' ||
          item.path === '/workspace' ||
          item.path.startsWith('/workspace/');
        if (!isExcluded) {
          flatMenus.push(item);
        }
      }
    }
  }
  flatten(menus);

  // Pick top 8 leaf menus
  const palette = [
    '#1fdaca',
    '#6b8afd',
    '#bf0c2c',
    '#e18525',
    '#3fb27f',
    '#4daf1bc9',
    '#00d8ff',
    '#EBD94E',
  ];

  quickNavItems.value = flatMenus.slice(0, 8).map((menu, index) => ({
    color: palette[index % palette.length],
    icon: menu.meta?.icon || 'ion:grid-outline',
    title: menu.meta?.title || menu.name,
    url: menu.path,
  }));
}

function renderOverview(ov: StatisticsOverview) {
  overviewItems.value = [
    {
      icon: SvgCardIcon,
      title: $t('page.dashboard.users'),
      totalTitle: $t('page.dashboard.usersTotal'),
      totalValue: ov.usersTotal,
      value: 0, // No daily increment data in overview, simplified
    },
    {
      icon: SvgBellIcon,
      title: $t('page.dashboard.messages'),
      totalTitle: $t('page.dashboard.messagesTotal'),
      totalValue: ov.messagesTotal,
      value: ov.unreadMessages,
    },
    {
      icon: SvgDownloadIcon,
      title: $t('page.dashboard.files'),
      totalTitle: $t('page.dashboard.filesTotal'),
      totalValue: ov.filesTotal,
      value: 0,
    },
    {
      icon: SvgCakeIcon,
      title: $t('page.dashboard.dicts'),
      totalTitle: $t('page.dashboard.dictsTotal'),
      totalValue: ov.dictDataTotal,
      value: ov.dictTypesTotal,
    },
  ];
}

function renderTrends(msgs: any[]) {
  trendItems.value = msgs.map((msg) => ({
    avatar: msg.avatar || 'svg:avatar-1',
    content: msg.content,
    date: msg.createTime,
    title: msg.senderName || msg.title || 'System',
  }));
}

function navTo(nav: WorkbenchQuickNavItem) {
  if (nav.url?.startsWith('http')) {
    openWindow(nav.url);
    return;
  }
  if (nav.url?.startsWith('/')) {
    router.push(nav.url);
  } else {
    console.warn(`Unknown URL for navigation item: ${nav.title} -> ${nav.url}`);
  }
}
</script>

<template>
  <div class="p-5" v-loading="loading">
    <WorkbenchHeader
      :avatar="userStore.userInfo?.avatar || preferences.app.defaultAvatar"
    >
      <template #title>
        {{ greeting }},
        {{ userStore.userInfo?.nickname || userStore.userInfo?.username }},
        {{ welcomeMessage }}
      </template>
      <template #description> {{ currentDate }} </template>
      <template #extra>
        <div class="flex flex-col justify-center text-right">
          <span class="text-foreground/80"> 未读消息 </span>
          <span class="text-2xl">{{ statistics?.unreadMessages ?? 0 }}/{{ statistics?.messagesTotal ?? 0 }}</span>
        </div>

        <div class="mx-12 flex flex-col justify-center text-right md:mx-16">
          <span class="text-foreground/80"> 用户 </span>
          <span class="text-2xl">{{ statistics?.usersTotal ?? 0 }}</span>
        </div>
        <div class="mr-4 flex flex-col justify-center text-right md:mr-10">
          <span class="text-foreground/80"> 文件 </span>
          <span class="text-2xl">{{ statistics?.filesTotal ?? 0 }}</span>
        </div>
      </template>
    </WorkbenchHeader>

    <!-- System Overview Cards -->
    <div class="mt-5 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
      <div
        v-for="(item, idx) in overviewItems"
        :key="idx"
        class="relative overflow-hidden rounded-xl border bg-card p-5 shadow-sm"
      >
        <div class="flex items-center justify-between">
          <div
            class="bg-primary/10 text-primary flex h-10 w-10 items-center justify-center rounded-lg"
          >
            <component :is="item.icon" class="h-5 w-5" />
          </div>
          <div class="text-xs font-medium text-muted-foreground">
            {{ item.totalTitle }}
          </div>
        </div>
        <div class="mt-4 text-3xl font-bold tracking-tight">
          {{ item.totalValue }}
        </div>
        <div class="mt-2 flex items-center justify-between text-xs">
          <span class="text-muted-foreground">{{ item.title }}</span>
          <span
            v-if="item.value > 0"
            class="font-medium text-green-600"
          >
            +{{ item.value }}
          </span>
        </div>
      </div>
    </div>

    <div class="mt-5 flex flex-col lg:flex-row gap-5">
      <div class="w-full lg:w-2/3 flex flex-col gap-5">
        <WorkbenchTrends
          :items="trendItems"
          title="最新消息"
        />
      </div>
      <div class="w-full lg:w-1/3 flex flex-col gap-5">
        <WorkbenchQuickNav
          :items="quickNavItems"
          title="快捷导航"
          @click="navTo"
        />
        <AnalysisChartCard title="文件分布">
          <AnalyticsVisitsSource :items="fileDistribution" />
        </AnalysisChartCard>
      </div>
    </div>
  </div>
</template>
