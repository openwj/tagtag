// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: [
    '@nuxt/eslint',
    '@nuxt/image',
    '@nuxt/ui',
    '@nuxt/content',
    'nuxt-og-image',
    'nuxt-llms',
    '@nuxtjs/mcp-toolkit',
    '@nuxtjs/i18n'
  ],

  devtools: {
    enabled: true
  },

  css: ['~/assets/css/main.css'],

  colorMode: {
    preference: 'light'
  },

  content: {
    build: {
      markdown: {
        toc: {
          searchDepth: 1
        },
        highlight: {
          langs: [
            'json',
            'js',
            'ts',
            'html',
            'css',
            'vue',
            'shell',
            'mdc',
            'md',
            'yaml',
            'java',
            'sql',
            'xml'
          ]
        }
      }
    }
  },

  ui: {
    fonts: false
  },

  experimental: {
    asyncContext: true
  },

  compatibilityDate: '2024-07-11',

  nitro: {
    prerender: {
      routes: ['/'],
      crawlLinks: true,
      autoSubfolderIndex: false
    }
  },

  eslint: {
    config: {
      stylistic: {
        commaDangle: 'never',
        braceStyle: '1tbs'
      }
    }
  },

  i18n: {
    locales: [
      { code: 'zh', language: 'zh-CN', name: '简体中文', file: 'zh.json' },
      { code: 'en', language: 'en-US', name: 'English', file: 'en.json' }
    ],
    defaultLocale: 'zh',
    strategy: 'prefix_except_default',
    langDir: 'locales',
    detectBrowserLanguage: {
      useCookie: true,
      cookieKey: 'i18n_redirected',
      redirectOn: 'root'
    }
  },

  icon: {
    provider: 'server',
    serverBundle: {
      collections: ['lucide', 'simple-icons', 'vscode-icons']
    }
  },

  llms: {
    domain: 'https://tagtag.cn',
    title: 'Tagtag Documentation',
    description:
      'Documentation for Tagtag - A modular, enterprise-grade Java Spring Boot admin framework.',
    full: {
      title: 'Tagtag - Full Documentation',
      description: 'The complete documentation for Tagtag framework.'
    },
    sections: [
      {
        title: 'Getting Started',
        contentCollection: 'docs',
        contentFilters: [
          { field: 'path', operator: 'LIKE', value: '/getting-started%' }
        ]
      },
      {
        title: 'Architecture',
        contentCollection: 'docs',
        contentFilters: [
          { field: 'path', operator: 'LIKE', value: '/architecture%' }
        ]
      },
      {
        title: 'Developer Guide',
        contentCollection: 'docs',
        contentFilters: [
          { field: 'path', operator: 'LIKE', value: '/developer-guide%' }
        ]
      },
      {
        title: 'Modules',
        contentCollection: 'docs',
        contentFilters: [
          { field: 'path', operator: 'LIKE', value: '/modules%' }
        ]
      },
      {
        title: 'Deployment',
        contentCollection: 'docs',
        contentFilters: [
          { field: 'path', operator: 'LIKE', value: '/deployment%' }
        ]
      },
      {
        title: 'Maintenance',
        contentCollection: 'docs',
        contentFilters: [
          { field: 'path', operator: 'LIKE', value: '/maintenance%' }
        ]
      }
    ]
  },

  mcp: {
    name: 'Docs template'
  }
})
