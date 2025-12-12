<script setup lang="ts">
const { seo } = useAppConfig()
const { locale } = useI18n()

const collection = computed(() => locale.value === 'en' ? 'docs_en' : 'docs')

const { data: navigation } = await useAsyncData('navigation', () => queryCollectionNavigation(collection.value), {
  watch: [locale]
})

const { data: files } = useLazyAsyncData('search', () => queryCollectionSearchSections(collection.value), {
  server: false,
  watch: [locale]
})

useHead({
  meta: [
    { name: 'viewport', content: 'width=device-width, initial-scale=1' }
  ],
  link: [
    { rel: 'icon', href: '/favicon.ico' }
  ],
  htmlAttrs: {
    lang: locale
  }
})

useSeoMeta({
  titleTemplate: `%s - ${seo?.siteName}`,
  ogSiteName: seo?.siteName,
  twitterCard: 'summary_large_image'
})

provide('navigation', navigation)
</script>

<template>
  <UApp>
    <NuxtLoadingIndicator />

    <AppHeader />

    <UMain>
      <NuxtLayout>
        <NuxtPage />
      </NuxtLayout>
    </UMain>

    <AppFooter />

    <ClientOnly>
      <LazyUContentSearch
        :files="files"
        :navigation="navigation"
      />
    </ClientOnly>
  </UApp>
</template>
