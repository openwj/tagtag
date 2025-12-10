package dev.tagtag.module.system.service.impl;

import dev.tagtag.contract.system.dto.DistributionItemDTO;
import dev.tagtag.contract.system.dto.StatisticsOverviewDTO;
import dev.tagtag.contract.system.dto.StatsTrendDTO;
import dev.tagtag.module.system.mapper.StatisticsMapper;
import dev.tagtag.module.system.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 系统统计服务实现
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    /**
     * 获取系统统计总览
     * @return 总览数据
     */
    @Override
    public StatisticsOverviewDTO overview() {
        StatisticsOverviewDTO dto = new StatisticsOverviewDTO();
        dto.setUsersTotal(statisticsMapper.countUsers());
        dto.setRolesTotal(statisticsMapper.countRoles());
        dto.setDeptsTotal(statisticsMapper.countDepts());
        dto.setMessagesTotal(statisticsMapper.countMessages());
        dto.setUnreadMessages(statisticsMapper.countUnreadMessages());
        dto.setFilesTotal(statisticsMapper.countFiles());
        dto.setDictTypesTotal(statisticsMapper.countDictTypes());
        dto.setDictDataTotal(statisticsMapper.countDictData());
        return dto;
    }

    /**
     * 获取近 N 天趋势数据
     * @param days 天数（默认 30）
     * @return 趋势数据
     */
    @Override
    public StatsTrendDTO trends(int days) {
        int n = (days <= 0) ? 30 : Math.min(days, 365);
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(n - 1);

        List<String> labels = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            labels.add(start.plusDays(i).toString());
        }

        List<Long> userSeries = fillSeries(labels, statisticsMapper.selectUserCountPerDay(start, end));
        List<Long> messageSeries = fillSeries(labels, statisticsMapper.selectMessageCountPerDay(start, end));
        List<Long> fileSeries = fillSeries(labels, statisticsMapper.selectFileCountPerDay(start, end));

        StatsTrendDTO dto = new StatsTrendDTO();
        dto.setLabels(labels);
        dto.setUserCreatedPerDay(userSeries);
        dto.setMessageCreatedPerDay(messageSeries);
        dto.setFileUploadedPerDay(fileSeries);
        return dto;
    }

    /**
     * 获取文件分布
     * @param by 维度（type|storage|ext）
     * @return 分布项列表
     */
    @Override
    public List<DistributionItemDTO> fileDistribution(String by) {
        List<Map<String, Object>> rows;
        if ("storage".equalsIgnoreCase(by)) {
            rows = statisticsMapper.selectFileDistributionByStorageType();
        } else if ("ext".equalsIgnoreCase(by)) {
            rows = statisticsMapper.selectFileDistributionByExt();
        } else {
            rows = statisticsMapper.selectFileDistributionByMimeType();
        }
        return toDist(rows);
    }

    /**
     * 获取消息分布
     * @param by 维度（status|type）
     * @return 分布项列表
     */
    @Override
    public List<DistributionItemDTO> messageDistribution(String by) {
        List<Map<String, Object>> rows;
        if ("status".equalsIgnoreCase(by)) {
            rows = statisticsMapper.selectMessageDistributionByStatus();
        } else {
            rows = statisticsMapper.selectMessageDistributionByType();
        }
        return toDist(rows);
    }

    /**
     * 将 mapper 返回的 map 列表转换为分布项
     * @param rows 原始数据（name/value）
     * @return 分布项列表
     */
    private List<DistributionItemDTO> toDist(List<Map<String, Object>> rows) {
        List<DistributionItemDTO> list = new ArrayList<>();
        if (rows != null) {
            for (Map<String, Object> m : rows) {
                String name = String.valueOf(m.getOrDefault("name", "unknown"));
                Object v = m.get("value");
                long value = 0L;
                if (v instanceof Number num) value = num.longValue();
                list.add(new DistributionItemDTO(name, value));
            }
        }
        return list;
    }

    /**
     * 按 labels 将每日计数补齐为完整序列
     * @param labels 日期标签（yyyy-MM-dd）
     * @param rows 原始 mapper 数据（day/cnt）
     * @return 补齐后的数值序列
     */
    private List<Long> fillSeries(List<String> labels, List<Map<String, Object>> rows) {
        List<Long> series = new ArrayList<>(labels.size());
        java.util.Map<String, Long> map = new java.util.HashMap<>();
        if (rows != null) {
            for (Map<String, Object> m : rows) {
                String d = String.valueOf(m.get("day"));
                Object c = m.get("cnt");
                long v = (c instanceof Number num) ? num.longValue() : Long.parseLong(String.valueOf(c));
                map.put(d, v);
            }
        }
        for (String label : labels) series.add(map.getOrDefault(label, 0L));
        return series;
    }
}

