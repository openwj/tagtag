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
  <div class="relative group isolate rounded-xl background-gradient ring-1 ring-gray-200/80 dark:ring-gray-700/60 before:hidden before:lg:block before:absolute before:-inset-[3px] before:h-[calc(100%+6px)] before:w-[calc(100%+6px)] before:z-[-1] before:rounded-xl flex-1 flex flex-col shadow-md hover:shadow-2xl transition-all duration-500 hover:-translate-y-2 transform hover:scale-[1.015] active:scale-[0.995]">
    <div class="flex-1 flex flex-col overflow-hidden rounded-xl divide-y divide-gray-200/80 dark:divide-gray-700/60 bg-white/95 dark:bg-gray-900/90 hover:bg-gray-50/95 dark:hover:bg-gray-800/75 transition-all duration-400 relative backdrop-blur-sm border border-gray-100/50 dark:border-gray-800/50">
      <!-- 背景装饰元素 -->
      <div class="absolute top-0 right-0 w-32 h-32 bg-gradient-to-br from-primary-100/20 to-primary-200/0 dark:from-primary-900/20 dark:to-primary-800/0 rounded-bl-full pointer-events-none transition-all duration-600 group-hover:scale-125 group-hover:rotate-6" />
      <div class="absolute bottom-0 left-0 w-24 h-24 bg-gradient-to-tr from-primary-100/10 to-primary-200/0 dark:from-primary-900/10 dark:to-primary-800/0 rounded-tr-full pointer-events-none transition-all duration-600 group-hover:scale-125 group-hover:-rotate-6" />
      
      <div class="flex-1 flex flex-col gap-y-6 p-8 transition-all relative z-10 animate-fade-in">
        <div v-if="icon" class="pointer-events-none relative w-14 h-14 flex items-center justify-center rounded-xl bg-gradient-to-br from-gray-100/80 to-gray-200/80 dark:from-gray-800/80 dark:to-gray-900/80 group-hover:from-primary-50 group-hover:to-primary-200/80 dark:group-hover:from-primary-950/60 dark:group-hover:to-primary-900/60 transition-all duration-400 shadow-md border border-gray-200/30 dark:border-gray-700/30 animate-fade-in" style="animation-delay: 0.1s;">
          <div class="absolute inset-0 rounded-xl bg-primary/5 opacity-0 group-hover:opacity-100 transition-opacity duration-400" />
          <UIcon :name="icon" class="w-7 h-7 text-gray-600 dark:text-gray-300 group-hover:text-primary dark:group-hover:text-primary transition-all duration-400" />
        </div>
        
        <div class="flex flex-col gap-y-3 flex-1 justify-between">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white group-hover:text-primary dark:group-hover:text-primary transition-all duration-400 leading-tight animate-fade-in" style="animation-delay: 0.2s;">
            {{ title }}
          </h3>
          <p class="text-base text-gray-500 dark:text-gray-400 leading-relaxed transition-colors duration-400 animate-fade-in" style="animation-delay: 0.3s;">
            {{ description }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 添加渐入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.6s ease-out forwards;
  opacity: 0;
}
</style>

<style scoped>
.background-gradient::before {
  background: linear-gradient(
    180deg,
    rgba(0, 193, 106, 0) 0%,
    rgba(0, 193, 106, 0) 60%,
    var(--ui-primary) 100%
  );
  opacity: 0;
  transition: all 0.5s ease;
  transform: scale(1);
}

.group:hover .background-gradient::before {
  opacity: 0.15;
  transform: scale(1.02);
}
</style>
