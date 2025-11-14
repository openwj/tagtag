package dev.tagtag.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortField {

    private String field;
    private Boolean asc;

    /**
     * 工厂方法：创建排序字段对象
     * @param field 排序字段名
     * @param asc 是否升序
     * @return SortField
     */
    public static SortField of(String field, boolean asc) {
        return SortField.builder().field(field).asc(asc).build();
    }

    /**
     * 获取升序标记（默认 true）
     * @return 若 asc 为空则返回 true
     */
    public boolean isAsc() {
        return asc == null || asc;
    }
}

