package dev.tagtag.framework.util;

import dev.tagtag.common.model.PageQuery;
import lombok.experimental.UtilityClass;

/**
 * PageQuery 处理工具（归一化与默认排序）
 * 已由 Bean Validation（PageQuery 字段约束）与 XML 默认排序替代
 * 建议直接在控制器使用 @Valid 校验，并在 XML 保持默认排序
 */
@UtilityClass
@Deprecated
public class PageQueries {

    /**
     * 归一化并应用默认排序（已弃用）
     * @param pageQuery 分页与排序参数
     * @param defaultField 默认排序字段
     * @param defaultAsc 是否升序
     * @return 处理后的 PageQuery（便于链式使用）
     */
    @Deprecated
    public static PageQuery normalizeAndDefault(PageQuery pageQuery, String defaultField, boolean defaultAsc) {
        return pageQuery.normalize().applyDefaultSort(defaultField, defaultAsc);
    }
}
