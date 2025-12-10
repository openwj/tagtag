package dev.tagtag.contract.system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分布图项（名称-数值）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistributionItemDTO {
    /** 名称 */
    private String name;
    /** 数值 */
    private long value;
}

