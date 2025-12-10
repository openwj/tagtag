<script setup lang="ts">
import type { WorkbenchQuickNavItem } from '../typing';

import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  VbenIcon,
} from '@vben-core/shadcn-ui';

interface Props {
  items?: WorkbenchQuickNavItem[];
  title: string;
}

defineOptions({
  name: 'WorkbenchQuickNav',
});

withDefaults(defineProps<Props>(), {
  items: () => [],
});

defineEmits(['click']);
</script>

<template>
  <Card>
    <CardHeader class="py-4">
      <CardTitle class="text-lg">{{ title }}</CardTitle>
    </CardHeader>
    <CardContent class="p-4 pt-0">
      <div class="grid grid-cols-2 gap-4 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 xl:grid-cols-8">
        <template v-for="(item, index) in items" :key="item.title">
          <div
            class="group flex flex-col items-center justify-center rounded-lg border bg-card p-4 transition-all duration-300 hover:-translate-y-1 hover:border-primary hover:shadow-md cursor-pointer"
            @click="$emit('click', item)"
          >
            <div
              class="flex h-12 w-12 items-center justify-center rounded-full bg-primary/5 transition-colors group-hover:bg-primary/10"
            >
              <VbenIcon
                :icon="item.icon"
                class="size-6 text-primary transition-transform duration-300 group-hover:scale-110"
              />
            </div>
            <span class="mt-3 truncate text-sm font-medium text-foreground/80 group-hover:text-primary">
              {{ item.title }}
            </span>
          </div>
        </template>
      </div>
    </CardContent>
  </Card>
</template>
