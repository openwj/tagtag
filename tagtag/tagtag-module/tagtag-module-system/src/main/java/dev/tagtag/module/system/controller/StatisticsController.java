package dev.tagtag.module.system.controller;

import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.system.dto.DistributionItemDTO;
import dev.tagtag.contract.system.dto.StatisticsOverviewDTO;
import dev.tagtag.contract.system.dto.StatsTrendDTO;
import dev.tagtag.module.system.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统统计控制器
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/sys/stats")
@RequiredArgsConstructor
@Tag(name = "系统管理 - 统计分析", description = "系统统计分析相关 API 接口")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 系统统计总览（仪表盘卡片）
     * @return 总览数据
     */
    @GetMapping("/overview")
    @Operation(summary = "系统统计总览", description = "获取系统统计总览数据，用于仪表盘展示")
    public Result<StatisticsOverviewDTO> overview() {
        return Result.ok(statisticsService.overview());
    }

    /**
     * 近 N 天趋势数据（默认 30 天）
     * @param days 天数（可选，7/30/90）
     * @return 趋势数据
     */
    @GetMapping("/trends")
    @Operation(summary = "近N天趋势数据", description = "获取近N天的系统趋势数据，默认30天")
    public Result<StatsTrendDTO> trends(@RequestParam(name = "days", required = false, defaultValue = "30") int days) {
        return Result.ok(statisticsService.trends(days));
    }

    /**
     * 文件分布数据
     * @param by 维度（type|storage|ext）
     * @return 分布项列表
     */
    @GetMapping("/files/distribution")
    @Operation(summary = "文件分布数据", description = "获取文件分布数据，支持按类型、存储位置、文件扩展名等维度统计")
    public Result<List<DistributionItemDTO>> fileDistribution(@RequestParam(name = "by", required = false, defaultValue = "type") String by) {
        return Result.ok(statisticsService.fileDistribution(by));
    }

    /**
     * 消息分布数据
     * @param by 维度（status|type）
     * @return 分布项列表
     */
    @GetMapping("/messages/distribution")
    @Operation(summary = "消息分布数据", description = "获取消息分布数据，支持按状态、类型等维度统计")
    public Result<List<DistributionItemDTO>> messageDistribution(@RequestParam(name = "by", required = false, defaultValue = "status") String by) {
        return Result.ok(statisticsService.messageDistribution(by));
    }
}
