import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

export const columns: VxeGridProps['columns'] = [
  { field: 'id', title: 'ID', visible: false },
  { field: 'menuName', title: '菜单名称', treeNode: true, width: 220 },
  { field: 'menuCode', title: '菜单编码', width: 160 },
  { field: 'menuType', title: '类型', slots: { default: 'menuType' }, width: 90 },
  { field: 'path', title: '路由路径', width: 220 },
  { field: 'component', title: '组件路径', width: 220 },
  { field: 'icon', title: '图标', slots: { default: 'icon' }, width: 120 },
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
    width: 160,
  },
];

export const searchFormSchema: VbenFormProps['schema'] = [
  {
    component: 'Input',
    fieldName: 'menuName',
    label: '菜单名称',
    componentProps: { placeholder: '请输入菜单名称' },
  },
  {
    component: 'Input',
    fieldName: 'menuCode',
    label: '菜单编码',
    componentProps: { placeholder: '请输入菜单编码' },
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
  {
    component: 'Select',
    fieldName: 'menuType',
    label: '类型',
    componentProps: {
      placeholder: '请选择类型',
      allowClear: true,
      options: [
        { label: '目录', value: 0 },
        { label: '菜单', value: 1 },
        { label: '按钮', value: 2 },
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
    componentProps: { placeholder: '请输入菜单编码' },
    fieldName: 'menuCode',
    label: '菜单编码',
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
      fieldNames: { children: 'children', label: 'menuName', value: 'id' },
    },
    fieldName: 'parentId',
    label: '上级菜单',
  },
  {
    component: 'Select',
    componentProps: {
      placeholder: '请选择类型',
      class: 'w-full',
      options: [
        { label: '目录', value: 0 },
        { label: '菜单', value: 1 },
        { label: '按钮', value: 2 },
      ],
    },
    fieldName: 'menuType',
    label: '类型',
    rules: 'required',
    defaultValue: 1,
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入菜单名称' },
    fieldName: 'menuName',
    label: '菜单名称',
    rules: 'required',
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入路由路径' },
    fieldName: 'path',
    label: '路由路径',
    dependencies: { triggerFields: ['menuType'], show: true },
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入组件路径' },
    fieldName: 'component',
    label: '组件路径',
    dependencies: { triggerFields: ['menuType'], show: true },
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入图标名称，例如 lucide:users' },
    fieldName: 'icon',
    label: '图标',
  },
  {
    component: 'Switch',
    fieldName: 'isHidden',
    label: '隐藏',
    defaultValue: 0,
  },
  {
    component: 'Switch',
    fieldName: 'isKeepalive',
    label: '缓存',
    defaultValue: 0,
  },
  {
    component: 'Switch',
    fieldName: 'isExternal',
    label: '外链',
    defaultValue: 0,
  },
  {
    component: 'Input',
    componentProps: { placeholder: '请输入外链地址' },
    fieldName: 'externalUrl',
    label: '外链地址',
    dependencies: { triggerFields: ['isExternal'], show: true },
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