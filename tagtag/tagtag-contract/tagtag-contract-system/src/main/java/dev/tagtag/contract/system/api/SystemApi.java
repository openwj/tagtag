package dev.tagtag.contract.system.api;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.system.dto.DictItemDTO;
import dev.tagtag.contract.system.dto.DictTypeDTO;
import dev.tagtag.contract.system.dto.MenuDTO;
import dev.tagtag.contract.system.dto.SystemConfigDTO;
import dev.tagtag.contract.system.dto.SystemConfigQueryDTO;
import dev.tagtag.contract.system.dto.MenuQueryDTO;
import dev.tagtag.contract.system.dto.DictTypeQueryDTO;
import dev.tagtag.contract.system.dto.DictItemQueryDTO;

import java.util.List;

public interface SystemApi {

    /**
     * 查询菜单树
     *
     * @return 菜单树列表
     */
    Result<List<MenuDTO>> getMenuTree();

    /**
     * 查询所有字典类型
     *
     * @return 字典类型列表
     */
    Result<List<DictTypeDTO>> listDictTypes();

    /**
     * 根据类型编码查询字典项
     *
     * @param typeCode 字典类型编码
     * @return 字典项列表
     */
    Result<List<DictItemDTO>> listDictItemsByType(String typeCode);

    /**
     * 创建字典类型
     *
     * @param type 字典类型
     * @return 操作结果
     */
    Result<Void> createDictType(DictTypeDTO type);

    /**
     * 创建字典项
     *
     * @param item 字典项
     * @return 操作结果
     */
    Result<Void> createDictItem(DictItemDTO item);

    /**
     * 查询全部系统配置
     *
     * @return 配置列表
     */
    Result<List<SystemConfigDTO>> listSystemConfigs();

    /**
     * 根据配置键查询配置
     *
     * @param key 配置键
     * @return 配置项
     */
    Result<SystemConfigDTO> getSystemConfig(String key);

    /**
     * 新增或更新系统配置
     *
     * @param config 配置项
     * @return 操作结果
     */
    Result<Void> upsertSystemConfig(SystemConfigDTO config);

    /**
     * 删除系统配置
     *
     * @param key 配置键
     * @return 操作结果
     */
    Result<Void> deleteSystemConfig(String key);

    /**
     * 分页查询系统配置
     *
     * @param query 分页查询参数
     * @return 系统配置分页结果
     */
    /**
     * 分页查询系统配置（支持过滤）
     *
     * @param pageQuery 分页参数
     * @param filter 过滤条件
     * @return 系统配置分页结果
     */
    Result<PageResult<SystemConfigDTO>> listSystemConfigs(PageQuery pageQuery, SystemConfigQueryDTO filter);

    /**
     * 分页查询菜单列表
     *
     * @param query 分页查询参数
     * @return 菜单分页结果
     */
    /**
     * 分页查询菜单列表（支持过滤）
     *
     * @param pageQuery 分页参数
     * @param filter 过滤条件
     * @return 菜单分页结果
     */
    Result<PageResult<MenuDTO>> listMenus(PageQuery pageQuery, MenuQueryDTO filter);

    /**
     * 根据菜单ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单详情
     */
    Result<MenuDTO> getMenuById(Long id);

    /**
     * 创建菜单
     *
     * @param menu 菜单数据
     * @return 操作结果
     */
    Result<Void> createMenu(MenuDTO menu);

    /**
     * 更新菜单
     *
     * @param menu 菜单数据
     * @return 操作结果
     */
    Result<Void> updateMenu(MenuDTO menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 操作结果
     */
    Result<Void> deleteMenu(Long id);

    /**
     * 分页查询字典类型
     *
     * @param query 分页查询参数
     * @return 字典类型分页结果
     */
    /**
     * 分页查询字典类型（支持过滤）
     *
     * @param pageQuery 分页参数
     * @param filter 过滤条件
     * @return 字典类型分页结果
     */
    Result<PageResult<DictTypeDTO>> listDictTypes(PageQuery pageQuery, DictTypeQueryDTO filter);

    /**
     * 根据字典类型ID查询
     *
     * @param id 字典类型ID
     * @return 字典类型详情
     */
    Result<DictTypeDTO> getDictTypeById(Long id);

    /**
     * 更新字典类型
     *
     * @param type 字典类型
     * @return 操作结果
     */
    Result<Void> updateDictType(DictTypeDTO type);

    /**
     * 删除字典类型
     *
     * @param id 字典类型ID
     * @return 操作结果
     */
    Result<Void> deleteDictType(Long id);

    /**
     * 分页查询字典项
     *
     * @param query 分页查询参数
     * @return 字典项分页结果
     */
    /**
     * 分页查询字典项（支持过滤）
     *
     * @param pageQuery 分页参数
     * @param filter 过滤条件
     * @return 字典项分页结果
     */
    Result<PageResult<DictItemDTO>> listDictItems(PageQuery pageQuery, DictItemQueryDTO filter);

    /**
     * 根据字典项ID查询
     *
     * @param id 字典项ID
     * @return 字典项详情
     */
    Result<DictItemDTO> getDictItemById(Long id);

    /**
     * 更新字典项
     *
     * @param item 字典项
     * @return 操作结果
     */
    Result<Void> updateDictItem(DictItemDTO item);

    /**
     * 删除字典项
     *
     * @param id 字典项ID
     * @return 操作结果
     */
    Result<Void> deleteDictItem(Long id);
}
