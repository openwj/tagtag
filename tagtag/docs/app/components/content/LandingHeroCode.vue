<script setup lang="ts">
defineProps<{
  filename?: string
  language?: string
}>()
</script>

<template>
  <div class="relative group">
    <!-- Glow effect -->
    <div class="absolute -inset-1 bg-gradient-to-r from-primary to-cyan-500 rounded-2xl blur opacity-25 group-hover:opacity-50 transition duration-1000 group-hover:duration-200" />
    
    <div class="relative rounded-xl bg-gray-900/90 ring-1 ring-white/10 backdrop-blur">
      <!-- Window Controls -->
      <div class="flex items-center gap-2 px-4 py-3 border-b border-white/5 bg-white/5 rounded-t-xl">
        <div class="flex gap-1.5">
          <div class="w-3 h-3 rounded-full bg-[#FF5F56]" />
          <div class="w-3 h-3 rounded-full bg-[#FFBD2E]" />
          <div class="w-3 h-3 rounded-full bg-[#27C93F]" />
        </div>
        <div v-if="filename" class="ml-4 text-xs font-mono text-gray-500">
          {{ filename }}
        </div>
      </div>

      <!-- Code Content -->
      <div class="p-4 overflow-x-auto text-sm font-mono">
        <slot />
      </div>
    </div>
  </div>
</template>
