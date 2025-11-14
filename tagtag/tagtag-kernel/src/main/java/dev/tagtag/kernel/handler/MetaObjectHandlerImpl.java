package dev.tagtag.kernel.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import dev.tagtag.kernel.util.UserContextHolder;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class MetaObjectHandlerImpl implements MetaObjectHandler {

    /** 插入时填充创建人与创建时间 */
    @Override
    public void insertFill(MetaObject metaObject) {
        Long uid = UserContextHolder.getUserId();
        LocalDateTime now = LocalDateTime.now();
        setFieldValByName("createTime", now, metaObject);
        setFieldValByName("updateTime", now, metaObject);
        if (uid != null) {
            setFieldValByName("createBy", uid, metaObject);
            setFieldValByName("updateBy", uid, metaObject);
        }
    }

    /** 更新时填充更新人与更新时间 */
    @Override
    public void updateFill(MetaObject metaObject) {
        Long uid = UserContextHolder.getUserId();
        LocalDateTime now = LocalDateTime.now();
        setFieldValByName("updateTime", now, metaObject);
        if (uid != null) {
            setFieldValByName("updateBy", uid, metaObject);
        }
    }
}
