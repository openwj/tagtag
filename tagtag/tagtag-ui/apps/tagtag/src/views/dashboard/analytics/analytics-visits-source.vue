<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';

import type { DistributionItem } from '#/api/modules/system/statistics';

import { computed, onMounted, ref, watch } from 'vue';

import { EmptyIcon } from '@vben/icons';
import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

const props = defineProps<{ items?: DistributionItem[] }>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

const hasData = computed(() => !!props.items && props.items.length > 0);

/**
 * 基于主色生成色板（浅/深/偏移）
 * @param hsl 原始 HSL（三元形式，如 "212 100% 45%" 或完整 hsl(...))
 * @returns 颜色数组
 */
function buildPaletteFromPrimary(): string[] {
  const raw = getComputedStyle(document.documentElement)
    .getPropertyValue('--primary')
    .trim();
  if (!raw) return ['#5ab1ef', '#b6a2de', '#67e0e3', '#2ec7c9'];
  const nums = raw.match(/-?\d+(?:\.\d+)?/g) || [];
  const h = Number(nums[0] || 212);
  const s = Number(nums[1] || 100);
  const l = Number(nums[2] || 45);
  const clamp = (x: number) => Math.max(5, Math.min(95, x));
  return [
    `hsl(${h} ${s}% ${l}%)`,
    `hsl(${h} ${s}% ${clamp(l + 12)}%)`,
    `hsl(${h} ${Math.max(0, s - 8)}% ${clamp(l - 10)}%)`,
    `hsl(${(h + 30) % 360} ${s}% ${l}%)`,
  ];
}

/**
 * 渲染环形饼图（基于分布项）
 * @param items 分布项列表（若为空则不渲染）
 */
function renderPie(items?: DistributionItem[]) {
  if (!items || items.length === 0) {
    return;
  }
  const data = items.map((d) => ({ name: d.name, value: d.value }));
  renderEcharts({
    legend: { bottom: '2%', left: 'center' },
    series: [
      {
        animationDelay() {
          return Math.random() * 100;
        },
        animationEasing: 'exponentialInOut',
        animationType: 'scale',
        avoidLabelOverlap: false,
        color: buildPaletteFromPrimary(),
        data,
        emphasis: { label: { fontSize: '12', fontWeight: 'bold', show: true } },
        itemStyle: { borderRadius: 10, borderWidth: 2 },
        label: { position: 'center', show: false },
        labelLine: { show: false },
        radius: ['40%', '65%'],
        type: 'pie',
      },
    ],
    tooltip: { trigger: 'item' },
  });
}

onMounted(() => renderPie(props.items));
watch(
  () => props.items,
  (items) => renderPie(items),
);
</script>

<template>
  <div class="h-full w-full">
    <EchartsUI v-show="hasData" ref="chartRef" />
    <div
      v-if="!hasData"
      class="flex h-full w-full flex-col items-center justify-center text-muted-foreground"
    >
      <EmptyIcon class="size-12" />
      <span class="mt-2 text-sm">暂无数据</span>
    </div>
  </div>
</template>
