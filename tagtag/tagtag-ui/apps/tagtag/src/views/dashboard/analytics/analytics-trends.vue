<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';
import type { TrendSeries } from './use-analytics-data';

import { onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

const props = defineProps<{ series?: TrendSeries | null }>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

/**
 * 渲染趋势图
 * @param s 趋势序列（若为空则回退示例数据）
 */
function renderTrends(s?: TrendSeries | null) {
  const labels = s?.labels ?? Array.from({ length: 18 }).map((_item, index) => `${index + 6}:00`)
  const userData = s?.userCreatedPerDay ?? [111, 2000, 6000, 16000, 33333, 55555, 64000, 33333, 18000, 36000, 70000, 42444, 23222, 13000, 8000, 4000, 1200, 333]
  const msgData = s?.messageCreatedPerDay ?? [33, 66, 88, 333, 3333, 6200, 20000, 3000, 1200, 13000, 22000, 11000, 2221, 1201, 390, 198, 60, 30]
  renderEcharts({
    grid: { bottom: 0, containLabel: true, left: '1%', right: '1%', top: '2 %' },
    series: [
      { areaStyle: {}, data: userData, itemStyle: { color: '#5ab1ef' }, smooth: true, type: 'line' },
      { areaStyle: {}, data: msgData, itemStyle: { color: '#019680' }, smooth: true, type: 'line' },
    ],
    tooltip: { axisPointer: { lineStyle: { color: '#019680', width: 1 } }, trigger: 'axis' },
    xAxis: {
      axisTick: { show: false },
      boundaryGap: false,
      data: labels,
      splitLine: { lineStyle: { type: 'solid', width: 1 }, show: true },
      type: 'category',
    },
    yAxis: [{ axisTick: { show: false }, splitArea: { show: true }, splitNumber: 4, type: 'value' }],
  })
}

onMounted(() => renderTrends(props.series))
watch(() => props.series, (s) => renderTrends(s))
</script>

<template>
  <EchartsUI ref="chartRef" />
</template>
