package dev.tagtag.contract.system.api;

import dev.tagtag.contract.system.dto.DictItemDTO;
import java.util.List;

/**
 * 字典服务契约接口
 */
public interface DictApi {

    /**
     * 根据字典类型获取数据列表
     *
     * @param typeCode 字典类型编码
     * @return 字典数据列表
     */
    List<DictItemDTO> getDictData(String typeCode);

    /**
     * 获取字典标签（翻译）
     *
     * @param typeCode  字典类型编码
     * @param itemValue 字典数据值
     * @return 字典标签，未找到返回 null 或原值
     */
    String getDictLabel(String typeCode, String itemValue);
}
