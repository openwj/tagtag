export default defineAppConfig({
  ui: {
    colors: {
      primary: 'indigo',
      neutral: 'zinc'
    },
    footer: {
      slots: {
        root: 'border-t border-default',
        left: 'text-sm text-muted'
      }
    },
    button: {
      slots: {
        base: 'cursor-pointer'
      }
    }
  },
  seo: {
    siteName: 'Tagtag Stater'
  },
  header: {
    title: 'Tagtag Starter',
    to: '/',
    logo: {
      alt: 'Tagtag Starter',
      light: '',
      dark: ''
    },
    search: true,
    colorMode: true,
    links: [
      {
        'label': 'navigation.home',
        'to': '/',
        'icon': 'i-heroicons-home',
        'aria-label': 'Home'
      },
      {
        'label': 'navigation.docs',
        'to': '/getting-started/introduction',
        'icon': 'i-heroicons-book-open',
        'aria-label': 'Docs'
      },
      {
        'label': 'navigation.announcements',
        'to': '/releases',
        'icon': 'i-heroicons-megaphone',
        'aria-label': 'Announcements'
      },
      {
        'icon': 'i-simple-icons-github',
        'to': 'https://github.com/admin/openwj/tagtag',
        'target': '_blank',
        'aria-label': 'GitHub'
      }
    ]
  },
  footer: {
    credits: `Copyright © ${new Date().getFullYear()} Tagtag Team`,
    colorMode: false,
    links: [{
      'icon': 'i-simple-icons-github',
      'to': 'https://github.com/admin/openwj/tagtag',
      'target': '_blank',
      'aria-label': 'Tagtag on GitHub'
    }]
  },
  toc: {
    title: 'On this page',
    bottom: {
      title: 'Community',
      edit: 'https://github.com/admin/openwj/tagtag/edit/main/docs/content',
      links: [{
        icon: 'i-lucide-star',
        label: 'Star on GitHub',
        to: 'https://github.com/admin/openwj/tagtag',
        target: '_blank'
      }]
    }
  }
})
