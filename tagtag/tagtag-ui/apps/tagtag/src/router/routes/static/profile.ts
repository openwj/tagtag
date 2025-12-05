import type { RouteRecordRaw } from 'vue-router';

import { $t } from '#/locales';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:user',
      order: -1,
      title: $t('page.auth.profile.title'),
      hideInMenu: true,
      keepAlive: true,
    },
    name: 'Profile',
    path: '/profile',
    component: () => import('#/views/_core/profile/index.vue'),
  },
];

export default routes;
