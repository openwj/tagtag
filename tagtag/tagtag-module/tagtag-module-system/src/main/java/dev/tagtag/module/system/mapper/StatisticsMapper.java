package dev.tagtag.module.system.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 系统统计聚合查询 Mapper
 */
@Mapper
public interface StatisticsMapper {

    /** 用户总数（逻辑未删除） */
    long countUsers();

    /** 角色总数 */
    long countRoles();

    /** 部门总数 */
    long countDepts();

    /** 消息总数（逻辑未删除） */
    long countMessages();

    /** 未读消息数（逻辑未删除） */
    long countUnreadMessages();

    /** 文件总数（逻辑未删除） */
    long countFiles();

    /** 字典类型总数（逻辑未删除） */
    long countDictTypes();

    /** 字典数据总数（逻辑未删除） */
    long countDictData();

    /** 用户每日新增（起止日期闭区间） */
    List<Map<String, Object>> selectUserCountPerDay(@Param("start") LocalDate start,
                                                    @Param("end") LocalDate end);

    /** 消息每日新增（起止日期闭区间） */
    List<Map<String, Object>> selectMessageCountPerDay(@Param("start") LocalDate start,
                                                       @Param("end") LocalDate end);

    /** 文件每日上传（起止日期闭区间） */
    List<Map<String, Object>> selectFileCountPerDay(@Param("start") LocalDate start,
                                                    @Param("end") LocalDate end);

    /** 文件分布（按 mime_type） */
    List<Map<String, Object>> selectFileDistributionByMimeType();

    /** 文件分布（按 storage_type） */
    List<Map<String, Object>> selectFileDistributionByStorageType();

    /** 文件分布（按扩展名 ext） */
    List<Map<String, Object>> selectFileDistributionByExt();

    /** 消息分布（按 is_read） */
    List<Map<String, Object>> selectMessageDistributionByStatus();

    /** 消息分布（按 type） */
    List<Map<String, Object>> selectMessageDistributionByType();
}

