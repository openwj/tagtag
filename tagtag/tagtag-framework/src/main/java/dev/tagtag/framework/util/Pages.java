package dev.tagtag.framework.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import dev.tagtag.common.model.PageQuery;
import lombok.experimental.UtilityClass;

/**
 * 分页对象构建工具
 */
@UtilityClass
public class Pages {

    /**
     * 从 PageQuery 构建 MyBatis Plus 的 Page 对象
     * @param pageQuery 分页参数
     * @param <T> 数据类型（仅用于泛型约束）
     * @return Page 对象（当前页与每页大小）
     */
    public static <T> Page<T> toPage(PageQuery pageQuery) {
        return new Page<>(pageQuery.getPageNo(), pageQuery.getPageSize());
    }
}
