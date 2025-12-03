import { requestClient } from '#/api/request';

export interface DictType {
  id: string;
  code: string; // 字典类型
  name: string; // 字典名称
  status: number; // 状态
  remark?: string;
  createTime?: string;
}

export interface DictData {
  id: string;
  typeCode: string; // 字典类型
  itemCode: string; // 字典键值
  itemName: string; // 字典标签
  sort: number; // 排序
  status: number; // 状态
  remark?: string;
  createTime?: string;
  isDefault?: string;
  cssClass?: string;
  listClass?: string;
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

export interface DictTypeQuery {
  name?: string;
  code?: string;
  status?: number;
}

export interface DictDataQuery {
  typeCode?: string;
  itemName?: string;
  status?: number;
}

enum Api {
  Type = '/sys/dict/type',
  Data = '/sys/dict/data',
}

// ============ 字典类型 ============

/** 字典类型分页查询 */
export function getDictTypePage(query: DictTypeQuery, page: PageQuery) {
  return requestClient.post<PageResult<DictType>>(`${Api.Type}/page`, { query, page });
}

export function getDictTypeList() {
  return requestClient.get<DictType[]>(`${Api.Type}/list`);
}

export function getDictType(id: string) {
  return requestClient.get<DictType>(`${Api.Type}/${id}`);
}

export function saveDictType(data: Partial<DictType>) {
  return requestClient.post(Api.Type, data);
}

export function updateDictType(data: Partial<DictType>) {
  return requestClient.put(Api.Type, data);
}

export function deleteDictType(id: string) {
  return requestClient.delete(`${Api.Type}/${id}`);
}

export function refreshDictCache() {
  return requestClient.post(`${Api.Type}/refresh`);
}

// ============ 字典数据 ============

/** 字典数据分页查询 */
export function getDictDataPage(query: DictDataQuery, page: PageQuery) {
  return requestClient.post<PageResult<DictData>>(`${Api.Data}/page`, { query, page });
}

export function getDictDataList(dictType: string) {
  return requestClient.get<DictData[]>(`${Api.Data}/type/${dictType}`);
}

export function getDictData(id: string) {
  return requestClient.get<DictData>(`${Api.Data}/${id}`);
}

export function saveDictData(data: Partial<DictData>) {
  return requestClient.post(Api.Data, data);
}

export function updateDictData(data: Partial<DictData>) {
  return requestClient.put(Api.Data, data);
}

export function deleteDictData(id: string) {
  return requestClient.delete(`${Api.Data}/${id}`);
}
