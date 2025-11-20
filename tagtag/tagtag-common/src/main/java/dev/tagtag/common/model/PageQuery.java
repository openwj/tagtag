package dev.tagtag.common.model;

import dev.tagtag.common.constant.GlobalConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonAlias;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {
    @NotNull
    @Min(1)
    @JsonAlias({"pageNumber", "currentPage"})
    private Integer pageNo = GlobalConstants.DEFAULT_PAGE_NO;
    @NotNull
    @Min(1)
    @Max(GlobalConstants.MAX_PAGE_SIZE)
    @JsonAlias({"pageSize", "limit"})
    private Integer pageSize = GlobalConstants.DEFAULT_PAGE_SIZE;
    private List<SortField> sortFields;

    /**
     * 归一化页码与分页大小
     * @return 当前对象（便于链式调用）
     */
    public PageQuery normalize() {
        this.pageNo = GlobalConstants.normalizePageNo(this.pageNo);
        this.pageSize = GlobalConstants.clampPageSize(this.pageSize);
        return this;
    }

    /**
     * 计算查询偏移量（(pageNo-1)*pageSize）
     * @return 偏移量
     */
    public int toOffset() {
        int pn = GlobalConstants.normalizePageNo(this.pageNo);
        int ps = GlobalConstants.clampPageSize(this.pageSize);
        return (pn - 1) * ps;
    }

    /**
     * 判断是否指定排序
     * @return 是否存在排序字段
     */
    public boolean hasSort() {
        return this.sortFields != null && !this.sortFields.isEmpty();
    }

    /**
     * 解析排序字段与方向（支持 "field asc|desc"）
     *
     * @return 当前对象（便于链式调用）
     */
    /**
     * 判断是否已设置排序字段（多字段支持）
     * @return 是否存在排序字段
     */
    public boolean hasSortFields() {
        return this.sortFields != null && !this.sortFields.isEmpty();
    }

    /**
     * 应用排序默认值
     *
     * @param defaultField 默认排序字段
     * @param defaultAsc 默认是否升序
     * @return 当前对象（便于链式调用）
     */
    public PageQuery applyDefaultSort(String defaultField, boolean defaultAsc) {
        if (this.sortFields == null) {
            this.sortFields = new ArrayList<>();
        }
        if (this.sortFields.isEmpty()) {
            this.sortFields.add(SortField.of(defaultField, defaultAsc));
        }
        return this;
    }

    /**
     * 添加一个排序字段
     * @param field 排序字段
     * @param asc 是否升序
     * @return 当前对象
     */
    public PageQuery addSortField(String field, boolean asc) {
        if (this.sortFields == null) {
            this.sortFields = new ArrayList<>();
        }
        if (field != null && !field.trim().isEmpty()) {
            this.sortFields.add(SortField.of(field.trim(), asc));
        }
        return this;
    }


    private static boolean containsIgnoreCase(Collection<String> set, String value) {
        if (value == null) return false;
        for (String s : set) {
            if (s != null && s.equalsIgnoreCase(value)) return true;
        }
        return false;
    }

    /**
     * 基于字段解析器与白名单构建安全的 ORDER BY 片段
     * @param resolver 字段到列名解析器
     * @param whitelist 允许的字段集合
     * @param defaultProperty 无有效排序时的默认属性
     * @param defaultAsc 默认排序方向是否升序
     * @return 完整 SQL 片段（如：ORDER BY create_time DESC）
     */
    public String buildOrderBy(ColumnResolver resolver, Collection<String> whitelist,
                               String defaultProperty, boolean defaultAsc) {
        List<String> parts = new ArrayList<>();
        if (this.sortFields != null) {
            for (SortField sf : this.sortFields) {
                if (sf == null || sf.getField() == null) continue;
                String field = sf.getField().trim();
                if (field.isEmpty()) continue;
                if (!containsIgnoreCase(whitelist, field)) continue;
                String column = resolver == null ? null : resolver.resolve(field);
                if (!isSafeColumn(column)) continue;
                boolean asc = sf.getAsc() == null || Objects.equals(sf.getAsc(), Boolean.TRUE);
                parts.add(column + (asc ? " ASC" : " DESC"));
            }
        }
        if (parts.isEmpty()) {
            String col = isSafeColumn(defaultProperty) ? defaultProperty : "id";
            return "ORDER BY " + col + (defaultAsc ? " ASC" : " DESC");
        }
        return "ORDER BY " + String.join(", ", parts);
    }

    /** 简单列名安全校验（字母/数字/下划线/点） */
    private static boolean isSafeColumn(String column) {
        if (column == null) return false;
        String c = column.trim();
        if (c.isEmpty()) return false;
        return c.matches("[A-Za-z0-9_.]+");
    }
}

