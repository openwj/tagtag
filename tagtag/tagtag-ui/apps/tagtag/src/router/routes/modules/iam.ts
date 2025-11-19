import type { RouteRecordRaw } from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    meta: {
      icon: 'lucide:users',
      order: 2,
      title: 'IAM',
    },
    name: 'iam',
    path: '/iam',
    children: [
      {
        name: 'IamDept',
        path: '/iam/dept',
        component: () => import('#/views/modules/iam/dept/index.vue'),
        meta: {
          icon: 'lucide:shield',
          title: '部门管理',
        },
      },
    ],
  },
];

export default routes;
