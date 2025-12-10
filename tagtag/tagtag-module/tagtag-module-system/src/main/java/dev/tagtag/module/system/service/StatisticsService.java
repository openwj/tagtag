package dev.tagtag.module.system.service;

import dev.tagtag.contract.system.dto.DistributionItemDTO;
import dev.tagtag.contract.system.dto.StatisticsOverviewDTO;
import dev.tagtag.contract.system.dto.StatsTrendDTO;

import java.util.List;

/**
 * 系统统计服务
 */
public interface StatisticsService {

    /**
     * 获取系统统计总览
     * @return 总览数据
     */
    StatisticsOverviewDTO overview();

    /**
     * 获取近 N 天趋势数据
     * @param days 天数（默认 30）
     * @return 趋势数据
     */
    StatsTrendDTO trends(int days);

    /**
     * 获取文件分布
     * @param by 维度（type|storage|ext）
     * @return 分布项列表
     */
    List<DistributionItemDTO> fileDistribution(String by);

    /**
     * 获取消息分布
     * @param by 维度（status|type）
     * @return 分布项列表
     */
    List<DistributionItemDTO> messageDistribution(String by);
}

