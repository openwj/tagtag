<script setup lang="ts">
defineProps<{
  icon?: string
  title: string
  description: string
  class?: string
}>()

const card = ref<HTMLElement | null>(null)
const mouseX = ref(0)
const mouseY = ref(0)

function handleMouseMove({ clientX, clientY }: MouseEvent) {
  if (!card.value) return
  const { left, top } = card.value.getBoundingClientRect()
  mouseX.value = clientX - left
  mouseY.value = clientY - top
}
</script>

<template>
  <div
    ref="card"
    class="group relative rounded-2xl bg-white dark:bg-neutral-900 border border-gray-200 dark:border-white/5 p-8 hover:border-transparent transition-colors duration-500 overflow-hidden"
    @mousemove="handleMouseMove"
  >
    <!-- Animated Border Gradient -->
    <div
      class="pointer-events-none absolute -inset-px rounded-2xl opacity-0 group-hover:opacity-100 transition duration-500"
      :style="{
        background: `radial-gradient(600px circle at ${mouseX}px ${mouseY}px, rgba(167, 139, 250, 0.4), transparent 40%)`
      }"
    />

    <!-- Inner Content Background -->
    <div class="absolute inset-px rounded-[15px] bg-gray-50/90 dark:bg-neutral-950/90 backdrop-blur-xl overflow-hidden">
      <!-- Noise Texture -->
      <div class="absolute inset-0 opacity-[0.03] mix-blend-overlay bg-[url('https://grainy-gradients.vercel.app/noise.svg')] bg-repeat" />

      <!-- Optional Grid Pattern (Always visible but subtle) -->
      <div class="absolute inset-0 bg-[linear-gradient(to_right,#80808012_1px,transparent_1px),linear-gradient(to_bottom,#80808012_1px,transparent_1px)] bg-[size:24px_24px] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)] opacity-20 group-hover:opacity-40 transition-opacity duration-700" />

      <!-- Hover Gradient Blob -->
      <div
        class="absolute inset-0 opacity-0 group-hover:opacity-100 transition duration-500"
        :style="{
          background: `radial-gradient(400px circle at ${mouseX}px ${mouseY}px, rgba(167, 139, 250, 0.05), transparent 40%)`
        }"
      />
    </div>

    <div class="relative z-10 h-full flex flex-col justify-between gap-6">
      <!-- Icon Container with Inner Shadow/Glow -->
      <div v-if="icon" class="w-14 h-14 rounded-xl flex items-center justify-center bg-gradient-to-br from-white/10 to-transparent border border-gray-200 dark:border-white/10 group-hover:border-indigo-500/30 group-hover:from-indigo-500/10 group-hover:scale-110 group-hover:-rotate-3 transition-all duration-500 shadow-[inset_0_1px_1px_rgba(255,255,255,0.1)] dark:shadow-[inset_0_1px_1px_rgba(255,255,255,0.05)]">
        <UIcon :name="icon" class="w-7 h-7 text-gray-500 dark:text-neutral-400 group-hover:text-indigo-500 dark:group-hover:text-cyan-400 transition-all duration-300 drop-shadow-[0_0_10px_rgba(167,139,250,0.5)] group-hover:scale-110" />
      </div>

      <div>
        <h3 class="text-xl font-bold text-gray-900 dark:text-white mb-3 tracking-tight group-hover:text-indigo-600 dark:group-hover:text-cyan-200 transition-colors duration-300">
          {{ title }}
        </h3>
        <p class="text-base text-gray-600 dark:text-neutral-400 leading-relaxed group-hover:text-gray-900 dark:group-hover:text-neutral-300 transition-colors duration-300">
          {{ description }}
        </p>
      </div>
    </div>
  </div>
</template>
