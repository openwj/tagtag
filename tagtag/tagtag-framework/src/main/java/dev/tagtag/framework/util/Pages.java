package dev.tagtag.framework.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.framework.config.PageProperties;
import java.util.Collection;
import java.util.function.BiFunction;
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

    /**
     * 基于 PageProperties 归一化后构建 Page 对象
     * @param pageQuery 分页参数
     * @param props 分页配置属性
     * @param <T> 数据类型
     * @return 归一化后的 Page 对象
     */
    public static <T> Page<T> toPageNormalized(PageQuery pageQuery, PageProperties props) {
        PageQuery normalized = new PageNormalizer(props).normalize(pageQuery);
        return new Page<>(normalized.getPageNo(), normalized.getPageSize());
    }

    /**
     * 构建完整的 ORDER BY 片段（基于实体元数据与白名单）
     * @param pageQuery 分页与排序参数
     * @param entityClass 实体类（用于解析属性到列）
     * @param alias 表别名（可为 null）
     * @param whitelist 允许的排序属性集合
     * @param defaultProperty 默认排序属性
     * @param defaultAsc 默认排序是否升序
     * @return 完整 SQL 片段，如 "ORDER BY create_time DESC"
     */
    public static String buildOrderBy(PageQuery pageQuery,
                                      Class<?> entityClass,
                                      String alias,
                                      Collection<String> whitelist,
                                      String defaultProperty,
                                      boolean defaultAsc) {
        return pageQuery.buildOrderBy(
                OrderByBuilder.mpResolver(entityClass, alias),
                whitelist,
                defaultProperty,
                defaultAsc
        );
    }

    /**
     * 构建 ORDER BY（默认别名为空、默认属性为 id、默认升序）
     * @param pageQuery 分页与排序参数
     * @param entityClass 实体类（用于解析属性到列）
     * @param whitelist 允许的排序属性集合
     * @return 完整 SQL 片段
     */
    public static String buildOrderBy(PageQuery pageQuery,
                                      Class<?> entityClass,
                                      Collection<String> whitelist) {
        return buildOrderBy(pageQuery, entityClass, null, whitelist, "id", true);
    }

    /**
     * 同时返回 Page 与 ORDER BY 片段，便于服务层简化代码
     * @param pageQuery 分页与排序参数
     * @param props 分页配置属性
     * @param entityClass 实体类（用于解析属性到列）
     * @param alias 表别名（可为 null）
     * @param whitelist 允许的排序属性集合
     * @param defaultProperty 默认排序属性
     * @param defaultAsc 默认排序是否升序
     * @param <T> 数据类型
     * @return Page 与 ORDER BY 的组合对象
     */
    public static <T> PageOrder<T> toPageAndOrder(PageQuery pageQuery,
                                                  PageProperties props,
                                                  Class<?> entityClass,
                                                  String alias,
                                                  Collection<String> whitelist,
                                                  String defaultProperty,
                                                  boolean defaultAsc) {
        PageQuery normalized = new PageNormalizer(props).normalize(pageQuery);
        Page<T> page = new Page<>(normalized.getPageNo(), normalized.getPageSize());
        String orderBySql = normalized.buildOrderBy(
                OrderByBuilder.mpResolver(entityClass, alias),
                whitelist,
                defaultProperty,
                defaultAsc
        );
        return new PageOrder<>(page, orderBySql);
    }

    /**
     * 便捷方法：使用默认别名、默认属性 id、默认升序
     * @param pageQuery 分页与排序参数
     * @param props 分页配置属性
     * @param entityClass 实体类
     * @param whitelist 允许的排序属性集合
     * @param <T> 数据类型
     * @return Page 与 ORDER BY 的组合对象
     */
    public static <T> PageOrder<T> toPageAndOrder(PageQuery pageQuery,
                                                  PageProperties props,
                                                  Class<?> entityClass,
                                                  Collection<String> whitelist) {
        return toPageAndOrder(pageQuery, props, entityClass, null, whitelist, "id", true);
    }

    /**
     * Page 与 ORDER BY 组合对象
     * @param <T> 数据类型
     */
    public static final class PageOrder<T> {
        private final Page<T> page;
        private final String orderBySql;

        /**
         * 构造函数：组合 Page 与 ORDER BY
         * @param page Page 对象
         * @param orderBySql ORDER BY 片段
         */
        public PageOrder(Page<T> page, String orderBySql) {
            this.page = page;
            this.orderBySql = orderBySql;
        }

        /** 获取 Page 对象 */
        public Page<T> getPage() { return page; }
        /** 获取 ORDER BY 片段 */
        public String getOrderBySql() { return orderBySql; }
    }

    /**
     * 一行式：完成归一化分页与 ORDER BY 构建，并调用 Mapper 的 selectPage
     * @param pageQuery 分页与排序参数
     * @param props 分页配置属性
     * @param entityClass 实体类
     * @param whitelist 允许的排序属性集合
     * @param mapperFn Mapper 调用函数，接受 Page 与 orderBySql，返回 IPage
     * @param <T> 数据类型
     * @return IPage 结果
     */
    public static <T> IPage<T> selectPage(PageQuery pageQuery,
                                          PageProperties props,
                                          Class<T> entityClass,
                                          Collection<String> whitelist,
                                          BiFunction<Page<T>, String, IPage<T>> mapperFn) {
        PageOrder<T> args = toPageAndOrder(pageQuery, props, entityClass, whitelist);
        return mapperFn.apply(args.getPage(), args.getOrderBySql());
    }
}
