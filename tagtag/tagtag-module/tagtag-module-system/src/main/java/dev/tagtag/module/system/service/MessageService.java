package dev.tagtag.module.system.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.system.dto.MessageDTO;
import java.util.List;

public interface MessageService {
    /**
     * 获取用户消息列表
     * @param userId 用户ID
     * @return 消息列表
     */
    List<MessageDTO> listByUserId(Long userId);

    /**
     * 分页获取用户消息列表
     * @param userId 用户ID
     * @param pageQuery 分页参数
     * @return 分页消息列表
     */
    PageResult<MessageDTO> pageByUserId(Long userId, PageQuery pageQuery);

    /**
     * 标记已读
     * @param id 消息ID
     */
    void markRead(Long id);

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
