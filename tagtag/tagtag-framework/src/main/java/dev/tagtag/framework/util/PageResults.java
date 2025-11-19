package dev.tagtag.framework.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.common.model.PageResult;
import lombok.experimental.UtilityClass;
import java.util.Collections;

/**
 * 分页结果适配工具（MyBatis Plus IPage → PageResult）
 */
@UtilityClass
public class PageResults {

    /**
     * 基于 MyBatis Plus 的 IPage 直接创建分页结果
     * @param page MyBatis Plus 分页对象
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        if (page == null) {
            return PageResult.of(Collections.emptyList(), 0L, 1, 1);
        }
        return PageResult.of(page.getRecords(), page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }
}
