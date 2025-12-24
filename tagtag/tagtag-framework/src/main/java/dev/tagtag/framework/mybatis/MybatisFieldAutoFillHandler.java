package dev.tagtag.framework.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import dev.tagtag.common.util.DateTimeUtil;
import dev.tagtag.framework.security.context.AuthContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

@Component
public class MybatisFieldAutoFillHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Long uid = AuthContext.getCurrentUserId();
        setFieldValByName("createTime", DateTimeUtil.now(), metaObject);
        setFieldValByName("updateTime", DateTimeUtil.now(), metaObject);
        if (uid != null) {
            setFieldValByName("createBy", uid, metaObject);
            setFieldValByName("updateBy", uid, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long uid = AuthContext.getCurrentUserId();
        setFieldValByName("updateTime", DateTimeUtil.now(), metaObject);
        if (uid != null) {
            setFieldValByName("updateBy", uid, metaObject);
        }
    }
}



