package dev.tagtag.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.system.dto.MessageDTO;
import dev.tagtag.framework.config.PageProperties;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.module.system.entity.Message;
import dev.tagtag.module.system.mapper.MessageMapper;
import dev.tagtag.module.system.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final PageProperties pageProperties;

    @Override
    public List<MessageDTO> listByUserId(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
               .eq(Message::getDeleted, 0)
               .orderByDesc(Message::getCreateTime);
        
        List<Message> list = this.list(wrapper);
        
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public PageResult<MessageDTO> pageByUserId(Long userId, PageQuery pageQuery) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getReceiverId, userId)
               .eq(Message::getDeleted, 0);

        IPage<Message> page = Pages.selectPage(pageQuery, pageProperties, Message.class, null,
                (p, orderBy) -> {
                    // 默认排序
                    if (orderBy == null || orderBy.isEmpty()) {
                        wrapper.orderByDesc(Message::getCreateTime);
                    } else {
                        // 这里可以解析 orderBySql 添加到 wrapper，简单起见直接使用默认倒序
                        wrapper.orderByDesc(Message::getCreateTime);
                    }
                    return this.page(p, wrapper);
                });
        
        IPage<MessageDTO> dtoPage = page.convert(this::toDTO);
        return PageResults.of(dtoPage);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markRead(Long id) {
        this.lambdaUpdate()
            .eq(Message::getId, id)
            .set(Message::getIsRead, 1)
            .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAllRead(Long userId) {
        this.lambdaUpdate()
            .eq(Message::getReceiverId, userId)
            .set(Message::getIsRead, 1)
            .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.lambdaUpdate()
            .eq(Message::getId, id)
            .set(Message::getDeleted, 1)
            .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearAll(Long userId) {
        this.lambdaUpdate()
            .eq(Message::getReceiverId, userId)
            .set(Message::getDeleted, 1)
            .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void send(MessageDTO dto) {
        Message entity = new Message();
        BeanUtils.copyProperties(dto, entity);
        entity.setIsRead(0);
        entity.setDeleted(0);
        entity.setCreateTime(LocalDateTime.now());
        this.save(entity);
    }

    private MessageDTO toDTO(Message entity) {
        MessageDTO dto = new MessageDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setIsRead(entity.getIsRead() != null && entity.getIsRead() == 1);
        if (entity.getCreateTime() != null) {
            dto.setCreateTime(DF.format(entity.getCreateTime()));
        }
        // 模拟头像，实际可以根据 senderId 查询用户头像
        dto.setAvatar("https://avatar.vercel.sh/" + (entity.getSenderId() == null ? "sys" : entity.getSenderId()));
        return dto;
    }
}
