<script lang="ts" setup>
import type { AnalysisOverviewItem } from '@vben/common-ui'
import { computed } from 'vue'
import { $t } from '#/locales'

const props = defineProps<{ items: AnalysisOverviewItem[]; updatedAt?: string | number }>()

/**
 * 计算“最后更新时间”展示文案
 * @returns 文案字符串
 */
function getUpdatedText(): string {
  const ts = props.updatedAt
  if (!ts) return ''
  const d = typeof ts === 'number' ? new Date(ts) : new Date(String(ts))
  return `${$t('page.dashboard.lastUpdated')}: ${d.toLocaleString()}`
}

const updatedText = computed(() => getUpdatedText())
</script>

<template>
  <div class="space-y-3">
    <div class="text-sm text-muted-foreground" v-if="updatedText">{{ updatedText }}</div>
    <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
      <div
        v-for="(item, idx) in items"
        :key="idx"
        class="rounded-lg border bg-card p-4 shadow-sm transition hover:shadow"
      >
        <div class="flex items-center justify-between">
          <component :is="item.icon" class="h-6 w-6" />
          <div class="text-xs text-muted-foreground">{{ item.totalTitle }}</div>
        </div>
        <div class="mt-2 text-2xl font-bold">{{ item.totalValue }}</div>
        <div class="mt-1 text-xs text-muted-foreground">{{ item.title }}</div>
        <div class="mt-1 text-sm">{{ item.value }}</div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.text-muted-foreground { color: rgba(0,0,0,0.6); }
@media (prefers-color-scheme: dark) { .text-muted-foreground { color: rgba(255,255,255,0.6); } }
.bg-card { background-color: var(--card-bg, rgba(255,255,255,0.7)); }
@media (prefers-color-scheme: dark) { .bg-card { background-color: var(--card-bg-dark, rgba(0,0,0,0.2)); } }
</style>

