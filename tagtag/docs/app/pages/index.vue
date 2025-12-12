<script setup lang="ts">
const { locale, t } = useI18n()
const route = useRoute()

// Fetch landing page data for SEO metadata only
const { data: page } = await useAsyncData(route.path, () => {
  const collection = locale.value === 'en' ? 'landing_en' : 'landing'
  return queryCollection(collection).path(route.path).first()
})

const title = computed(() => page.value?.seo?.title || page.value?.title || 'Tagtag - Generic Admin Backend')
const description = computed(() => page.value?.seo?.description || page.value?.description || 'A modular, enterprise-grade Java Spring Boot admin framework.')

useSeoMeta({
  titleTemplate: '',
  title,
  ogTitle: title,
  description,
  ogDescription: description,
  ogImage: 'https://ui.nuxt.com/assets/templates/nuxt/docs-light.png',
  twitterImage: 'https://ui.nuxt.com/assets/templates/nuxt/docs-light.png'
})

const techStack = [
  { name: 'Spring Boot', icon: 'i-simple-icons-springboot' },
  { name: 'Vue.js', icon: 'i-simple-icons-vuedotjs' },
  { name: 'TypeScript', icon: 'i-simple-icons-typescript' },
  { name: 'Nuxt', icon: 'i-simple-icons-nuxtdotjs' },
  { name: 'Tailwind CSS', icon: 'i-simple-icons-tailwindcss' },
  { name: 'Redis', icon: 'i-simple-icons-redis' },
  { name: 'MySQL', icon: 'i-simple-icons-mysql' },
  { name: 'PostgreSQL', icon: 'i-simple-icons-postgresql' },
  { name: 'Docker', icon: 'i-simple-icons-docker' },
  { name: 'Nginx', icon: 'i-simple-icons-nginx' },
  { name: 'Maven', icon: 'i-simple-icons-apachemaven' },
  { name: 'Vite', icon: 'i-simple-icons-vite' }
]

// Quick Start Logic
const quickStartTab = ref('backend')
const activeStep = ref(1)
const isCopied = ref(false)

const quickStartSteps = computed(() => {
  const steps = {
    backend: [
      {
        id: 1,
        title: t('quick_start.steps.clone.title') || 'Clone',
        description: t('quick_start.steps.clone.desc') || 'Clone the repository',
        command: 'git clone https://github.com/admin/openwj/tagtag.git'
      },
      {
        id: 2,
        title: t('quick_start.steps.install.title') || 'Install',
        description: t('quick_start.steps.install.desc') || 'Install dependencies',
        command: 'mvn clean install'
      },
      {
        id: 3,
        title: t('quick_start.steps.run.title') || 'Run',
        description: t('quick_start.steps.run.desc') || 'Start the application',
        command: 'cd tagtag-start && mvn spring-boot:run'
      }
    ],
    frontend: [
      {
        id: 1,
        title: t('quick_start.steps.clone.title') || 'Clone',
        description: t('quick_start.steps.clone.desc') || 'Clone the repository',
        command: 'git clone https://github.com/admin/openwj/tagtag.git'
      },
      {
        id: 2,
        title: t('quick_start.steps.install.title') || 'Install',
        description: t('quick_start.steps.install.desc') || 'Install dependencies',
        command: 'cd tagtag/tagtag-ui && pnpm install'
      },
      {
        id: 3,
        title: t('quick_start.steps.run.title') || 'Run',
        description: t('quick_start.steps.run.desc') || 'Start the application',
        command: 'pnpm dev:tagtag'
      }
    ]
  }
  return steps[quickStartTab.value as keyof typeof steps]
})

function copyCommand(command: string) {
  navigator.clipboard.writeText(command)
  isCopied.value = true
  setTimeout(() => {
    isCopied.value = false
  }, 2000)
}
</script>

