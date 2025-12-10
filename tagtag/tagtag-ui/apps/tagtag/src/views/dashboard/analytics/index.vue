<script lang="ts" setup>
import type { AnalysisOverviewItem } from '@vben/common-ui';
import type { TabOption } from '@vben/types';

import { AnalysisChartCard } from '@vben/common-ui';
import {
  SvgBellIcon,
  SvgCakeIcon,
  SvgCardIcon,
  SvgDownloadIcon,
} from '@vben/icons';

import OverviewGrid from './components/OverviewGrid.vue';
import TrendsPanel from './components/TrendsPanel.vue';
import DistributionCard from './components/DistributionCard.vue';
import AnalyticsTrends from './analytics-trends.vue';
import AnalyticsVisitsData from './analytics-visits-data.vue';
import AnalyticsVisitsSales from './analytics-visits-sales.vue';
import AnalyticsVisitsSource from './analytics-visits-source.vue';
import AnalyticsVisits from './analytics-visits.vue';

import { $t } from '#/locales';
import { onMounted, ref } from 'vue';
import {
  loadOverview,
  loadTrends,
  loadFileDistribution,
  loadMessageDistribution,
  type TrendSeries,
  type DistributionItem,
} from './use-analytics-data';

const overviewItems = ref<AnalysisOverviewItem[]>([]);

const trends = ref<TrendSeries | null>(null);
const fileTypeDist = ref<DistributionItem[]>([]);
const messageStatusDist = ref<DistributionItem[]>([]);
const loading = ref(true);

const chartTabs: TabOption[] = [];

onMounted(async () => {
  try {
    const [ov, ts, fileDist, msgDist] = await Promise.all([
      loadOverview(),
      loadTrends(30),
      loadFileDistribution('type'),
      loadMessageDistribution('status'),
    ]);

    overviewItems.value = [
      {
        icon: SvgCardIcon,
        title: $t('page.dashboard.users'),
        totalTitle: $t('page.dashboard.usersTotal'),
        totalValue: ov.usersTotal,
        value: ts.userCreatedPerDay?.[ts.userCreatedPerDay.length - 1] ?? 0,
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
        value: fileDist.reduce((acc, cur) => acc + (cur?.value ?? 0), 0),
      },
      {
        icon: SvgCakeIcon,
        title: $t('page.dashboard.dicts'),
        totalTitle: $t('page.dashboard.dictsTotal'),
        totalValue: ov.dictDataTotal,
        value: ov.dictTypesTotal,
      },
    ];

    trends.value = ts;
    fileTypeDist.value = fileDist;
    messageStatusDist.value = msgDist;
  } catch (err) {
    // 可选：错误处理与回退静态数据
    console.error('Load analytics failed', err);
  } finally {
    loading.value = false;
  }
});

async function refreshTrends(days: number) {
  loading.value = true;
  try {
    const ts = await loadTrends(days);
    trends.value = ts;
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="p-5" v-loading="loading">
    <OverviewGrid :items="overviewItems" :updated-at="Date.now()" />
    <div class="mt-5">
      <TrendsPanel :series="trends" @rangeChange="refreshTrends" />
    </div>

    <div class="mt-5 w-full md:flex">
      <DistributionCard
        class="mt-5 md:mr-4 md:mt-0 md:w-1/3"
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
        class="mt-5 md:mr-4 md:mt-0 md:w-1/3"
        :title="$t('page.dashboard.fileDist')"
        :items="fileTypeDist"
        chart="pie"
        :dimensions="[
          { label: 'type', value: 'type' },
          { label: 'storage', value: 'storage' },
          { label: 'ext', value: 'ext' },
        ]"
        dimension="type"
        :onDimensionChange="async (v: string) => { fileTypeDist = await loadFileDistribution(v as any) }"
      />
      <DistributionCard
        class="mt-5 md:mt-0 md:w-1/3"
        :title="$t('page.dashboard.messageDist')"
        :items="messageStatusDist"
        chart="rose"
        :dimensions="[
          { label: 'status', value: 'status' },
          { label: 'type', value: 'type' },
        ]"
        dimension="status"
        :onDimensionChange="async (v: string) => { messageStatusDist = await loadMessageDistribution(v as any) }"
      />
    </div>
  </div>
</template>
