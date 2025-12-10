<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts';
import type { DistributionItem } from './use-analytics-data';

import { onMounted, ref, watch } from 'vue';

import { EchartsUI, useEcharts } from '@vben/plugins/echarts';

const props = defineProps<{ items?: DistributionItem[] }>();

const chartRef = ref<EchartsUIType>();
const { renderEcharts } = useEcharts(chartRef);

/**
 * 渲染雷达图（基于分布项）
 * @param items 分布项列表（若为空则回退示例数据）
 */
function renderRadar(items?: DistributionItem[]) {
  const data = items && items.length > 0 ? items : [
    { name: '网页', value: 90 },
    { name: '移动端', value: 50 },
    { name: 'Ipad', value: 86 },
    { name: '客户端', value: 40 },
    { name: '第三方', value: 50 },
    { name: '其它', value: 20 },
  ]
  renderEcharts({
    legend: { bottom: 0, data: ['访问'] },
    radar: { indicator: data.map(d => ({ name: d.name })), radius: '60%', splitNumber: 8 },
    series: [{ areaStyle: { opacity: 1 }, data: [{ name: '访问', value: data.map(d => d.value) }], symbolSize: 0, type: 'radar' }],
    tooltip: {},
  })
}

onMounted(() => renderRadar(props.items))
watch(() => props.items, (items) => renderRadar(items))
</script>

<template>
  <EchartsUI ref="chartRef" />
</template>
