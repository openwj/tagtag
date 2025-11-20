import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

export const columns: VxeGridProps['columns'] = [
  { field: 'id', title: 'ID', visible: false },
  { field: 'code', title: '角色编码', width: 160 },
  { field: 'name', title: '角色名称', width: 160 },
  { field: 'status', title: '状态', slots: { default: 'status' }, width: 90 },
  {
    field: 'action',
    fixed: 'right',
    slots: { default: 'action' },
    title: '操作',
    width: 200,
  },
];

export const searchFormSchema: VbenFormProps['schema'] = [
  {
    component: 'Input',
    fieldName: 'code',
    label: '角色编码',
    componentProps: { placeholder: '请输入角色编码' },
  },
  {
    component: 'Input',
    fieldName: 'name',
    label: '角色名称',
    componentProps: { placeholder: '请输入角色名称' },
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
    componentProps: { placeholder: '请输入角色编码' },
    fieldName: 'code',
    label: '角色编码',
    rules: 'required',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入角色名称' },
    fieldName: 'name',
    label: '角色名称',
    rules: 'required',
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
];