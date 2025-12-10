<script lang="ts" setup>
import type { EchartsUIType } from '@vben/plugins/echarts'
import type { DistributionItem } from '#/api/modules/system/statistics'
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
    const maxVal = Math.max(...data.map(d => Number(d.value) || 0), 0)
    // Ensure max is a multiple of 4 (splitNumber) to avoid unreadable ticks
    const baseMax = Math.max(maxVal > 0 ? maxVal * 1.2 : 0, 10)
    const max = Math.ceil(baseMax / 4) * 4

    renderEcharts({
      legend: { bottom: 0 },
      radar: {
        indicator: data.map(d => ({ name: d.name, max })),
        splitArea: { show: true, areaStyle: { color: ['transparent'] } },
        splitNumber: 4,
        axisName: {
          color: 'hsl(var(--muted-foreground))'
        }
      },
      series: [{
        data: [{ name: 'Value', value: data.map(d => Number(d.value) || 0) }],
        type: 'radar',
        symbolSize: 0,
        areaStyle: { opacity: 0.5 }
      }],
      tooltip: { trigger: 'item' }
    })
    return
  }
  if (props.chart === 'pie') {
    renderEcharts({
      series: [{
        data,
        type: 'pie',
        radius: ['40%', '65%'],
        itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 1 },
        label: { show: false }
      }],
      tooltip: { trigger: 'item' }
    })
    return
  }
  // Rose
  renderEcharts({
    series: [{
      data: data.toSorted((a, b) => Number(a.value) - Number(b.value)),
      type: 'pie',
      roseType: 'radius',
      radius: ['10%', '65%'],
      itemStyle: { borderRadius: 4 },
      label: { show: false }
    }],
    tooltip: { trigger: 'item' }
  })
}

onMounted(() => render(props.items))
watch(() => props.items, (items) => render(items))

function onSelectDim(e: Event) {
  const v = (e.target as HTMLSelectElement).value
  props.onDimensionChange?.(v)
}
</script>

<template>
  <div class="flex flex-col rounded-xl border bg-card p-5 shadow-sm">
    <div class="mb-5 flex items-center justify-between">
      <div class="text-base font-semibold">{{ title }}</div>
      <div class="flex items-center gap-2">
        <div class="relative" v-if="dimensions?.length">
          <select
            class="appearance-none rounded-md border bg-transparent px-3 py-1 pr-8 text-xs font-medium focus:border-primary focus:outline-none focus:ring-1 focus:ring-primary"
            :value="dimension"
            @change="onSelectDim"
          >
            <option v-for="d in dimensions" :key="d.value" :value="d.value">{{ d.label }}</option>
          </select>
           <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-muted-foreground">
              <svg class="h-3 w-3 fill-current" viewBox="0 0 20 20"><path d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clip-rule="evenodd" fill-rule="evenodd"></path></svg>
           </div>
        </div>
        <button
          v-if="onRefresh"
          class="rounded-md border bg-transparent p-1.5 text-muted-foreground hover:bg-muted hover:text-primary transition-colors"
          @click="onRefresh?.()"
          title="刷新"
        >
          <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-refresh-cw"><path d="M3 12a9 9 0 0 1 9-9 9.75 9.75 0 0 1 6.74 2.74L21 8"/><path d="M21 3v5h-5"/><path d="M21 12a9 9 0 0 1-9 9 9.75 9.75 0 0 1-6.74-2.74L3 16"/><path d="M8 16H3v5"/></svg>
        </button>
      </div>
    </div>
    <div class="flex-1 min-h-[300px]">
      <EchartsUI ref="chartRef" />
    </div>
  </div>
</template>

<style scoped>
</style>
