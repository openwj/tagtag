import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

export const columns: VxeGridProps['columns'] = [
  { field: 'id', title: 'ID', visible: false },
  { field: 'name', title: '部门名称', treeNode: true, width: 220 },
  { field: 'code', title: '部门编码', width: 140 },
  { field: 'leader', title: '负责人', width: 120 },
  { field: 'phone', title: '联系电话', width: 140 },
  { field: 'email', title: '邮箱', width: 180 },
  { field: 'sort', title: '排序', width: 80 },
  { field: 'status', title: '状态', slots: { default: 'status' }, width: 90 },
  { field: 'createTime', title: '创建时间', width: 160 },
  { field: 'updateTime', title: '更新时间', width: 160 },
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
    fieldName: 'name',
    label: '部门名称',
    componentProps: { placeholder: '请输入部门名称' },
  },
  {
    component: 'Input',
    fieldName: 'code',
    label: '部门编码',
    componentProps: { placeholder: '请输入部门编码' },
  },
  {
    component: 'Input',
    fieldName: 'leader',
    label: '负责人',
    componentProps: { placeholder: '请输入负责人姓名' },
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
    dependencies: {
      show: false,
      triggerFields: ['id'],
    },
    fieldName: 'id',
    label: 'ID',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入部门编码' },
    fieldName: 'code',
    label: '部门编码',
    rules: 'required',
  },
  {
    component: 'TreeSelect',
    componentProps: {
      placeholder: '请选择',
      class: 'w-full',
      treeData: [],
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
    fieldName: 'parentId',
    label: '上级部门',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入部门名称' },
    fieldName: 'name',
    label: '部门名称',
    rules: 'required',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入负责人姓名' },
    fieldName: 'leader',
    label: '负责人',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入联系电话' },
    fieldName: 'phone',
    label: '联系电话',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入邮箱地址' },
    fieldName: 'email',
    label: '邮箱',
  },
  {
    component: 'InputNumber',
    componentProps: { placeholder: '请输入排序号', class: 'w-full', min: 0 },
    fieldName: 'sort',
    label: '排序',
    rules: 'required',
    defaultValue: 0,
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
    component: 'Textarea',
    componentProps: { placeholder: '请输入备注', rows: 3 },
    fieldName: 'remark',
    label: '备注',
  },
];
