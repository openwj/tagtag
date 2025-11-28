import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

import { z } from '#/adapter/form';

export const columns: VxeGridProps['columns'] = [
  { align: 'left', type: 'checkbox', width: 50 },
  { title: '序号', type: 'seq', width: 50 },
  { field: 'id', title: 'ID', visible: false },
  {
    field: 'name',
    title: '角色名称',
    slots: { default: 'name' },
    sortable: true,
  },
  { field: 'code', title: '角色编码', sortable: true },
  {
    field: 'roleType',
    title: '角色类型',
    slots: { default: 'roleType' },
    sortable: true,
  },
  { field: 'sort', title: '排序', sortable: true },
  {
    field: 'status',
    title: '状态',
    slots: { default: 'status' },
    sortable: true,
  },
  { field: 'remark', title: '描述', showOverflow: 'tooltip' },
  {
    field: 'action',
    fixed: 'right',
    slots: { default: 'action' },
    title: '操作',
    width: 120,
  },
];

export const searchFormSchema: VbenFormProps['schema'] = [
  {
    component: 'Input',
    fieldName: 'name',
    label: '角色名称',
    componentProps: {
      placeholder: '请输入角色名称',
    },
  },
  {
    component: 'Input',
    fieldName: 'code',
    label: '角色编码',
    componentProps: {
      placeholder: '请输入角色编码',
    },
  },
  {
    component: 'Select',
    componentProps: {
      placeholder: '请选择角色类型',
      allowClear: true,
      options: [
        { label: '系统角色', value: 1 },
        { label: '业务角色', value: 2 },
      ],
    },
    fieldName: 'roleType',
    label: '角色类型',
  },
  {
    component: 'Select',
    componentProps: {
      placeholder: '请选择状态',
      allowClear: true,
      options: [
        { label: '启用', value: 1 },
        { label: '禁用', value: 0 },
      ],
    },
    fieldName: 'status',
    label: '状态',
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
    fieldName: 'name',
    label: '角色名称',
    rules: z
      .string()
      .min(2, { message: '角色名称至少需要2个字符' })
      .max(20, { message: '角色名称最多20个字符' }),
    componentProps: {
      placeholder: '请输入角色名称，如：系统管理员',
      maxlength: 20,
      showCount: true,
    },
  },
  {
    component: 'Input',
    fieldName: 'code',
    label: '角色编码',
    rules: z
      .string()
      .min(2, { message: '角色编码至少需要2个字符' })
      .max(50, { message: '角色编码最多50个字符' }),
    componentProps: {
      placeholder: '请输入角色编码，如：ADMIN、USER_MANAGER',
      maxlength: 50,
      showCount: true,
      style: { textTransform: 'uppercase' },
    },
  },
  {
    component: 'Switch',
    fieldName: 'status',
    label: '状态',
    defaultValue: 1,
    componentProps: {
      checkedChildren: '启用',
      unCheckedChildren: '禁用',
      checkedValue: 1,
      unCheckedValue: 0,
    },
    help: '启用后该角色可以被分配给用户',
  },
  {
    component: 'InputNumber',
    fieldName: 'sort',
    label: '排序',
    rules: z
      .number()
      .min(0, { message: '排序值不能小于0' })
      .max(9999, { message: '排序值不能大于9999' }),
    componentProps: {
      placeholder: '请输入排序值',
      min: 0,
      max: 9999,
      step: 1,
      precision: 0,
      class: 'w-full',
    },
    help: '数字越小越靠前，用于控制角色在列表中的显示顺序',
    defaultValue: 0,
  },
  {
    component: 'Select',
    fieldName: 'roleType',
    label: '角色类型',
    rules: z
      .number()
      .min(1, { message: '请选择角色类型' })
      .max(2, { message: '角色类型值不正确' }),
    componentProps: {
      placeholder: '请选择角色类型',
      options: [
        {
          label: '系统角色',
          value: 1,
          description: '系统内置角色，用于系统管理',
        },
        {
          label: '业务角色',
          value: 2,
          description: '业务相关角色，用于业务权限控制',
        },
      ],
      class: 'w-full',
    },
    help: '系统角色：用于系统管理，如超级管理员、系统管理员等；业务角色：用于业务权限控制，如内容编辑员、财务专员等',
    defaultValue: 2,
  },
  {
    component: 'Textarea',
    fieldName: 'remark',
    label: '角色描述',
    rules: z.string().max(200, { message: '角色描述最多200个字符' }).optional(),
    componentProps: {
      placeholder: '请输入角色描述，说明该角色的职责和权限范围',
      rows: 4,
      maxlength: 200,
      showCount: true,
      autoSize: { minRows: 3, maxRows: 6 },
      class: 'w-full',
    },
  },
];
