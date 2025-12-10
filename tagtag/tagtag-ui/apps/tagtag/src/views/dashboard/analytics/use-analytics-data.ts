import { requestClient } from '#/api/request'

export interface StatisticsOverview {
  usersTotal: number
  rolesTotal: number
  deptsTotal: number
  messagesTotal: number
  unreadMessages: number
  filesTotal: number
  dictTypesTotal: number
  dictDataTotal: number
}

export interface TrendSeries {
  labels: string[]
  userCreatedPerDay: number[]
  messageCreatedPerDay: number[]
  fileUploadedPerDay: number[]
}

export interface DistributionItem {
  name: string
  value: number
}

/**
 * 加载仪表盘总览数据
 * @returns 概览数据
 */
export async function loadOverview(): Promise<StatisticsOverview> {
  const data = await requestClient.get('/sys/stats/overview')
  return data as StatisticsOverview
}

/**
 * 加载近 N 天趋势数据
 * @param days 天数（默认 30）
 * @returns 趋势序列
 */
export async function loadTrends(days = 30): Promise<TrendSeries> {
  const data = await requestClient.get('/sys/stats/trends', { params: { days } })
  return data as TrendSeries
}

/**
 * 加载文件分布数据
 * @param by 维度（type|storage|ext）
 * @returns 分布项列表
 */
export async function loadFileDistribution(by: 'type' | 'storage' | 'ext' = 'type'): Promise<DistributionItem[]> {
  const data = await requestClient.get('/sys/stats/files/distribution', { params: { by } })
  return data as DistributionItem[]
}

/**
 * 加载消息分布数据
 * @param by 维度（status|type）
 * @returns 分布项列表
 */
export async function loadMessageDistribution(by: 'status' | 'type' = 'status'): Promise<DistributionItem[]> {
  const data = await requestClient.get('/sys/stats/messages/distribution', { params: { by } })
  return data as DistributionItem[]
}

