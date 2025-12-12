<script lang="ts" setup>
import type { AnalysisOverviewItem } from '@vben/common-ui';

import type {
  DistributionItem,
  TrendSeries,
} from '#/api/modules/system/statistics';

import { markRaw, onMounted, ref } from 'vue';

import {
  SvgBellIcon,
  SvgCakeIcon,
  SvgCardIcon,
  SvgDownloadIcon,
} from '@vben/icons';

import {
  loadFileDistribution,
  loadMessageDistribution,
  loadOverview,
  loadTrends,
} from '#/api/modules/system/statistics';
// 移除历史未使用模块的 import
import { $t } from '#/locales';

import DistributionCard from './components/DistributionCard.vue';
import OverviewGrid from './components/OverviewGrid.vue';
import TrendsPanel from './components/TrendsPanel.vue';

const overviewItems = ref<AnalysisOverviewItem[]>([]);

const trends = ref<null | TrendSeries>(null);
const fileTypeDist = ref<DistributionItem[]>([]);
const messageStatusDist = ref<DistributionItem[]>([]);
const loading = ref(true);
const trendLoading = ref(false);

onMounted(async () => {
  try {
    const [ov, ts, fileDist, msgDist] = await Promise.all([
      loadOverview(),
      loadTrends(30),
      loadFileDistribution('type'),
      loadMessageDistribution('status'),
    ]);
    // ... (rest is same)

    overviewItems.value = [
      {
        icon: markRaw(SvgCardIcon),
        title: $t('page.dashboard.users'),
        totalTitle: $t('page.dashboard.usersTotal'),
        totalValue: ov.usersTotal,
        value: ts.userCreatedPerDay?.[ts.userCreatedPerDay.length - 1] ?? 0,
      },
      {
        icon: markRaw(SvgBellIcon),
        title: $t('page.dashboard.messages'),
        totalTitle: $t('page.dashboard.messagesTotal'),
        totalValue: ov.messagesTotal,
        value: ov.unreadMessages,
      },
      {
        icon: markRaw(SvgDownloadIcon),
        title: $t('page.dashboard.files'),
        totalTitle: $t('page.dashboard.filesTotal'),
        totalValue: ov.filesTotal,
        value: fileDist.reduce((acc, cur) => acc + (cur?.value ?? 0), 0),
      },
      {
        icon: markRaw(SvgCakeIcon),
        title: $t('page.dashboard.dicts'),
        totalTitle: $t('page.dashboard.dictsTotal'),
        totalValue: ov.dictDataTotal,
        value: ov.dictTypesTotal,
      },
    ];

    trends.value = ts;
    fileTypeDist.value = fileDist;
    messageStatusDist.value = msgDist;
  } catch (error) {
    // 可选：错误处理与回退静态数据
    console.error('Load analytics failed', error);
  } finally {
    loading.value = false;
  }
});

async function refreshTrends(days: number) {
  trendLoading.value = true;
  try {
    const ts = await loadTrends(days);
    trends.value = ts;
  } finally {
    trendLoading.value = false;
  }
}

async function onFileTypeChange(v: string) {
  fileTypeDist.value = await loadFileDistribution(v as any);
}

async function onMsgStatusChange(v: string) {
  messageStatusDist.value = await loadMessageDistribution(v as any);
}
</script>

<template>
  <div class="p-5" v-loading="loading">
    <OverviewGrid :items="overviewItems" :updated-at="Date.now()" />
    <div class="mt-5">
      <TrendsPanel
        :series="trends"
        :loading="trendLoading"
        @range-change="refreshTrends"
      />
    </div>

    <div class="mt-5 grid grid-cols-1 gap-5 md:grid-cols-3">
      <DistributionCard
        :title="$t('page.dashboard.coverageRadar')"
        :items="[
          { name: 'users', value: overviewItems[0]?.totalValue || 0 },
          { name: 'messages', value: overviewItems[1]?.totalValue || 0 },
          { name: 'files', value: overviewItems[2]?.totalValue || 0 },
          { name: 'dicts', value: overviewItems[3]?.totalValue || 0 },
        ]"
        chart="radar"
      />
      <DistributionCard
        :title="$t('page.dashboard.fileDist')"
        :items="fileTypeDist"
        chart="pie"
        :dimensions="[
          { label: 'type', value: 'type' },
          { label: 'storage', value: 'storage' },
          { label: 'ext', value: 'ext' },
        ]"
        dimension="type"
        :on-dimension-change="onFileTypeChange"
      />
      <DistributionCard
        :title="$t('page.dashboard.messageDist')"
        :items="messageStatusDist"
        chart="rose"
        :dimensions="[
          { label: 'status', value: 'status' },
          { label: 'type', value: 'type' },
        ]"
        dimension="status"
        :on-dimension-change="onMsgStatusChange"
      />
    </div>
  </div>
</template>
