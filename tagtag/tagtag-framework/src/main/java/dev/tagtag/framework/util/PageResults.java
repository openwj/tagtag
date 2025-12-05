package dev.tagtag.framework.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.common.model.PageResult;
import lombok.experimental.UtilityClass;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

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

    /**
     * 基于 IPage 并按映射函数转换记录后创建分页结果
     * @param page MyBatis Plus 分页对象（源类型）
     * @param mapper 记录映射函数（S -> T）
     * @param <S> 源记录类型
     * @param <T> 目标记录类型
     * @return 分页结果（目标类型）
     */
    public static <S, T> PageResult<T> of(IPage<S> page, Function<S, T> mapper) {
        if (page == null) {
            return PageResult.of(Collections.emptyList(), 0L, 1, 1);
        }
        List<T> mapped = page.getRecords().stream().map(mapper).toList();
        return PageResult.of(mapped, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }
}