<template>
  <div class="relative">
    <HomeHero />

    <!-- Tech Stack Section (Marquee) -->
    <div class="border-y border-gray-200 dark:border-white/10 bg-white dark:bg-neutral-900/50 py-16 relative overflow-hidden">
      <!-- Subtle Background Pattern -->
      <div class="absolute inset-0 opacity-[0.4] pointer-events-none bg-[radial-gradient(#e5e7eb_1px,transparent_1px)] [background-size:16px_16px] [mask-image:radial-gradient(ellipse_50%_50%_at_50%_50%,#000_70%,transparent_100%)] dark:bg-[radial-gradient(#ffffff15_1px,transparent_1px)]" />

      <!-- Grain Texture Overlay (Consistent with Hero) -->
      <div class="absolute inset-0 z-[1] opacity-[0.03] mix-blend-overlay pointer-events-none bg-[url('https://grainy-gradients.vercel.app/noise.svg')] bg-repeat" />

      <div class="relative z-10">
        <div class="text-center mb-10 px-6">
          <h2 class="text-sm font-semibold leading-7 text-indigo-600 dark:text-indigo-400 uppercase tracking-wide">
            {{ t('tech_stack.label') }}
          </h2>
          <p class="mt-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-3xl">
            {{ t('tech_stack.title') }}
          </p>
        </div>

        <!-- Marquee Container -->
        <div class="relative flex overflow-x-hidden group mask-linear-fade">
          <div class="animate-marquee flex gap-16 py-4 whitespace-nowrap px-8">
            <div
              v-for="tech in techStack"
              :key="tech.name"
              class="flex flex-col items-center justify-center group/item relative"
            >
              <div class="w-20 h-20 rounded-2xl bg-white dark:bg-white/5 border border-gray-200 dark:border-white/10 shadow-sm flex items-center justify-center transition-all duration-300 group-hover/item:scale-110 group-hover/item:shadow-lg group-hover/item:shadow-indigo-500/10 group-hover/item:border-indigo-500/20 dark:group-hover/item:border-indigo-400/30">
                <UIcon
                  :name="tech.icon"
                  class="h-10 w-10 text-gray-400 group-hover/item:text-indigo-500 dark:group-hover/item:text-white transition-all duration-300"
                />
              </div>
              <span class="mt-3 text-xs font-medium text-gray-500 dark:text-gray-400 group-hover/item:text-gray-900 dark:group-hover/item:text-white transition-colors duration-300">
                {{ tech.name }}
              </span>
            </div>
          </div>

          <!-- Duplicate for infinite loop -->
          <div
            class="animate-marquee flex gap-16 py-4 whitespace-nowrap px-8"
            aria-hidden="true"
          >
            <div
              v-for="tech in techStack"
              :key="`${tech.name}-duplicate`"
              class="flex flex-col items-center justify-center group/item relative"
            >
              <div class="w-20 h-20 rounded-2xl bg-white dark:bg-white/5 border border-gray-200 dark:border-white/10 shadow-sm flex items-center justify-center transition-all duration-300 group-hover/item:scale-110 group-hover/item:shadow-lg group-hover/item:shadow-indigo-500/10 group-hover/item:border-indigo-500/20 dark:group-hover/item:border-indigo-400/30">
                <UIcon
                  :name="tech.icon"
                  class="h-10 w-10 text-gray-400 group-hover/item:text-indigo-500 dark:group-hover/item:text-white transition-all duration-300"
                />
              </div>
              <span class="mt-3 text-xs font-medium text-gray-500 dark:text-gray-400 group-hover/item:text-gray-900 dark:group-hover/item:text-white transition-colors duration-300">
                {{ tech.name }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <HomeFeatures />

    <!-- Quick Start Section (Interactive) -->
    <div class="py-24 sm:py-32 bg-white dark:bg-neutral-950 border-t border-gray-200 dark:border-white/10 relative overflow-hidden">
      <!-- Grain Texture -->
      <div class="absolute inset-0 opacity-[0.03] mix-blend-overlay pointer-events-none bg-[url('https://grainy-gradients.vercel.app/noise.svg')] bg-repeat" />

      <!-- Background Glow -->
      <div class="absolute right-0 top-1/2 -translate-y-1/2 w-[500px] h-[500px] bg-indigo-500/5 blur-[120px] rounded-full pointer-events-none" />

      <div class="mx-auto max-w-7xl px-6 lg:px-8 relative z-10">
        <div class="mx-auto max-w-2xl text-center mb-10">
          <h2 class="text-3xl font-bold tracking-tight text-gray-900 dark:text-white sm:text-4xl">
            {{ t('quick_start.title') }}
          </h2>
          <p class="mt-4 text-lg leading-8 text-gray-600 dark:text-gray-400">
            {{ t('quick_start.subtitle') }}
          </p>
        </div>

        <!-- Toggle Switch -->
        <div class="flex justify-center mb-12">
          <div class="bg-gray-100 dark:bg-white/5 p-1 rounded-full flex gap-1 relative border border-gray-200 dark:border-white/10">
            <button
              class="relative z-10 px-6 py-2 rounded-full text-sm font-medium transition-colors duration-300"
              :class="quickStartTab === 'backend' ? 'text-gray-900 dark:text-white' : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'"
              @click="quickStartTab = 'backend'; activeStep = 1"
            >
              {{ t('quick_start.backend') }}
            </button>
            <button
              class="relative z-10 px-6 py-2 rounded-full text-sm font-medium transition-colors duration-300"
              :class="quickStartTab === 'frontend' ? 'text-gray-900 dark:text-white' : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'"
              @click="quickStartTab = 'frontend'; activeStep = 1"
            >
              {{ t('quick_start.frontend') }}
            </button>

            <!-- Sliding Background -->
            <div
              class="absolute top-1 bottom-1 bg-white dark:bg-white/10 rounded-full shadow-sm transition-all duration-300 ease-in-out"
              :class="quickStartTab === 'backend' ? 'left-1 w-[calc(50%-4px)]' : 'left-[calc(50%+2px)] w-[calc(50%-4px)]'"
            />
          </div>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center max-w-5xl mx-auto">
          <!-- Left: Steps Navigation -->
          <div class="space-y-4">
            <div
              v-for="step in quickStartSteps"
              :key="step.id"
              class="relative p-6 rounded-2xl border transition-all duration-300 cursor-pointer group"
              :class="activeStep === step.id ? 'bg-white dark:bg-white/5 border-indigo-500/50 shadow-lg shadow-indigo-500/10 scale-105' : 'bg-transparent border-transparent hover:bg-gray-50 dark:hover:bg-white/5'"
              @mouseenter="activeStep = step.id"
            >
              <div class="flex items-center gap-4">
                <div
                  class="shrink-0 w-10 h-10 rounded-full flex items-center justify-center font-bold text-sm transition-all duration-300"
                  :class="activeStep === step.id ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-500/30' : 'bg-gray-100 dark:bg-white/10 text-gray-500 dark:text-gray-400'"
                >
                  {{ step.id }}
                </div>
                <div>
                  <h3
                    class="font-semibold text-lg transition-colors duration-300"
                    :class="activeStep === step.id ? 'text-gray-900 dark:text-white' : 'text-gray-600 dark:text-gray-400'"
                  >
                    {{ step.title }}
                  </h3>
                  <p class="text-sm text-gray-500 dark:text-gray-500 mt-1">
                    {{ step.description }}
                  </p>
                </div>
                <div
                  class="ml-auto transition-opacity duration-300"
                  :class="activeStep === step.id ? 'opacity-100' : 'opacity-0'"
                >
                  <UIcon
                    name="i-lucide-arrow-right"
                    class="w-5 h-5 text-indigo-500"
                  />
                </div>
              </div>
            </div>
          </div>

          <!-- Right: Terminal Window -->
          <div class="relative group perspective-1000">
            <div class="relative bg-[#1e1e1e] rounded-xl border border-white/10 shadow-2xl overflow-hidden transform-gpu transition-all duration-500 group-hover:rotate-y-1 group-hover:rotate-x-1">
              <!-- Window Controls -->
              <div class="flex items-center justify-between px-4 py-3 bg-white/5 border-b border-white/5">
                <div class="flex gap-2">
                  <div class="w-3 h-3 rounded-full bg-[#ff5f56]" />
                  <div class="w-3 h-3 rounded-full bg-[#ffbd2e]" />
                  <div class="w-3 h-3 rounded-full bg-[#27c93f]" />
                </div>
                <div class="text-xs text-gray-500 font-mono">
                  bash
                </div>
                <div class="w-14 text-right">
                  <UIcon
                    name="i-lucide-terminal"
                    class="w-4 h-4 text-gray-600"
                  />
                </div>
              </div>

              <!-- Terminal Content -->
              <div class="p-6 font-mono text-sm min-h-60 flex flex-col justify-center relative">
                <!-- Copy Button -->
                <button
                  class="absolute top-4 right-4 p-2 rounded-lg bg-white/5 hover:bg-white/10 text-gray-400 hover:text-white transition-all duration-200 opacity-0 group-hover:opacity-100"
                  @click="copyCommand(quickStartSteps[activeStep - 1].command)"
                >
                  <UIcon
                    :name="isCopied ? 'i-lucide-check' : 'i-lucide-copy'"
                    class="w-4 h-4"
                  />
                </button>

                <div
                  v-for="step in quickStartSteps"
                  :key="step.id"
                  class="transition-all duration-500"
                  :class="activeStep === step.id ? 'opacity-100 blur-none scale-100' : 'opacity-30 blur-[1px] scale-95 select-none'"
                >
                  <div
                    v-if="activeStep === step.id"
                    class="flex gap-3 items-start mb-2"
                  >
                    <span class="text-indigo-400 select-none">$</span>
                    <span class="text-gray-200 break-all typing-effect">{{ step.command }}</span>
                  </div>
                </div>

                <div class="mt-4 flex gap-2 items-center animate-pulse">
                  <span class="text-indigo-400 select-none">➜</span>
                  <span class="w-2 h-4 bg-gray-500 block" />
                </div>
              </div>
            </div>

            <!-- Glow Effect -->
            <div class="absolute -inset-4 bg-indigo-500/20 blur-2xl -z-10 opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
          </div>
        </div>
      </div>
    </div>

    <!-- CTA Section -->
    <div class="relative py-32 sm:py-40 overflow-hidden bg-white dark:bg-neutral-950">
      <!-- Grain Texture -->
      <div class="absolute inset-0 opacity-[0.03] mix-blend-overlay pointer-events-none bg-[url('https://grainy-gradients.vercel.app/noise.svg')] bg-repeat" />

      <div class="absolute inset-0 bg-[linear-gradient(to_right,#80808012_1px,transparent_1px),linear-gradient(to_bottom,#80808012_1px,transparent_1px)] bg-[size:24px_24px] [mask-image:radial-gradient(ellipse_60%_50%_at_50%_0%,#000_70%,transparent_100%)] opacity-50" />

      <!-- Gradient Blobs (Dual Tone) -->
      <div class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-gradient-to-tr from-indigo-500/20 to-cyan-500/20 blur-[100px] rounded-full pointer-events-none animate-pulse-slow" />

      <div class="relative z-10 mx-auto max-w-7xl px-6 lg:px-8 text-center">
        <h2 class="text-5xl font-extrabold tracking-tight text-gray-900 dark:text-white sm:text-7xl mb-8 leading-tight">
          {{ t('cta.title') }}
          <span class="block mt-4 bg-linear-to-r from-indigo-600 to-cyan-500 dark:from-indigo-400 dark:to-cyan-300 bg-clip-text text-transparent">{{ t('cta.subtitle') }}</span>
        </h2>
        <p class="mx-auto mt-8 max-w-2xl text-xl leading-relaxed text-gray-600 dark:text-gray-300 mb-14">
          {{ t('cta.description') }}
        </p>
        <div class="flex items-center justify-center gap-x-6">
          <UButton
            to="/getting-started/introduction"
            size="xl"
            color="primary"
            variant="solid"
            class="px-8 py-4 text-lg font-semibold rounded-full shadow-xl shadow-indigo-500/20 hover:shadow-indigo-500/40 hover:-translate-y-1 transition-all duration-300"
            :label="t('cta.get_started')"
          />
          <UButton
            to="https://github.com/admin/openwj/tagtag"
            target="_blank"
            size="xl"
            variant="ghost"
            class="px-8 py-4 text-lg font-semibold rounded-full hover:bg-gray-100 dark:hover:bg-white/10"
            label="GitHub"
            icon="i-simple-icons-github"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-marquee {
  animation: marquee 60s linear infinite;
}

.group:hover .animate-marquee {
  animation-play-state: paused;
}

@keyframes marquee {
  0% {
    transform: translateX(0);
  }
  100% {
    transform: translateX(-100%);
  }
}

.mask-linear-fade {
  mask-image: linear-gradient(to right, transparent, black 10%, black 90%, transparent);
  -webkit-mask-image: linear-gradient(to right, transparent, black 10%, black 90%, transparent);
}

.perspective-1000 {
  perspective: 1000px;
}
</style>
