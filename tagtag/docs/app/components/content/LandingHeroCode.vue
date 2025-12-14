<script setup lang="ts">
defineProps<{
  filename?: string
  language?: string
}>()
</script>

<template>
  <div class="relative group transition-all duration-400 hover:-translate-y-1.5">
    <!-- Glow effect -->
    <div class="absolute -inset-0.5 bg-gradient-to-r from-primary/60 to-primary-500/60 rounded-xl blur-md opacity-30 group-hover:opacity-50 transition-all duration-500 group-hover:duration-300" />
    
    <div class="relative rounded-xl bg-gray-900/98 ring-1 ring-white/15 backdrop-blur-sm shadow-2xl overflow-hidden transition-all duration-300">
      <!-- Window Controls -->
      <div class="flex items-center gap-2 px-4 py-3 border-b border-white/10 bg-gradient-to-r from-gray-800/90 to-gray-900/90 rounded-t-xl">
        <div class="flex gap-2">
          <div class="w-3 h-3 rounded-full bg-[#FF5F56] hover:bg-[#FF453A] transition-all duration-200 cursor-pointer shadow-md" />
          <div class="w-3 h-3 rounded-full bg-[#FFBD2E] hover:bg-[#FFC400] transition-all duration-200 cursor-pointer shadow-md" />
          <div class="w-3 h-3 rounded-full bg-[#27C93F] hover:bg-[#30D158] transition-all duration-200 cursor-pointer shadow-md" />
        </div>
        <div v-if="filename" class="ml-4 text-xs font-mono text-gray-300 bg-gray-800/60 px-3 py-1 rounded-full backdrop-blur-sm border border-gray-700/50">
          {{ filename }}
        </div>
        <div v-if="language" class="ml-auto text-xs font-mono text-gray-400 bg-gray-800/40 px-2 py-0.5 rounded-full backdrop-blur-sm border border-gray-700/40">
          {{ language }}
        </div>
      </div>

      <!-- Code Content -->
      <div class="p-5 overflow-x-auto text-sm font-mono leading-relaxed bg-gray-950/80">
        <div class="relative">
          <!-- 代码内容 -->
          <div class="relative"><slot /></div>
        </div>
      </div>
    </div>
  </div>
</template>
