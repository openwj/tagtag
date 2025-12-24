package dev.tagtag.module.iam.service.impl;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.util.TreeUtil;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import dev.tagtag.module.iam.convert.MenuMapperConvert;
import dev.tagtag.module.iam.entity.Menu;
import dev.tagtag.module.iam.mapper.MenuMapper;
import dev.tagtag.module.iam.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import dev.tagtag.common.exception.BusinessException;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final MenuMapperConvert menuMapperConvert;

    

    /** 菜单分页查询 */
    @Override
    @Transactional(readOnly = true)
    public PageResult<MenuDTO> page(MenuQueryDTO query, PageQuery pageQuery) {
        var page = baseMapper.selectPage(Pages.toPage(pageQuery), query);
        return PageResults.of(page.convert(menuMapperConvert::toDTO));
    }

    /** 获取菜单详情 */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "menuById", key = "#root.args[0]")
    public MenuDTO getById(Long id) {
        Menu entity = super.getById(id);
        return menuMapperConvert.toDTO(entity);
    }

    /** 创建菜单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menuTree", "allPermissionCodes"}, allEntries = true)
    public Long create(MenuDTO menu) {
        Menu entity = menuMapperConvert.toEntity(menu);
        super.save(entity);
        return entity.getId();
    }

    /** 更新菜单（忽略源对象中的空值） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menuTree", "allPermissionCodes"}, allEntries = true)
    public void update(MenuDTO menu) {
        if (menu == null || menu.getId() == null) return;
        Menu entity = super.getById(menu.getId());
        if (entity == null) return;
        menuMapperConvert.updateEntityFromDTO(menu, entity);
        super.updateById(entity);
    }

    /**
     * 删除菜单
     * @param id 菜单ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menuTree", "allPermissionCodes"}, allEntries = true)
    public void delete(Long id) {
        if (id == null) return;
        // 保护：存在子菜单禁止删除
        boolean hasChildren = this.lambdaQuery().eq(Menu::getParentId, id).count() > 0;
        if (hasChildren) {
            throw BusinessException.badRequest("该菜单下存在子菜单，无法删除");
        }
        super.removeById(id);
    }


    /**
     * 菜单树查询（不分页，一次性查询后在内存构树）
     * @param query 查询条件
     * @return 树形菜单列表
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "menuTree", key = "#query == null ? 'all' : #query.toString()")
    public List<MenuDTO> listTree(MenuQueryDTO query) {
        var lq = this.lambdaQuery();
        if (query != null) {
            if (query.getMenuCode() != null && !query.getMenuCode().isBlank()) {
                lq.like(Menu::getMenuCode, query.getMenuCode());
            }
            if (query.getMenuName() != null && !query.getMenuName().isBlank()) {
                lq.like(Menu::getMenuName, query.getMenuName());
            }
            if (query.getStatus() != null) {
                lq.eq(Menu::getStatus, query.getStatus());
            }
            if (query.getMenuType() != null) {
                lq.eq(Menu::getMenuType, query.getMenuType());
            }
        }
        List<Menu> all = lq.orderByAsc(Menu::getSort, Menu::getId).list();
        // 映射为 DTO 并初始化子节点列表
        List<MenuDTO> dtos = all.stream()
                .map(menuMapperConvert::toDTO)
                .peek(dto -> dto.setChildren(new ArrayList<>()))
                .collect(Collectors.toList());
        
        // 使用 TreeUtil 构建树结构
        return TreeUtil.buildTree(dtos, 
                MenuDTO::getId, 
                MenuDTO::getParentId, 
                (dto, children) -> dto.setChildren(children));
    }

    /**
     * 查询所有有效的权限编码（按钮类型）
     * @return 权限编码集合
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "allPermissionCodes", key = "'all'")
    public Set<String> listAllPermissionCodes() {
        List<String> codes = baseMapper.selectAllPermissionCodes();
        if (codes == null || codes.isEmpty()) return Collections.emptySet();
        return codes.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }


    /**
     * 更新菜单状态
     * @param id 菜单ID
     * @param status 状态（0=禁用，1=启用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menuTree", "allPermissionCodes"}, allEntries = true)
    public void updateStatus(Long id, int status) {
        if (id == null) return;
        this.lambdaUpdate().eq(Menu::getId, id).set(Menu::getStatus, status).update();
    }

    /**
     * 批量更新菜单状态（单SQL）
     * @param ids 菜单ID列表
     * @param status 状态（0=禁用，1=启用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menuTree", "allPermissionCodes"}, allEntries = true)
    public void batchUpdateStatus(List<Long> ids, int status) {
        if (ids == null || ids.isEmpty()) return;
        LinkedHashSet<Long> uniq = new LinkedHashSet<>(ids);
        this.lambdaUpdate()
                .in(Menu::getId, uniq)
                .set(Menu::getStatus, status)
                .update();
    }

    /**
     * 批量删除菜单（一次性子菜单校验）
     * @param ids 菜单ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menuTree", "allPermissionCodes"}, allEntries = true)
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        LinkedHashSet<Long> uniq = new LinkedHashSet<>(ids);
        boolean existChildren = this.lambdaQuery().in(Menu::getParentId, uniq).exists();
        if (existChildren) {
            throw BusinessException.badRequest("选中的菜单中存在子菜单，无法批量删除");
        }
        super.removeBatchByIds(uniq);
    }
}
