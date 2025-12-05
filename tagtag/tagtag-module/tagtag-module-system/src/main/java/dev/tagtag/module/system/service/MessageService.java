package dev.tagtag.module.system.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.system.dto.MessageDTO;
import java.util.List;

public interface MessageService {
    /**
     * 获取用户消息列表
     * @param userId 用户ID
     * @param isRead 是否已读（可选）
     * @return 消息列表
     */
    List<MessageDTO> listByUserId(Long userId, Boolean isRead);

    /**
     * 分页获取用户消息列表
     * @param userId 用户ID
     * @param pageQuery 分页参数
     * @return 分页消息列表
     */
    PageResult<MessageDTO> pageByUserId(Long userId, PageQuery pageQuery);

    /**
     * 分页获取所有消息列表（管理员）
     * @param query 查询条件
     * @param pageQuery 分页参数
     * @return 分页消息列表
     */
    PageResult<MessageDTO> page(MessageDTO query, PageQuery pageQuery);

    /**
     * 获取消息详情
     * @param id 消息ID
     * @return 消息详情
     */
    MessageDTO getById(Long id);

    /**
     * 标记已读
     * @param id 消息ID
     */
    void markRead(Long id);

    /**
     * 批量标记已读
     * @param ids 消息ID列表
     */
    void markReadBatch(List<Long> ids);

    /**
     * 标记未读
     * @param id 消息ID
     */
    void markUnread(Long id);

    /**
     * 批量标记未读
     * @param ids 消息ID列表
     */
    void markUnreadBatch(List<Long> ids);

    /**
     * 全部已读
     * @param userId 用户ID
     */
    void markAllRead(Long userId);

    /**
     * 删除消息
     * @param id 消息ID
     */
    void delete(Long id);

    /**
     * 批量删除消息
     * @param ids 消息ID列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 清空所有消息
     * @param userId 用户ID
     */
    void clearAll(Long userId);

    /**
     * 发送消息
     * @param message 消息内容
     */
    void send(MessageDTO message);
}
