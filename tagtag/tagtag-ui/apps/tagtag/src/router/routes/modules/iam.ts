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
      {
        name: 'IamUser',
        path: '/iam/user',
        component: () => import('#/views/modules/iam/user/index.vue'),
        meta: {
          icon: 'lucide:user',
          title: '用户管理',
        },
      },
      {
        name: 'IamRole',
        path: '/iam/role',
        component: () => import('#/views/modules/iam/role/index.vue'),
        meta: {
          icon: 'lucide:shield-question',
          title: '角色管理',
        },
      },
    ],
  },
];

export default routes;
