package dev.tagtag.framework.util;

import dev.tagtag.common.model.SortField;
import java.util.*;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import dev.tagtag.common.model.ColumnResolver;

/**
 * 通用 ORDER BY 构建器：将 SortField 列表转换为安全的 SQL ORDER BY 片段
 */
public final class OrderByBuilder {

    private OrderByBuilder() {}


    /**
     * 基于 MyBatis-Plus 元数据的默认解析器（支持可选表别名）
     * @param entityClass 实体类
     * @param alias 表别名（可为 null）
     * @return 字段到列名解析器
     */
    public static ColumnResolver mpResolver(Class<?> entityClass, String alias) {
        TableInfo ti = TableInfoHelper.getTableInfo(entityClass);
        final String prefix = (alias == null || alias.isBlank()) ? "" : (alias.trim() + ".");
        Map<String, String> propToCol = new HashMap<>();
        if (ti != null) {
            if (ti.getKeyProperty() != null && ti.getKeyColumn() != null) {
                propToCol.put(ti.getKeyProperty().toLowerCase(Locale.ROOT), prefix + ti.getKeyColumn());
            }
            if (ti.getFieldList() != null) {
                for (com.baomidou.mybatisplus.core.metadata.TableFieldInfo f : ti.getFieldList()) {
                    if (f.getProperty() != null && f.getColumn() != null) {
                        propToCol.put(f.getProperty().toLowerCase(Locale.ROOT), prefix + f.getColumn());
                    }
                }
            }
        }
        return property -> {
            if (property == null) return null;
            return propToCol.get(property.trim().toLowerCase(Locale.ROOT));
        };
    }


    /** 简单列名安全校验（字母/数字/下划线/点） */
    private static boolean isSafeColumn(String column) {
        if (column == null) return false;
        String c = column.trim();
        if (c.isEmpty()) return false;
        return c.matches("[A-Za-z0-9_\\.]+");
    }
}