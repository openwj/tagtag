package dev.tagtag.module.iam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.OrderByBuilder;
import dev.tagtag.framework.util.PageNormalizer;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.framework.config.PageProperties;
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

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    private final PageProperties pageProperties;
    private final MenuMapperConvert menuMapperConvert;

    

    /** 菜单分页查询 */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResult<MenuDTO> page(MenuQueryDTO query, PageQuery pageQuery) {
        IPage<Menu> page = Pages.selectPage(pageQuery, pageProperties, Menu.class, pageProperties.getMenu(),
                (p, orderBy) -> baseMapper.selectPage(p, query, orderBy));
        IPage<MenuDTO> dtoPage = page.convert(menuMapperConvert::toDTO);
        return PageResults.of(dtoPage);
    }

    /** 获取菜单详情 */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Cacheable(cacheNames = "menuById", key = "#root.args[0]")
    public MenuDTO getById(Long id) {
        Menu entity = super.getById(id);
        return menuMapperConvert.toDTO(entity);
    }

    /** 创建菜单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menusByParent", "menuByCode"}, allEntries = true)
    public void create(MenuDTO menu) {
        Menu entity = menuMapperConvert.toEntity(menu);
        super.save(entity);
        if (menu != null) menu.setId(entity.getId());
    }

    /** 更新菜单（忽略源对象中的空值） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menusByParent", "menuByCode"}, allEntries = true)
    public void update(MenuDTO menu) {
        if (menu == null || menu.getId() == null) return;
        Menu entity = super.getById(menu.getId());
        if (entity == null) return;
        menuMapperConvert.updateEntityFromDTO(menu, entity);
        super.updateById(entity);
    }

    /** 删除菜单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"menuById", "menusByParent", "menuByCode"}, allEntries = true)
    public void delete(Long id) {
        if (id == null) return;
        super.removeById(id);
    }

    /**
     * 根据父ID查询子菜单列表（LambdaQuery）
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Cacheable(cacheNames = "menusByParent", key = "#root.args[0]")
    public java.util.List<MenuDTO> listByParentId(Long parentId) {
        if (parentId == null) return java.util.Collections.emptyList();
        java.util.List<Menu> list = this.lambdaQuery().eq(Menu::getParentId, parentId).orderByAsc(Menu::getSort, Menu::getId).list();
        return menuMapperConvert.toDTOList(list);
    }

    /**
     * 根据菜单编码查询单条（LambdaQuery）
     * @param menuCode 菜单编码
     * @return 菜单详情
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @Cacheable(cacheNames = "menuByCode", key = "#root.args[0]")
    public MenuDTO getByMenuCode(String menuCode) {
        if (menuCode == null || menuCode.isBlank()) return null;
        Menu entity = this.lambdaQuery().eq(Menu::getMenuCode, menuCode).last("LIMIT 1").one();
        return menuMapperConvert.toDTO(entity);
    }
}
