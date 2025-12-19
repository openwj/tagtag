package dev.tagtag.module.system.api.impl;

import dev.tagtag.contract.system.api.DictApi;
import dev.tagtag.contract.system.dto.DictItemDTO;
import dev.tagtag.module.system.service.DictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 字典服务契约实现
 * 提供跨模块的字典数据访问能力
 */
@Service
@RequiredArgsConstructor
public class DictApiImpl implements DictApi {

    private final DictDataService dictDataService;

    /**
     * 根据字典类型获取字典数据列表
     *
     * @param typeCode 字典类型编码
     * @return 字典数据列表
     */
    @Override
    public List<DictItemDTO> getDictData(String typeCode) {
        return dictDataService.listByDictType(typeCode);
    }

    /**
     * 根据字典类型和字典值获取字典标签
     *
     * @param typeCode  字典类型编码
     * @param itemValue 字典值
     * @return 字典标签（若未找到则返回字典值本身）
     */
    @Override
    public String getDictLabel(String typeCode, String itemValue) {
        List<DictItemDTO> list = dictDataService.listByDictType(typeCode);
        if (list == null || list.isEmpty()) {
            return itemValue;
        }
        return list.stream()
                .filter(item -> Objects.equals(item.getItemCode(), itemValue))
                .findFirst()
                .map(DictItemDTO::getItemName)
                .orElse(itemValue);
    }
}
