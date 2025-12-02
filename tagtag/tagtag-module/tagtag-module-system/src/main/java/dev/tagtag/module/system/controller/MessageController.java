package dev.tagtag.module.system.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.system.dto.MessageDTO;
import dev.tagtag.framework.security.context.AuthContext;
import dev.tagtag.module.system.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sys/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 获取当前用户的消息列表（不分页）
     */
    @GetMapping
    public Result<List<MessageDTO>> list() {
        Long userId = AuthContext.getCurrentUserId();
        return Result.ok(messageService.listByUserId(userId));
    }

    /**
     * 分页获取当前用户的消息列表
     */
    @PostMapping("/page")
    public Result<PageResult<MessageDTO>> page(@RequestBody PageQuery pageQuery) {
        Long userId = AuthContext.getCurrentUserId();
        return Result.ok(messageService.pageByUserId(userId, pageQuery));
    }

    /**
     * 标记消息已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@PathVariable("id") Long id) {
        // 实际生产环境应校验该消息是否属于当前用户
        messageService.markRead(id);
        return Result.ok();
    }

    /**
     * 标记全部已读
     */
    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        Long userId = AuthContext.getCurrentUserId();
        messageService.markAllRead(userId);
        return Result.ok();
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        // 实际生产环境应校验该消息是否属于当前用户
        messageService.delete(id);
        return Result.ok();
    }

    /**
     * 清空所有消息
     */
    @DeleteMapping("/clear")
    public Result<Void> clearAll() {
        Long userId = AuthContext.getCurrentUserId();
        messageService.clearAll(userId);
        return Result.ok();
    }

    /**
     * 发送消息（仅供测试或管理员使用）
     */
    @PostMapping("/send")
    public Result<Void> send(@RequestBody MessageDTO message) {
        // 简单设置当前用户为发送者
        Long userId = AuthContext.getCurrentUserId();
        if (message.getSenderId() == null) {
            message.setSenderId(userId);
        }
        messageService.send(message);
        return Result.ok();
    }
}
