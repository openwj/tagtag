package dev.tagtag.framework.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import dev.tagtag.framework.security.context.AuthContext;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

public class MetaObjectHandlerImpl implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long uid = AuthContext.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        setFieldValByName("createTime", now, metaObject);
        setFieldValByName("updateTime", now, metaObject);
        if (uid != null) {
            setFieldValByName("createBy", uid, metaObject);
            setFieldValByName("updateBy", uid, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long uid = AuthContext.getCurrentUserId();
        LocalDateTime now = LocalDateTime.now();
        setFieldValByName("updateTime", now, metaObject);
        if (uid != null) {
            setFieldValByName("updateBy", uid, metaObject);
        }
    }
}
