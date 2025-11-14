package dev.tagtag.module.iam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.framework.util.SortWhitelists;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import dev.tagtag.module.iam.convert.MenuConvert;
import dev.tagtag.module.iam.entity.Menu;
import dev.tagtag.module.iam.mapper.MenuMapper;
import dev.tagtag.module.iam.service.MenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    /** 菜单分页查询 */
    @Override
    public PageResult<MenuDTO> page(MenuQueryDTO query, PageQuery pageQuery) {
        pageQuery.filterSortByWhitelist(SortWhitelists.permission());
        IPage<Menu> page = baseMapper.selectPage(Pages.toPage(pageQuery), query, pageQuery.getSortFields());
        IPage<MenuDTO> dtoPage = page.convert(MenuConvert::toDTO);
        return PageResults.of(dtoPage);
    }

    /** 获取菜单详情 */
    @Override
    public MenuDTO getById(Long id) {
        Menu entity = super.getById(id);
        return MenuConvert.toDTO(entity);
    }

    /** 创建菜单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(MenuDTO menu) {
        Menu entity = MenuConvert.toEntity(menu);
        super.save(entity);
        if (menu != null) menu.setId(entity.getId());
    }

    /** 更新菜单（忽略源对象中的空值） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MenuDTO menu) {
        if (menu == null || menu.getId() == null) return;
        Menu entity = super.getById(menu.getId());
        if (entity == null) return;
        MenuConvert.mergeNonNull(menu, entity);
        super.updateById(entity);
    }

    /** 删除菜单 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) return;
        super.removeById(id);
    }
}
