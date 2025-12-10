<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts'
import type { DistributionItem } from '../use-analytics-data'
import { ref, watch, onMounted } from 'vue'
import { EchartsUI, useEcharts } from '@vben/plugins/echarts'

const props = defineProps<{
  title: string
  items?: DistributionItem[]
  chart: 'radar' | 'pie' | 'rose'
  onRefresh?: () => void
  dimensions?: Array<{ label: string; value: string }>
  dimension?: string
  onDimensionChange?: (v: string) => void
}>()

const chartRef = ref<EchartsUIType>()
const { renderEcharts } = useEcharts(chartRef)

/**
 * 渲染分布图
 * @param items 分布项
 */
function render(items?: DistributionItem[]) {
  const data = (items ?? []).map(d => ({ name: d.name, value: d.value }))
  if (props.chart === 'radar') {
    renderEcharts({ legend: { bottom: 0 }, radar: { indicator: data.map(d => ({ name: d.name })) }, series: [{ data: [{ name: 'value', value: data.map(d => Number(d.value) || 0) }], type: 'radar' }] })
    return
  }
  if (props.chart === 'pie') {
    renderEcharts({ series: [{ data, type: 'pie', radius: ['40%', '65%'] }], tooltip: { trigger: 'item' } })
    return
  }
  renderEcharts({ series: [{ data: data.toSorted((a, b) => Number(a.value) - Number(b.value)), type: 'pie', roseType: 'radius' }], tooltip: { trigger: 'item' } })
}

onMounted(() => render(props.items))
watch(() => props.items, (items) => render(items))

function onSelectDim(e: Event) {
  const v = (e.target as HTMLSelectElement).value
  props.onDimensionChange?.(v)
}
</script>

<template>
  <div class="rounded-lg border p-4">
    <div class="mb-2 flex items-center justify-between">
      <div class="text-sm font-semibold">{{ title }}</div>
      <div class="flex items-center gap-2">
        <select v-if="dimensions?.length" class="rounded border px-2 py-1 text-sm" :value="dimension" @change="onSelectDim">
          <option v-for="d in dimensions" :key="d.value" :value="d.value">{{ d.label }}</option>
        </select>
        <button v-if="onRefresh" class="rounded border px-2 py-1 text-sm" @click="onRefresh?.()">Refresh</button>
      </div>
    </div>
    <EchartsUI ref="chartRef" />
  </div>
  
</template>

