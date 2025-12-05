import { h } from 'vue';

import { Tag } from 'ant-design-vue';

import type { VxeGridProps } from '#/adapter/vxe-table';

/**
 * 消息类型映射
 */
export const MessageTypeMap: Record<string, { color: string; text: string }> = {
  notification: { color: 'blue', text: '通知' },
  message: { color: 'cyan', text: '私信' },
  todo: { color: 'orange', text: '待办' },
  security: { color: 'red', text: '安全' },
};

export const columns: VxeGridProps['columns'] = [
  { type: 'checkbox', width: 60 },
  {
    field: 'title',
    title: '标题',
    minWidth: 200,
    slots: { default: 'title' },
  },
  {
    field: 'content', // 接口字段：消息内容
    title: '内容',
    minWidth: 300,
    showOverflow: 'tooltip',
  },
  {
    field: 'type',
    title: '类型',
    width: 100,
    slots: { default: 'type' },
    formatter: ({ cellValue }) => {
      return MessageTypeMap[cellValue]?.text || cellValue;
    },
  },
  {
    field: 'senderName',
    title: '发送者',
    width: 120,
  },
  {
    field: 'receiverName',
    title: '接收者',
    width: 120,
  },
  {
    field: 'createTime', // 接口字段：发送时间
    title: '发送时间',
    width: 180,
    sortable: true,
    slots: { default: 'date' },
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
  {
    fieldName: 'content', // 更新字段名
    label: '内容',
    component: 'Input',
    componentProps: {
      placeholder: '请输入内容关键字',
    },
  },
  {
    fieldName: 'type',
    label: '类型',
    component: 'Select',
    componentProps: {
      options: Object.keys(MessageTypeMap).map((key) => ({
        label: MessageTypeMap[key]?.text || key,
        value: key,
      })),
      allowClear: true,
      placeholder: '请选择类型',
    },
  },
  {
    fieldName: 'isRead',
    label: '状态',
    component: 'Select',
    defaultValue: false,
    componentProps: {
      options: [
        { label: '全部', value: undefined },
        { label: '未读', value: false },
        { label: '已读', value: true },
      ],
      allowClear: true,
      placeholder: '请选择状态',
    },
  },
  {
    fieldName: 'dateRange',
    label: '时间范围',
    component: 'RangePicker',
    componentProps: {
      placeholder: ['开始时间', '结束时间'],
      showTime: true,
    },
  },
];
