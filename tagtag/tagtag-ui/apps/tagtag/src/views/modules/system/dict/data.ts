import type { VbenFormProps } from '#/adapter/form';
import type { VxeGridProps } from '#/adapter/vxe-table';

export const typeColumns: VxeGridProps['columns'] = [
  { type: 'checkbox', width: 50 },
  { field: 'name', title: '字典名称', minWidth: 150 },
  { field: 'code', title: '字典类型', minWidth: 150 },
  { field: 'status', title: '状态', width: 100, slots: { default: 'status' } },
  { field: 'remark', title: '备注', minWidth: 150 },
  { field: 'createTime', title: '创建时间', width: 160 },
  { title: '操作', width: 200, slots: { default: 'action' }, fixed: 'right' },
];

export const typeSearchFormSchema: VbenFormProps['schema'] = [
  {
    fieldName: 'name',
    label: '字典名称',
    component: 'Input',
  },
  {
    fieldName: 'code',
    label: '字典类型',
    component: 'Input',
  },
  {
    fieldName: 'status',
    label: '状态',
    component: 'Select',
    componentProps: {
      options: [
        { label: '正常', value: 1 },
        { label: '停用', value: 0 },
      ],
    },
  },
];

export const dataColumns: VxeGridProps['columns'] = [
  { type: 'checkbox', width: 50 },
  { field: 'itemCode', title: '字典键值', minWidth: 120 },
  { field: 'itemName', title: '字典标签', minWidth: 120 },
  { field: 'sort', title: '排序', width: 80 },
  { field: 'status', title: '状态', width: 100, slots: { default: 'status' } },
  { field: 'remark', title: '备注', minWidth: 150 },
  { field: 'createTime', title: '创建时间', width: 160 },
  { title: '操作', width: 150, slots: { default: 'action' }, fixed: 'right' },
];

export const dataSearchFormSchema: VbenFormProps['schema'] = [
  {
    fieldName: 'itemName',
    label: '字典标签',
    component: 'Input',
  },
  {
    fieldName: 'status',
    label: '状态',
    component: 'Select',
    componentProps: {
      options: [
        { label: '正常', value: 1 },
        { label: '停用', value: 0 },
      ],
    },
  },
];
