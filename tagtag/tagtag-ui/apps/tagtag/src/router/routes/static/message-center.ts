import type { RouteRecordRaw } from 'vue-router';

import { BasicLayout } from '#/layouts';

const routes: RouteRecordRaw[] = [
  {
    component: BasicLayout,
    meta: {
      icon: 'lucide:bell',
      order: 10,
      title: '消息中心',
      hideInMenu: true,
    },
    name: 'Message',
    path: '/message',
    redirect: '/message/center',
    children: [
      {
        name: 'MessageCenterList',
        path: 'center',
        component: () => import('#/views/modules/system/message/center.vue'),
        meta: {
          hideInMenu: true,
          title: '消息列表',
        },
      },
      {
        meta: {
          activePath: '/message/center',
          hideInMenu: true,
          keepAlive: true,
          title: '消息详情',
        },
        name: 'MessageCenterDetail',
        path: 'detail/:id',
        component: () => import('#/views/modules/system/message/detail.vue'),
      },
    ],
  },
];

export default routes;
