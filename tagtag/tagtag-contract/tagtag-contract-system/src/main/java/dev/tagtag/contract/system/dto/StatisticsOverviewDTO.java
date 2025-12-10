package dev.tagtag.contract.system.dto;

import lombok.Data;

/**
 * 仪表盘统计总览数据
 */
@Data
public class StatisticsOverviewDTO {

    /** 用户总数 */
    private long usersTotal;
    /** 角色总数 */
    private long rolesTotal;
    /** 部门总数 */
    private long deptsTotal;

    /** 消息总数 */
    private long messagesTotal;
    /** 未读消息数 */
    private long unreadMessages;

    /** 文件总数 */
    private long filesTotal;

    /** 字典类型总数 */
    private long dictTypesTotal;
    /** 字典数据总数 */
    private long dictDataTotal;
}

