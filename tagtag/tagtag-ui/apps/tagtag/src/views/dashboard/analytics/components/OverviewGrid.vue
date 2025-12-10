<script lang="ts" setup>
import type { AnalysisOverviewItem } from '@vben/common-ui'

defineProps<{ items: AnalysisOverviewItem[] }>()
</script>

<template>
  <div class="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
    <div
      v-for="(item, idx) in items"
      :key="idx"
      class="relative overflow-hidden rounded-xl border bg-card p-5 shadow-sm"
    >
      <div class="flex items-center justify-between">
        <div class="bg-primary/10 text-primary flex h-10 w-10 items-center justify-center rounded-lg">
          <component :is="item.icon" class="h-5 w-5" />
        </div>
        <div class="text-xs font-medium text-muted-foreground">{{ item.totalTitle }}</div>
      </div>
      <div class="mt-4 text-3xl font-bold tracking-tight">{{ item.totalValue }}</div>
      <div class="mt-2 flex items-center justify-between text-xs">
        <span class="text-muted-foreground">{{ item.title }}</span>
        <span class="font-medium" :class="Number(item.value) >= 0 ? 'text-green-600' : 'text-red-600'">
          {{ Number(item.value) > 0 ? '+' : '' }}{{ item.value }}
        </span>
      </div>
    </div>
  </div>
</template>

