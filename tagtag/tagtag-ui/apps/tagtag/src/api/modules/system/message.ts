import { requestClient } from '#/api/request';

export interface MessageItem {
  id: number | string;
  avatar?: string;
  createTime: string; // 后端字段 createTime
  isRead: boolean;
  content: string; // 后端字段 content
  title: string;
  link?: string;
  senderName?: string; // 发送者
  receiverName?: string; // 接收者
  type?: string; // 类型
}

export interface PageQuery {
  pageNo?: number;
  pageSize?: number;
  sortFields?: Array<{ asc: boolean; field: string }>;
}

export interface PageResult<T> {
  list: T[];
  total: number;
}

enum Api {
  Base = '/sys/message',
}

/**
 * 获取消息列表（不分页）
 */
export function getMessageList(params?: { isRead?: boolean }) {
  return requestClient.get<MessageItem[]>(Api.Base, { params });
}

/**
 * 分页获取消息列表（标准分页契约）
 * @param query 查询条件（如标题）
 * @param page 分页与排序参数（pageNo/pageSize/sortFields）
 * @returns 分页结果，包含消息列表与总条数
 */
export function getMessagePage(query: Partial<MessageItem>, page: PageQuery) {
  return requestClient.post<PageResult<MessageItem>>(`${Api.Base}/page`, {
    query,
    page,
  });
}

/**
 * 分页获取所有消息列表（管理员）
 * @param query 查询条件
 * @param page 分页参数
 */
export function getAllMessagePage(query: Partial<MessageItem>, page: PageQuery) {
  return requestClient.post<PageResult<MessageItem>>(`${Api.Base}/page-all`, {
    query,
    page,
  });
}

/**
 * 获取消息详情
 */
export function getMessage(id: number | string) {
  return requestClient.get<MessageItem>(`${Api.Base}/${id}`);
}

/**
 * 标记已读
 */
export function markMessageRead(id: number | string) {
  return requestClient.put(`${Api.Base}/${id}/read`);
}

/**
 * 批量标记已读
 */
export function markMessageReadBatch(ids: (number | string)[]) {
  return requestClient.put(`${Api.Base}/read/batch`, ids);
}

/**
 * 标记未读
 */
export function markMessageUnread(id: number | string) {
  return requestClient.put(`${Api.Base}/${id}/unread`);
}

/**
 * 批量标记未读
 */
export function markMessageUnreadBatch(ids: (number | string)[]) {
  return requestClient.put(`${Api.Base}/unread/batch`, ids);
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
 * 批量删除消息
 */
export function deleteMessageBatch(ids: (number | string)[]) {
  return requestClient.delete(`${Api.Base}/batch`, { data: ids });
}

/**
 * 清空消息
 */
export function clearAllMessages() {
  return requestClient.delete(`${Api.Base}/clear`);
}
