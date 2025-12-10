<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';
import type { DistributionItem } from './use-analytics-data';

import { onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

const props = defineProps<{ items?: DistributionItem[] }>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

/**
 * 渲染玫瑰图（基于分布项）
 * @param items 分布项列表（若为空则回退示例数据）
 */
function renderRose(items?: DistributionItem[]) {
  const data = (items && items.length > 0 ? items : [
    { name: '外包', value: 500 },
    { name: '定制', value: 310 },
    { name: '技术支持', value: 274 },
    { name: '远程', value: 400 },
  ]).map(d => ({ name: d.name, value: d.value }))
  renderEcharts({
    series: [{
      animationDelay() { return Math.random() * 400 },
      animationEasing: 'exponentialInOut',
      animationType: 'scale',
      center: ['50%', '50%'],
      color: ['#5ab1ef', '#b6a2de', '#67e0e3', '#2ec7c9'],
      data: data.toSorted((a, b) => (a.value as number) - (b.value as number)),
      radius: '80%',
      roseType: 'radius',
      type: 'pie',
    }],
    tooltip: { trigger: 'item' },
  })
}

onMounted(() => renderRose(props.items))
watch(() => props.items, (items) => renderRose(items))
</script>

<template>
  <EchartsUI ref="chartRef" />
</template>
