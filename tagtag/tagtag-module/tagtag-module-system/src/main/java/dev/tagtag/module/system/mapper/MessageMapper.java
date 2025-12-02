package dev.tagtag.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.tagtag.module.system.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
