<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';
import type { TrendSeries } from './use-analytics-data';

import { onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

const props = defineProps<{ series?: TrendSeries | null }>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

/**
 * 渲染月度柱状图（基于趋势序列）
 * @param s 趋势序列（若为空则回退示例数据）
 */
function renderVisits(s?: TrendSeries | null) {
  const labels = s?.labels ?? Array.from({ length: 12 }).map((_item, index) => `${index + 1}月`)
  const barData = s?.messageCreatedPerDay ?? [3000, 2000, 3333, 5000, 3200, 4200, 3200, 2100, 3000, 5100, 6000, 3200]
  renderEcharts({
    grid: { bottom: 0, containLabel: true, left: '1%', right: '1%', top: '2 %' },
    series: [{ barMaxWidth: 80, data: barData, type: 'bar' }],
    tooltip: { axisPointer: { lineStyle: { width: 1 } }, trigger: 'axis' },
    xAxis: { data: labels, type: 'category' },
    yAxis: { splitNumber: 4, type: 'value' },
  })
}

onMounted(() => renderVisits(props.series))
watch(() => props.series, (s) => renderVisits(s))
</script>

<template>
  <EchartsUI ref="chartRef" />
</template>
