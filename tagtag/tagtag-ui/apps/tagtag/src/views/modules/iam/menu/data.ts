import type { VxeGridProps } from '#/adapter/vxe-table';
import type { VbenFormProps } from '#/adapter/form';

export const columns: VxeGridProps['columns'] = [
  { type: 'seq', width: 60, title: '序号' },
  { field: 'menuCode', title: '菜单编码', minWidth: 160 },
  { field: 'menuName', title: '菜单名称', minWidth: 160 },
  { field: 'remark', title: '备注', minWidth: 200, showOverflow: true },
  { field: 'action', title: '操作', width: 200, fixed: 'right', slots: { default: 'action' } },
];

export const searchFormSchema: VbenFormProps['schema'] = [
  { fieldName: 'menuCode', label: '菜单编码', component: 'Input' },
  { fieldName: 'menuName', label: '菜单名称', component: 'Input' },
];