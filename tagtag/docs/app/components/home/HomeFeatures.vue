<script setup lang="ts">
const { t } = useI18n()

const features = computed(() => {
  return [
    {
      icon: 'i-lucide-layers',
      title: t('features.modular.title'),
      description: t('features.modular.description'),
      class: 'lg:col-span-2'
    },
    {
      icon: 'i-lucide-shield-check',
      title: t('features.enterprise.title'),
      description: t('features.enterprise.description'),
      class: 'lg:col-span-1'
    },
    {
      icon: 'i-lucide-file-code',
      title: t('features.contract.title'),
      description: t('features.contract.description'),
      class: 'lg:col-span-1'
    },
    {
      icon: 'i-lucide-box',
      title: t('features.modern.title'),
      description: t('features.modern.description'),
      class: 'lg:col-span-2'
    },
    {
      icon: 'i-lucide-plug',
      title: t('features.integrations.title'),
      description: t('features.integrations.description'),
      class: 'lg:col-span-1'
    },
    {
      icon: 'i-lucide-zap',
      title: t('features.dx.title'),
      description: t('features.dx.description'),
      class: 'lg:col-span-2'
    }
  ]
})
</script>

<template>
  <div class="py-24 sm:py-32 bg-gray-50 dark:bg-neutral-950 relative overflow-hidden">
    <div class="absolute inset-0 bg-gray-50 dark:bg-neutral-950" />

    <!-- Grid Background -->
    <div class="absolute inset-0 z-0 opacity-[0.15] pointer-events-none">
      <div class="absolute inset-0 bg-[linear-gradient(to_right,#4f4f4f2e_1px,transparent_1px),linear-gradient(to_bottom,#4f4f4f2e_1px,transparent_1px)] bg-[size:4rem_4rem] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)]" />
    </div>

    <!-- Background Gradient Blob (More subtle and positioned) -->
    <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[1000px] h-[1000px] bg-indigo-500/5 blur-[120px] rounded-full pointer-events-none" />

    <div class="relative mx-auto max-w-7xl px-6 lg:px-8 z-10">
      <div class="mx-auto max-w-2xl text-center mb-20">
        <h2 class="text-base font-semibold leading-7 text-indigo-600 dark:text-cyan-400 uppercase tracking-wide">
          {{ t('features.title') }}
        </h2>
        <p class="mt-2 text-3xl font-bold tracking-tight sm:text-4xl bg-gradient-to-r from-gray-900 via-gray-900 to-gray-600 dark:from-white dark:via-white dark:to-white/60 bg-clip-text text-transparent">
          {{ t('features.subtitle') }}
        </p>
        <p class="mt-6 text-lg leading-8 text-gray-600 dark:text-neutral-400">
          {{ t('features.description') }}
        </p>
      </div>

      <div class="mx-auto mt-16 max-w-2xl sm:mt-20 lg:mt-24 lg:max-w-none">
        <div class="grid grid-cols-1 gap-6 lg:grid-cols-3">
          <HomeSpotlightCard
            v-for="feature in features"
            :key="feature.title"
            v-bind="feature"
          />
        </div>
      </div>
    </div>
  </div>
</template>
