<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts'
import type { TrendSeries } from '../use-analytics-data'
import { ref, computed, watch, onMounted } from 'vue'
import { EchartsUI, useEcharts } from '@vben/plugins/echarts'
import { $t } from '#/locales'

const props = defineProps<{ series?: TrendSeries | null }>()
const emit = defineEmits<{ (e: 'rangeChange', days: number): void }>()

const ranges = [7, 30, 90]
const current = ref<number>(30)

/**
 * 计算合计与均值
 * @returns 文案对象
 */
function getSummary() {
  const s = props.series
  const sumUsers = (s?.userCreatedPerDay ?? []).reduce((a, b) => a + Number(b ?? 0), 0)
  const sumMsgs = (s?.messageCreatedPerDay ?? []).reduce((a, b) => a + Number(b ?? 0), 0)
  const days = s?.labels?.length ?? 0
  const avgUsers = days ? Math.round(sumUsers / days) : 0
  const avgMsgs = days ? Math.round(sumMsgs / days) : 0
  const nf = new Intl.NumberFormat()
  return {
    sumText: `${$t('page.dashboard.sumInRange')}: U ${nf.format(sumUsers)} / M ${nf.format(sumMsgs)}`,
    avgText: `${$t('page.dashboard.avgPerDay')}: U ${nf.format(avgUsers)} / M ${nf.format(avgMsgs)}`,
  }
}

const summary = computed(() => getSummary())

const chartRef = ref<EchartsUIType>()
const { renderEcharts } = useEcharts(chartRef)

/**
 * 渲染趋势图
 * @param s 趋势序列
 */
function renderTrends(s?: TrendSeries | null) {
  const labels = s?.labels ?? []
  const userData = s?.userCreatedPerDay ?? []
  const msgData = s?.messageCreatedPerDay ?? []
  renderEcharts({
    grid: { bottom: 0, containLabel: true, left: '1%', right: '1%', top: '8%' },
    series: [
      { areaStyle: {}, data: userData, itemStyle: { color: '#5ab1ef' }, smooth: true, type: 'line', name: 'Users' },
      { areaStyle: {}, data: msgData, itemStyle: { color: '#019680' }, smooth: true, type: 'line', name: 'Messages' },
    ],
    legend: { top: 0 },
    tooltip: { axisPointer: { lineStyle: { width: 1 } }, trigger: 'axis' },
    xAxis: { axisTick: { show: false }, boundaryGap: false, data: labels, splitLine: { show: true }, type: 'category' },
    yAxis: [{ axisTick: { show: false }, splitArea: { show: true }, splitNumber: 4, type: 'value' }],
  })
}

onMounted(() => renderTrends(props.series))
watch(() => props.series, (s) => renderTrends(s))

function onChangeRange(days: number) {
  current.value = days
  emit('rangeChange', days)
}
</script>

<template>
  <div>
    <div class="mb-2 flex items-center justify-between">
      <div class="text-sm text-muted-foreground">{{ summary.sumText }} · {{ summary.avgText }}</div>
      <div class="flex gap-2">
        <button
          v-for="d in ranges"
          :key="d"
          class="rounded border px-2 py-1 text-sm"
          :class="current === d ? 'bg-primary text-white' : ''"
          @click="onChangeRange(d)"
        >
          {{ d }}d
        </button>
      </div>
    </div>
    <EchartsUI ref="chartRef" />
  </div>
</template>

<style scoped>
.text-muted-foreground { color: rgba(0,0,0,0.6); }
@media (prefers-color-scheme: dark) { .text-muted-foreground { color: rgba(255,255,255,0.6); } }
.bg-primary { background-color: #4f69fd; }
</style>
