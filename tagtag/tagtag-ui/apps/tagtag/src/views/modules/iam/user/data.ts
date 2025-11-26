import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { ref } from 'vue';

// 注意：getDeptTree 和 getRoleList 在 FormDrawer.vue 中使用

// 创建响应式的部门数据
export const deptTreeData = ref([]);

export const columns: VxeGridProps['columns'] = [
  { align: 'left', type: 'checkbox', width: 50 },
  { title: '序号', type: 'seq', width: 50 },
  { field: 'id', title: 'ID', visible: false },
  {
    field: 'username',
    title: '用户',
    width: 220,
    slots: { default: 'userCell' },
  },
  { field: 'nickname', title: '昵称', visible: false },
  { field: 'email', title: '邮箱', width: 200, showOverflow: true },
  { field: 'phone', title: '手机号', width: 140, showOverflow: true },
  { field: 'gender', title: '性别', width: 80 },
  { field: 'deptName', title: '部门', width: 140 },
  { field: 'position', title: '职位', width: 120, showOverflow: true },
  { field: 'status', title: '状态', width: 120, slots: { default: 'status' } },
  {
    field: 'lastLoginTime',
    title: '最后登录时间',
    width: 180,
    showOverflow: true,
  },
  { field: 'createTime', title: '创建时间', width: 180, showOverflow: true },
  {
    field: 'action',
    fixed: 'right',
    slots: { default: 'action' },
    title: '操作',
    width: 140,
  },
];

export const searchFormSchema: VbenFormProps['schema'] = [
  {
    component: 'Input',
    fieldName: 'username',
    label: '用户名',
    componentProps: {
      placeholder: '请输入用户名',
    },
  },
  {
    component: 'Input',
    fieldName: 'nickname',
    label: '昵称',
    componentProps: {
      placeholder: '请输入昵称',
    },
  },
  {
    component: 'Input',
    fieldName: 'email',
    label: '邮箱',
    componentProps: {
      placeholder: '请输入邮箱',
    },
  },
  {
    component: 'Input',
    fieldName: 'phone',
    label: '手机号',
    componentProps: {
      placeholder: '请输入手机号',
    },
  },
  {
    component: 'Select',
    fieldName: 'gender',
    label: '性别',
    componentProps: {
      placeholder: '请选择性别',
      allowClear: true,
      options: [
        { label: '男', value: 1 },
        { label: '女', value: 2 },
        { label: '未知', value: 0 },
      ],
    },
  },
  {
    component: 'TreeSelect',
    fieldName: 'deptId',
    label: '部门',
    componentProps: {
      placeholder: '请选择部门',
      allowClear: true,
      treeData: deptTreeData,
      fieldNames: {
        children: 'children',
        label: 'name',
        value: 'id',
      },
    },
  },
  {
    component: 'Select',
    fieldName: 'status',
    label: '状态',
    componentProps: {
      placeholder: '请选择状态',
      allowClear: true,
      options: [
        { label: '启用', value: 1 },
        { label: '禁用', value: 0 },
      ],
    },
  },
];

export const formSchema: VbenFormProps['schema'] = [
  {
    component: 'Input',
    componentProps: {
      placeholder: '请输入',
    },
    dependencies: {
      show: false,
      // 随意一个字段改变时，都会触发
      triggerFields: ['id'],
    },
    fieldName: 'id',
    label: 'ID',
  },
  {
    component: 'Input',
    componentProps: {
      placeholder: '请输入用户名（3-20字符，仅支持字母数字下划线）',
    },
    fieldName: 'username',
    label: '用户名',
    rules: 'required',
  },
  {
    component: 'InputPassword',
    componentProps: {
      placeholder: '请输入密码（6-20字符）',
    },
    dependencies: {
      show: (values) => !values.id, // 仅在新增时显示
      triggerFields: ['id'],
    },
    fieldName: 'password',
    label: '密码',
    rules: 'required',
  },
  {
    component: 'Input',
    componentProps: {
      placeholder: '请输入昵称（最多50字符）',
    },
    fieldName: 'nickname',
    label: '昵称',
    rules: 'required',
  },
  {
    component: 'TreeSelect',
    componentProps: {
      placeholder: '请选择',
      class: 'w-full',
      treeData: deptTreeData,
      treeLine: true,
      allowClear: true,
      treeDefaultExpandAll: true,
      showSearch: true,
      fieldNames: {
        children: 'children',
        label: 'name',
        value: 'id',
      },
    },
    fieldName: 'deptId',
    label: '所属部门',
    rules: 'required',
  },
  {
    component: 'Select',
    componentProps: {
      allowClear: true,
      filterOption: true,
      class: 'w-full',
      options: [
        {
          label: '男',
          value: 1,
        },
        {
          label: '女',
          value: 2,
        },
        {
          label: '未知',
          value: 0,
        },
      ],
      placeholder: '请选择',
      showSearch: true,
    },
    fieldName: 'gender',
    label: '性别',
  },
  {
    component: 'Input',
    componentProps: {
      placeholder: '请输入手机号码',
    },
    fieldName: 'phone',
    label: '手机号码',
    rules: 'required',
  },
  {
    component: 'Switch',
    componentProps: {
      class: 'w-auto',
      checkedChildren: '启用',
      unCheckedChildren: '禁用',
      checkedValue: 1,
      unCheckedValue: 0,
    },
    fieldName: 'status',
    label: '状态',
    rules: 'required',
    defaultValue: 1,
  },
  {
    component: 'Input',
    componentProps: {
      placeholder: '请输入邮箱地址（最多100字符）',
      type: 'email',
    },
    fieldName: 'email',
    label: '电子邮箱',
    rules: 'required',
  },

  {
    component: 'DatePicker',
    componentProps: {
      placeholder: '请选择生日',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD HH:mm:ss',
      showTime: false,
    },
    fieldName: 'birthday',
    label: '生日',
  },
  {
    component: 'Input',
    componentProps: {
      placeholder: '请输入职位（最多100字符）',
    },
    fieldName: 'position',
    label: '职位',
  },
  {
    component: 'Switch',
    componentProps: {
      class: 'w-auto',
      checkedChildren: '是',
      unCheckedChildren: '否',
      checkedValue: 1,
      unCheckedValue: 0,
    },
    fieldName: 'isAdmin',
    label: '超级管理员',
    defaultValue: 0,
  },
  // {
  //   component: 'DatePicker',
  //   componentProps: {
  //     placeholder: '请选择账号过期时间',
  //     format: 'YYYY-MM-DD HH:mm:ss',
  //     valueFormat: 'YYYY-MM-DD HH:mm:ss',
  //     showTime: true,
  //   },
  //   fieldName: 'expireTime',
  //   label: '过期时间',
  // },
  {
    component: 'Textarea',
    componentProps: {
      placeholder: '请输入备注（最多500字符）',
      rows: 3,
    },
    fieldName: 'remark',
    label: '备注',
  },
];
