package dev.tagtag.module.system.controller;

import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.system.dto.MessageDTO;
import dev.tagtag.kernel.annotation.RequirePerm;
import dev.tagtag.kernel.constant.Permissions;
import dev.tagtag.framework.security.context.AuthContext;
import dev.tagtag.module.system.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/sys/message")
@RequiredArgsConstructor
@Tag(name = "系统管理 - 消息管理", description = "系统消息管理相关 API 接口")
public class MessageController {

    private final MessageService messageService;

    /**
     * 获取当前用户的消息列表（不分页）
     * @param isRead 是否已读（可选）
     */
    @GetMapping
    @Operation(summary = "获取当前用户消息列表", description = "获取当前用户的消息列表，支持按已读状态过滤，不分页")
    public Result<List<MessageDTO>> list(@RequestParam(required = false) Boolean isRead) {
        Long userId = AuthContext.getCurrentUserId();
        return Result.ok(messageService.listByUserId(userId, isRead));
    }

    /**
     * 分页获取当前用户的消息列表
     */
    @PostMapping("/page")
    @Operation(summary = "分页获取当前用户消息列表", description = "分页获取当前用户的消息列表")
    public Result<PageResult<MessageDTO>> page(@RequestBody PageQuery pageQuery) {
        Long userId = AuthContext.getCurrentUserId();
        return Result.ok(messageService.pageByUserId(userId, pageQuery));
    }

    /**
     * 分页获取所有消息列表（管理员）
     * @param req 通用分页请求体，包含查询条件与分页参数
     */
    @PostMapping("/page-all")
    @RequirePerm(Permissions.MESSAGE_READ)
    @Operation(summary = "分页获取所有消息列表", description = "分页获取所有消息列表，仅管理员可用")
    public Result<PageResult<MessageDTO>> pageAll(@RequestBody dev.tagtag.common.model.PageRequest<MessageDTO> req) {
        return Result.ok(messageService.page(req.query(), req.page()));
    }

    /**
     * 获取消息详情（仅允许查看当前用户自己的消息）
     * @param id 消息ID
     * @return 消息详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取消息详情", description = "获取消息详情，仅允许查看当前用户自己的消息")
    public Result<MessageDTO> get(@PathVariable Long id) {
        Long userId = AuthContext.getCurrentUserId();
        MessageDTO dto = messageService.getById(id);
        if (dto == null) return Result.ok(null);
        if (dto.getReceiverId() == null || !dto.getReceiverId().equals(userId)) {
            return Result.forbidden("无权限查看该消息");
        }
        return Result.ok(dto);
    }

    /**
     * 标记消息已读（仅限当前用户自己的消息）
     * @param id 消息ID
     * @return 空
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "标记消息已读", description = "标记消息为已读状态，仅限当前用户自己的消息")
    public Result<Void> markRead(@PathVariable Long id) {
        Long userId = AuthContext.getCurrentUserId();
        MessageDTO dto = messageService.getById(id);
        if (dto == null || dto.getReceiverId() == null || !dto.getReceiverId().equals(userId)) {
            return Result.forbidden("无权限操作该消息");
        }
        messageService.markRead(id);
        return Result.ok();
    }

    /**
     * 批量标记消息已读（仅限当前用户自己的消息）
     * @param ids 消息ID列表
     * @return 空
     */
    @PutMapping("/read/batch")
    @Operation(summary = "批量标记消息已读", description = "批量标记消息为已读状态，仅限当前用户自己的消息")
    public Result<Void> markReadBatch(@RequestBody List<Long> ids) {
        Long userId = AuthContext.getCurrentUserId();
        java.util.List<Long> ownIds = new java.util.ArrayList<>();
        if (ids != null) {
            for (Long mid : ids) {
                MessageDTO dto = messageService.getById(mid);
                if (dto != null && dto.getReceiverId() != null && dto.getReceiverId().equals(userId)) {
                    ownIds.add(mid);
                }
            }
        }
        if (!ownIds.isEmpty()) {
            messageService.markReadBatch(ownIds);
        }
        return Result.ok();
    }

    /**
     * 标记消息未读（仅限当前用户自己的消息）
     * @param id 消息ID
     * @return 空
     */
    @PutMapping("/{id}/unread")
    @Operation(summary = "标记消息未读", description = "标记消息为未读状态，仅限当前用户自己的消息")
    public Result<Void> markUnread(@PathVariable Long id) {
        Long userId = AuthContext.getCurrentUserId();
        MessageDTO dto = messageService.getById(id);
        if (dto == null || dto.getReceiverId() == null || !dto.getReceiverId().equals(userId)) {
            return Result.forbidden("无权限操作该消息");
        }
        messageService.markUnread(id);
        return Result.ok();
    }

    /**
     * 批量标记消息未读（仅限当前用户自己的消息）
     * @param ids 消息ID列表
     * @return 空
     */
    @PutMapping("/unread/batch")
    @Operation(summary = "批量标记消息未读", description = "批量标记消息为未读状态，仅限当前用户自己的消息")
    public Result<Void> markUnreadBatch(@RequestBody List<Long> ids) {
        Long userId = AuthContext.getCurrentUserId();
        java.util.List<Long> ownIds = new java.util.ArrayList<>();
        if (ids != null) {
            for (Long mid : ids) {
                MessageDTO dto = messageService.getById(mid);
                if (dto != null && dto.getReceiverId() != null && dto.getReceiverId().equals(userId)) {
                    ownIds.add(mid);
                }
            }
        }
        if (!ownIds.isEmpty()) {
            messageService.markUnreadBatch(ownIds);
        }
        return Result.ok();
    }

    /**
     * 标记全部已读（仅当前用户）
     * @return 空
     */
    @PutMapping("/read-all")
    @Operation(summary = "标记全部已读", description = "标记当前用户的所有消息为已读状态")
    public Result<Void> markAllRead() {
        Long userId = AuthContext.getCurrentUserId();
        messageService.markAllRead(userId);
        return Result.ok();
    }

    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    @RequirePerm(Permissions.MESSAGE_DELETE)
    @Operation(summary = "删除消息", description = "删除指定消息")
    public Result<Void> delete(@PathVariable Long id) {
        // 实际生产环境应校验该消息是否属于当前用户
        messageService.delete(id);
        return Result.ok();
    }

    /**
     * 批量删除消息
     */
    @DeleteMapping("/batch")
    @RequirePerm(Permissions.MESSAGE_DELETE)
    @Operation(summary = "批量删除消息", description = "批量删除指定消息列表")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        messageService.deleteBatch(ids);
        return Result.ok();
    }

    /**
     * 清空所有消息
     */
    @DeleteMapping("/clear")
    @RequirePerm(Permissions.MESSAGE_DELETE)
    @Operation(summary = "清空所有消息", description = "清空当前用户的所有消息")
    public Result<Void> clearAll() {
        Long userId = AuthContext.getCurrentUserId();
        messageService.clearAll(userId);
        return Result.ok();
    }

    /**
     * 发送消息（仅供测试或管理员使用）
     */
    @PostMapping("/send")
    @RequirePerm(Permissions.MESSAGE_UPDATE)
    @Operation(summary = "发送消息", description = "发送消息，仅供测试或管理员使用")
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
