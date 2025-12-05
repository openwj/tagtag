package dev.tagtag.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.contract.system.dto.MessageDTO;
import dev.tagtag.module.system.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 消息 Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 分页查询消息（关联用户表）
     * @param page 分页参数
     * @param query 查询参数 (复用 MessageDTO 作为查询条件载体，或者 Map)
     * @return 分页结果
     */
    IPage<MessageDTO> selectPageDTO(IPage<MessageDTO> page, @Param("q") MessageDTO query);

    /**
     * 根据ID查询消息详情（关联用户表）
     * @param id 消息ID
     * @return 消息详情
     */
    MessageDTO selectDTOById(@Param("id") Long id);
}
