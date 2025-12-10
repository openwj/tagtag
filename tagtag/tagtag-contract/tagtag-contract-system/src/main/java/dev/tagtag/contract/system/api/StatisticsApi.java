package dev.tagtag.contract.system.api;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.system.dto.StatisticsOverviewDTO;
import dev.tagtag.contract.system.dto.StatsTrendDTO;
import dev.tagtag.contract.system.dto.DistributionItemDTO;

import java.util.List;

/**
 * 统计接口契约
 */
public interface StatisticsApi {

    /**
     * 获取系统统计总览数据
     * @return 总览数据（用户/角色/部门/消息/未读/文件/字典类型/字典数据）
     */
    Result<StatisticsOverviewDTO> overview();

    /**
     * 获取近 N 天的趋势数据
     * @param days 天数（如 7/30/90）
     * @return 趋势数据（日期标签与多序列）
     */
    Result<StatsTrendDTO> trends(int days);

    /**
     * 获取文件分布数据
     * @param by 维度（type|storage|ext）
     * @return 分布项列表
     */
    Result<List<DistributionItemDTO>> fileDistribution(String by);

    /**
     * 获取消息分布数据
     * @param by 维度（status|type）
     * @return 分布项列表
     */
    Result<List<DistributionItemDTO>> messageDistribution(String by);
}

