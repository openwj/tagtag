<script setup lang="ts">
import type { ContentNavigationItem } from '@nuxt/content'

const navigation = inject<Ref<ContentNavigationItem[]>>('navigation')

const { header } = useAppConfig()
const { locale, locales, setLocale, t } = useI18n()
const localePath = useLocalePath()

const navLinks = computed(() => {
  return (header?.links || [])
    .filter((link: any) => link.label)
    .map((link: any) => ({
      ...link,
      label: link.label ? t(link.label) : '',
      to: localePath(link.to)
    }))
})

const iconLinks = computed(() => {
  return (header?.links || []).filter((link: any) => !link.label && link.icon)
})
</script>

<template>
  <UHeader
    :ui="{ center: 'flex-1' }"
    :to="header?.to || '/'"
  >
    <UContentSearchButton
      v-if="header?.search"
      :collapsed="false"
      class="w-full"
    />

    <template
      v-if="header?.logo?.dark || header?.logo?.light || header?.title"
      #title
    >
      <div class="flex items-center gap-2">
        <UColorModeImage
          v-if="header?.logo?.dark || header?.logo?.light"
          :light="header?.logo?.light!"
          :dark="header?.logo?.dark!"
          :alt="header?.logo?.alt"
          class="h-6 w-auto shrink-0"
        />

        <span v-if="header?.title" class="font-bold">
          {{ header.title }}
        </span>
      </div>
    </template>

    <template
      v-else
      #left
    >
      <NuxtLink :to="header?.to || '/'">
        <AppLogo class="w-auto h-6 shrink-0" />
      </NuxtLink>
    </template>

    <template #right>
      <template v-if="navLinks.length">
        <UButton
          v-for="(link, index) of navLinks"
          :key="index"
          v-bind="{ color: 'neutral', variant: 'ghost', ...link }"
          class="hidden lg:inline-flex"
        />
      </template>

      <UContentSearchButton
        v-if="header?.search"
        :label="$t('search.label')"
        class="lg:hidden"
      />

      <UColorModeButton v-if="header?.colorMode" />

      <UDropdownMenu
        v-if="locales.length > 1"
        :items="locales.map(l => ({
          label: l.name,
          onSelect: () => setLocale(l.code),
          checked: locale === l.code,
          type: 'checkbox'
        }))"
      >
        <UButton
          color="neutral"
          variant="ghost"
          icon="i-lucide-languages"
        />
      </UDropdownMenu>

      <template v-if="iconLinks.length">
        <UButton
          v-for="(link, index) of iconLinks"
          :key="index"
          v-bind="{ color: 'neutral', variant: 'ghost', ...link }"
        />
      </template>
    </template>

    <template #body>
      <UContentNavigation
        highlight
        :navigation="navigation"
      />
    </template>
  </UHeader>
</template>
