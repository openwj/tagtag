package dev.tagtag.common.util;

import dev.tagtag.common.model.PageQuery;
import lombok.experimental.UtilityClass;

/**
 * 分页工具类
 */
@UtilityClass
public class PageUtil {

    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 200;
    public static final String PAGE_PARAM_PAGE_NO = "pageNo";
    public static final String PAGE_PARAM_PAGE_SIZE = "pageSize";
    public static final String PAGE_PARAM_SORT = "sort";

    /**
     * 标准化页码
     * @param pageNo 原始页码
     * @return 标准化后的页码
     */
    public static int normalizePageNo(Integer pageNo) {
        if (pageNo == null || pageNo < 1) {
            return DEFAULT_PAGE_NO;
        }
        return pageNo;
    }

    /**
     * 限制页大小
     * @param pageSize 原始页大小
     * @return 限制后的页大小
     */
    public static int clampPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    /**
     * 计算偏移量
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return 偏移量
     */
    public static int calculateOffset(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }

    /**
     * 根据页码和页大小创建标准化的PageQuery对象
     * @param pageNo 页码
     * @param pageSize 页大小
     * @return 标准化的PageQuery对象
     */
    public static PageQuery toPageQuery(int pageNo, int pageSize) {
        return new PageQuery(normalizePageNo(pageNo), clampPageSize(pageSize));
    }

    /**
     * 根据PageQuery对象创建标准化的PageQuery对象
     * @param query 原始PageQuery对象
     * @return 标准化的PageQuery对象
     */
    public static PageQuery normalize(PageQuery query) {
        return query.normalized();
    }
}