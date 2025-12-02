import { requestClient } from '#/api/request';

export interface MessageItem {
  id: number | string;
  avatar?: string;
  date: string; // 后端对应 createTime
  isRead: boolean;
  message: string; // 后端对应 content
  title: string;
  link?: string;
}

export interface PageQuery {
  page: number;
  size: number;
}

export interface PageResult<T> {
  records: T[];
  total: number;
}

enum Api {
  Base = '/sys/message',
}

/**
 * 获取消息列表（不分页）
 */
export function getMessageList() {
  return requestClient.get<MessageItem[]>(Api.Base);
}

/**
 * 分页获取消息列表
 */
export function getMessagePage(page: PageQuery) {
  return requestClient.post<PageResult<MessageItem>>(`${Api.Base}/page`, page);
}

/**
 * 标记已读
 */
export function markMessageRead(id: number | string) {
  return requestClient.put(`${Api.Base}/${id}/read`);
}

/**
 * 全部已读
 */
export function markAllMessageRead() {
  return requestClient.put(`${Api.Base}/read-all`);
}

/**
 * 删除消息
 */
export function deleteMessage(id: number | string) {
  return requestClient.delete(`${Api.Base}/${id}`);
}

/**
 * 清空消息
 */
export function clearAllMessages() {
  return requestClient.delete(`${Api.Base}/clear`);
}
