<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts'
import type { TrendSeries } from '#/api/modules/system/statistics'
import { ref, computed, watch, onMounted } from 'vue'
import { EchartsUI, useEcharts } from '@vben/plugins/echarts'
import { $t } from '#/locales'

const props = defineProps<{ series?: TrendSeries | null; loading?: boolean }>()
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
    grid: { bottom: 20, left: 40, right: 20, top: '12%' },
    series: [
      { areaStyle: {}, data: userData, itemStyle: { color: '#5ab1ef' }, smooth: true, type: 'line', name: $t('page.dashboard.users') },
      { areaStyle: {}, data: msgData, itemStyle: { color: '#019680' }, smooth: true, type: 'line', name: $t('page.dashboard.messages') },
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
  <div class="rounded-xl border bg-card p-5 shadow-sm relative">
    <div v-if="loading" class="absolute inset-0 z-10 flex items-center justify-center rounded-xl bg-card/50 backdrop-blur-sm">
      <div class="h-6 w-6 animate-spin rounded-full border-2 border-primary border-t-transparent"></div>
    </div>
    <div class="mb-5 flex items-center justify-between">
      <div class="flex flex-col">
        <span class="text-base font-semibold">趋势分析</span>
        <span class="mt-1 text-xs text-muted-foreground">{{ summary.sumText }} · {{ summary.avgText }}</span>
      </div>
      <div class="flex items-center rounded-lg border bg-muted p-1">
        <button
          v-for="d in ranges"
          :key="d"
          class="rounded-md px-3 py-1 text-xs font-medium transition-all"
          :class="current === d ? 'bg-white text-primary shadow-sm' : 'text-muted-foreground hover:bg-white/50'"
          @click="onChangeRange(d)"
        >
          {{ d }}天
        </button>
      </div>
    </div>
    <div class="h-[360px] w-full">
      <EchartsUI ref="chartRef" />
    </div>
  </div>
</template>

<style scoped>
</style>
