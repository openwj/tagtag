import type { VxeGridProps } from '#/adapter/vxe-table';

export const columns: VxeGridProps['columns'] = [
  { type: 'checkbox', width: 60 },
  {
    field: 'title',
    title: '标题',
    minWidth: 200,
    slots: { default: 'title' },
  },
  {
    field: 'content', // 对应后端的 message
    title: '内容',
    minWidth: 300,
    showOverflow: 'tooltip',
  },
  {
    field: 'createTime', // 对应后端的 date
    title: '发送时间',
    width: 180,
    sortable: true,
    slots: { default: 'createTime' },
  },
  {
    field: 'action',
    fixed: 'right',
    slots: { default: 'action' },
    title: '操作',
    width: 120,
  },
];

export const searchFormSchema = [
  {
    fieldName: 'title',
    label: '标题',
    component: 'Input',
    componentProps: {
      placeholder: '请输入消息标题',
    },
  },
];
