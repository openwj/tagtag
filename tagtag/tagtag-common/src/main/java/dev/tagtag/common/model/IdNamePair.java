package dev.tagtag.common.model;

import dev.tagtag.common.exception.AssertUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class IdNamePair {
    private Long id;
    private String name;

    /**
     * 创建键值对
     * @param id 标识
     * @param name 名称
     * @return 键值对
     */
    public static IdNamePair of(Long id, String name) {
        AssertUtils.notNull(id, "id 不能为空");
        AssertUtils.hasText(name, "name 不能为空");
        return new IdNamePair(id, name);
    }

    /**
     * 使用空名称创建键值对
     * @param id 标识
     * @return 键值对
     */
    public static IdNamePair emptyName(Long id) {
        AssertUtils.notNull(id, "id 不能为空");
        return new IdNamePair(id, "");
    }
}
