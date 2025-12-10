package dev.tagtag.module.system.controller;

import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.system.dto.DistributionItemDTO;
import dev.tagtag.contract.system.dto.StatisticsOverviewDTO;
import dev.tagtag.contract.system.dto.StatsTrendDTO;
import dev.tagtag.module.system.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统统计控制器
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/sys/stats")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 系统统计总览（仪表盘卡片）
     * @return 总览数据
     */
    @GetMapping("/overview")
    public Result<StatisticsOverviewDTO> overview() {
        return Result.ok(statisticsService.overview());
    }

    /**
     * 近 N 天趋势数据（默认 30 天）
     * @param days 天数（可选，7/30/90）
     * @return 趋势数据
     */
    @GetMapping("/trends")
    public Result<StatsTrendDTO> trends(@RequestParam(name = "days", required = false, defaultValue = "30") int days) {
        return Result.ok(statisticsService.trends(days));
    }

    /**
     * 文件分布数据
     * @param by 维度（type|storage|ext）
     * @return 分布项列表
     */
    @GetMapping("/files/distribution")
    public Result<List<DistributionItemDTO>> fileDistribution(@RequestParam(name = "by", required = false, defaultValue = "type") String by) {
        return Result.ok(statisticsService.fileDistribution(by));
    }

    /**
     * 消息分布数据
     * @param by 维度（status|type）
     * @return 分布项列表
     */
    @GetMapping("/messages/distribution")
    public Result<List<DistributionItemDTO>> messageDistribution(@RequestParam(name = "by", required = false, defaultValue = "status") String by) {
        return Result.ok(statisticsService.messageDistribution(by));
    }
}
