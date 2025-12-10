import { requestClient } from '#/api/request';

export interface StatisticsOverview {
  usersTotal: number;
  rolesTotal: number;
  deptsTotal: number;
  messagesTotal: number;
  unreadMessages: number;
  filesTotal: number;
  dictTypesTotal: number;
  dictDataTotal: number;
}

export interface TrendSeries {
  labels: string[];
  userCreatedPerDay: number[];
  messageCreatedPerDay: number[];
  fileUploadedPerDay: number[];
}

export interface DistributionItem {
  name: string;
  value: number;
}

enum Api {
  Overview = '/sys/stats/overview',
  Trends = '/sys/stats/trends',
  FileDistribution = '/sys/stats/files/distribution',
  MessageDistribution = '/sys/stats/messages/distribution',
}

/**
 * 加载仪表盘总览数据
 * @returns 概览数据
 */
export function loadOverview() {
  return requestClient.get<StatisticsOverview>(Api.Overview);
}

/**
 * 加载近 N 天趋势数据
 * @param days 天数（默认 30）
 * @returns 趋势序列
 */
export function loadTrends(days = 30) {
  return requestClient.get<TrendSeries>(Api.Trends, { params: { days } });
}

/**
 * 加载文件分布数据
 * @param by 维度（type|storage|ext）
 * @returns 分布项列表
 */
export function loadFileDistribution(
  by: 'ext' | 'storage' | 'type' = 'type',
) {
  return requestClient.get<DistributionItem[]>(Api.FileDistribution, {
    params: { by },
  });
}

/**
 * 加载消息分布数据
 * @param by 维度（status|type）
 * @returns 分布项列表
 */
export function loadMessageDistribution(by: 'status' | 'type' = 'status') {
  return requestClient.get<DistributionItem[]>(Api.MessageDistribution, {
    params: { by },
  });
}
