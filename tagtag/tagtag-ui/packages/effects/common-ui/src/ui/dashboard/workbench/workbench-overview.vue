<script setup lang="ts">
import type { AnalysisOverviewItem } from '../typing';

import { Card, CardContent, VbenIcon } from '@vben-core/shadcn-ui';

interface Props {
  items: AnalysisOverviewItem[];
}

defineOptions({
  name: 'WorkbenchOverview',
});

withDefaults(defineProps<Props>(), {
  items: () => [],
});
</script>

<template>
  <div class="grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
    <template v-for="(item, idx) in items" :key="idx">
      <Card>
        <CardContent class="p-5">
          <div class="flex items-center justify-between">
            <div
              class="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary"
            >
              <VbenIcon :icon="item.icon" class="size-5" />
            </div>
            <div class="text-xs font-medium text-muted-foreground">
              {{ item.totalTitle }}
            </div>
          </div>
          <div class="mt-4 text-3xl font-bold tracking-tight">
            {{ item.totalValue }}
          </div>
          <div class="mt-2 flex items-center justify-between text-xs">
            <span class="text-muted-foreground">{{ item.title }}</span>
            <span
              v-if="item.value !== 0"
              class="font-medium"
              :class="[item.value > 0 ? 'text-green-600' : 'text-red-600']"
            >
              {{ item.value > 0 ? '+' : '' }}{{ item.value }}
            </span>
          </div>
        </CardContent>
      </Card>
    </template>
  </div>
</template>
