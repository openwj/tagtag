<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';
import type { DistributionItem } from './use-analytics-data';

import { onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

const props = defineProps<{ items?: DistributionItem[] }>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

/**
 * 渲染环形饼图（基于分布项）
 * @param items 分布项列表（若为空则回退示例数据）
 */
function renderPie(items?: DistributionItem[]) {
  const data = (items && items.length > 0 ? items : [
    { name: '搜索引擎', value: 1048 },
    { name: '直接访问', value: 735 },
    { name: '邮件营销', value: 580 },
    { name: '联盟广告', value: 484 },
  ]).map(d => ({ name: d.name, value: d.value }))
  renderEcharts({
    legend: { bottom: '2%', left: 'center' },
    series: [{
      animationDelay() { return Math.random() * 100 },
      animationEasing: 'exponentialInOut',
      animationType: 'scale',
      avoidLabelOverlap: false,
      color: ['#5ab1ef', '#b6a2de', '#67e0e3', '#2ec7c9'],
      data,
      emphasis: { label: { fontSize: '12', fontWeight: 'bold', show: true } },
      itemStyle: { borderRadius: 10, borderWidth: 2 },
      label: { position: 'center', show: false },
      labelLine: { show: false },
      radius: ['40%', '65%'],
      type: 'pie',
    }],
    tooltip: { trigger: 'item' },
  })
}

onMounted(() => renderPie(props.items))
watch(() => props.items, (items) => renderPie(items))
</script>

<template>
  <EchartsUI ref="chartRef" />
</template>
