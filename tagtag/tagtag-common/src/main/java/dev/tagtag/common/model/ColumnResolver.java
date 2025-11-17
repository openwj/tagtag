package dev.tagtag.common.model;

/**
 * 字段到列名解析器接口：将实体属性名解析为数据库列名
 */
public interface ColumnResolver {

    /**
     * 根据属性名解析对应的数据库列名（可包含表别名）
     * @param property 实体属性名（如 createTime）
     * @return 数据库列名（如 create_time 或 u.create_time），解析失败返回 null
     */
    String resolve(String property);
}