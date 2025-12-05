package dev.tagtag.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public List<MessageDTO> listByUserId(Long userId, Boolean isRead) {
        // 构造一个临时的 Page 对象，pageSize 设置大一点或者不分页（MyBatis Plus 分页插件如果不传 page 参数就是列表查询）
        // 这里为了复用 Mapper XML 里的关联查询逻辑，我们直接构造一个 MessageDTO 作为查询条件
        MessageDTO query = MessageDTO.builder()
                .receiverId(userId)
                .isRead(isRead)
                .build();
        
        // 注意：MessageMapper.selectPageDTO 是分页查询，如果只是想查列表，可以传一个不限制条数的 Page 对象，或者单独写一个 listDTO 的 Mapper 方法
        // 这里简单起见，使用分页查询接口，但取较大页大小，或者直接调用 baseMapper.selectListDTO(query) 如果有的话
        // 为了避免修改 Mapper 接口过多，这里先用 selectPageDTO，传入较大 pageSize
        // 更好的做法是在 Mapper 增加 selectListDTO 方法
        
        // 由于 Mapper 目前只有 selectPageDTO，我们暂时用分页模拟列表，或者在 Mapper 增加 selectListDTO
        // 鉴于需要返回 List<MessageDTO>，且数据量可能不大，暂时用分页查第一页大数量
        IPage<MessageDTO> page = new Page<>(1, 1000);
        return baseMapper.selectPageDTO(page, query).getRecords();
    }

    @Override
    public PageResult<MessageDTO> pageByUserId(Long userId, PageQuery pageQuery) {
        // 复用 selectPageDTO，构造查询条件
        MessageDTO query = MessageDTO.builder().receiverId(userId).build();
        
        IPage<MessageDTO> page = Pages.selectPage(pageQuery, pageProperties, MessageDTO.class, null,
                (p, orderBy) -> baseMapper.selectPageDTO(p, query));
        
        return PageResults.of(page);
    }

    @Override
    public PageResult<MessageDTO> page(MessageDTO query, PageQuery pageQuery) {
        IPage<MessageDTO> page = Pages.selectPage(pageQuery, pageProperties, MessageDTO.class, null,
                (p, orderBy) -> baseMapper.selectPageDTO(p, query));
        return PageResults.of(page);
    }

    @Override
    public MessageDTO getById(Long id) {
        // 使用自定义的关联查询方法，确保 senderName 等字段有值
        MessageDTO dto = baseMapper.selectDTOById(id);
        if (dto != null) {
            // 可以在这里做一些额外的数据处理
        }
        return dto;
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
    public void markReadBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        this.lambdaUpdate()
            .in(Message::getId, ids)
            .set(Message::getIsRead, 1)
            .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markUnread(Long id) {
        this.lambdaUpdate()
            .eq(Message::getId, id)
            .set(Message::getIsRead, 0)
            .update();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markUnreadBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        this.lambdaUpdate()
            .in(Message::getId, ids)
            .set(Message::getIsRead, 0)
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
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        this.lambdaUpdate()
            .in(Message::getId, ids)
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
}
