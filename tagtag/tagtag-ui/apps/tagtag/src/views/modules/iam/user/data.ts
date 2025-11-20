import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

export const columns: VxeGridProps['columns'] = [
  { field: 'id', title: 'ID', visible: false },
  { field: 'username', title: '用户名', width: 160 },
  { field: 'nickname', title: '姓名', width: 140 },
  { field: 'employeeNo', title: '工号', width: 120 },
  { field: 'jobTitle', title: '职务', width: 120 },
  { field: 'email', title: '邮箱', width: 180 },
  { field: 'mobile', title: '手机号', width: 140 },
  { field: 'gender', title: '性别', width: 80 },
  { field: 'status', title: '状态', slots: { default: 'status' }, width: 90 },
  { field: 'remark', title: '备注' },
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
    fieldName: 'username',
    label: '用户名',
    componentProps: { placeholder: '请输入用户名' },
  },
  {
    component: 'Input',
    fieldName: 'nickname',
    label: '姓名',
    componentProps: { placeholder: '请输入姓名' },
  },
  {
    component: 'Input',
    fieldName: 'employeeNo',
    label: '工号',
    componentProps: { placeholder: '请输入工号' },
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
    dependencies: { show: false, triggerFields: ['id'] },
    fieldName: 'id',
    label: 'ID',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入用户名' },
    fieldName: 'username',
    label: '用户名',
    rules: 'required',
  },
  {
    component: 'InputPassword',
    componentProps: { placeholder: '请输入密码' },
    fieldName: 'password',
    label: '密码',
    dependencies: {
      show: ({ formModel }) => !formModel?.id,
      triggerFields: ['id'],
    },
    rules: 'required',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入姓名/昵称' },
    fieldName: 'nickname',
    label: '姓名',
  },
  {
    component: 'TreeSelect',
    componentProps: {
      placeholder: '请选择部门',
      class: 'w-full',
      treeData: [],
      treeLine: true,
      allowClear: true,
      treeDefaultExpandAll: true,
      showSearch: true,
      fieldNames: { children: 'children', label: 'name', value: 'id' },
    },
    fieldName: 'deptId',
    label: '所属部门',
  },
  {
    component: 'Select',
    componentProps: {
      placeholder: '请选择性别',
      class: 'w-full',
      options: [
        { label: '未知', value: 0 },
        { label: '男', value: 1 },
        { label: '女', value: 2 },
      ],
    },
    fieldName: 'gender',
    label: '性别',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入邮箱地址' },
    fieldName: 'email',
    label: '邮箱',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入手机号' },
    fieldName: 'mobile',
    label: '手机号',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入工号' },
    fieldName: 'employeeNo',
    label: '工号',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入职务' },
    fieldName: 'jobTitle',
    label: '职务',
  },
  {
    component: 'DatePicker',
    fieldName: 'birthday',
    label: '生日',
    componentProps: {
      class: 'w-full',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    component: 'DatePicker',
    fieldName: 'entryDate',
    label: '入职日期',
    componentProps: {
      class: 'w-full',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD',
    },
  },
  {
    component: 'Select',
    componentProps: {
      placeholder: '请选择状态',
      class: 'w-full',
      options: [
        { label: '启用', value: 1 },
        { label: '禁用', value: 0 },
      ],
    },
    fieldName: 'status',
    label: '状态',
    rules: 'required',
    defaultValue: 1,
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入头像 URL' },
    fieldName: 'avatar',
    label: '头像',
  },
  {
    component: 'Textarea',
    componentProps: { placeholder: '请输入备注', rows: 3 },
    fieldName: 'remark',
    label: '备注',
  },
];
