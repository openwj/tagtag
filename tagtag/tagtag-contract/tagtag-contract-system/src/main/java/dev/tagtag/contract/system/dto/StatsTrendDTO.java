package dev.tagtag.contract.system.dto;

import lombok.Data;

import java.util.List;

/**
 * 近 N 天趋势数据
 */
@Data
public class StatsTrendDTO {
    /** 日期标签（yyyy-MM-dd） */
    private List<String> labels;
    /** 用户每日新增 */
    private List<Long> userCreatedPerDay;
    /** 消息每日新增 */
    private List<Long> messageCreatedPerDay;
    /** 文件每日上传 */
    private List<Long> fileUploadedPerDay;
}

