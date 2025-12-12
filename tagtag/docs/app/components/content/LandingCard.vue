<script setup lang="ts">
defineProps<{
  icon?: string
  title: string
  description: string
  to?: string
  color?: string
}>()
</script>

<template>
  <div class="relative group isolate rounded-xl background-gradient ring-1 ring-gray-200 dark:ring-gray-800 before:hidden before:lg:block before:absolute before:-inset-[2px] before:h-[calc(100%+4px)] before:w-[calc(100%+4px)] before:z-[-1] before:rounded-[13px] flex-1 flex flex-col shadow-sm hover:shadow-lg transition-shadow duration-300">
    <div class="flex-1 flex flex-col overflow-hidden rounded-xl divide-y divide-gray-200 dark:divide-gray-800 bg-white dark:bg-gray-900 hover:bg-gray-50 dark:hover:bg-gray-800/50 transition-colors">
      <div class="flex-1 flex flex-col gap-y-4 p-8">
        <div v-if="icon" class="pointer-events-none relative w-12 h-12 flex items-center justify-center rounded-lg bg-gray-100/50 dark:bg-gray-800/50 group-hover:bg-primary-50 dark:group-hover:bg-primary-950/30 transition-colors">
          <UIcon :name="icon" class="w-6 h-6 text-gray-500 dark:text-gray-400 group-hover:text-primary dark:group-hover:text-primary transition-colors" />
        </div>
        
        <div class="flex flex-col gap-y-2">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white group-hover:text-primary dark:group-hover:text-primary transition-colors">
            {{ title }}
          </h3>
          <p class="text-base text-gray-500 dark:text-gray-400 leading-relaxed">
            {{ description }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.background-gradient::before {
  background: linear-gradient(
    180deg,
    rgba(56, 189, 248, 0) 0%,
    rgba(56, 189, 248, 0) 50%,
    var(--ui-primary) 100%
  );
  opacity: 0;
  transition: opacity 0.5s;
}

.group:hover .background-gradient::before {
  opacity: 0.1;
}
</style>
