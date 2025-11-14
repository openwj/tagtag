package dev.tagtag.module.iam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.framework.util.SortWhitelists;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.module.iam.convert.RoleConvert;
import dev.tagtag.module.iam.convert.MenuConvert;
import dev.tagtag.module.iam.entity.Role;
import dev.tagtag.module.iam.entity.Menu;
import dev.tagtag.module.iam.mapper.RoleMapper;
import dev.tagtag.module.iam.mapper.MenuMapper;
import dev.tagtag.module.iam.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    private final MenuMapper menuMapper;

    public RoleServiceImpl(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    /** 角色分页查询 */
    @Override
    public PageResult<RoleDTO> page(RoleQueryDTO query, PageQuery pageQuery) {
        pageQuery.filterSortByWhitelist(SortWhitelists.role());
        IPage<Role> page = baseMapper.selectPage(Pages.toPage(pageQuery), query, pageQuery.getSortFields());
        IPage<RoleDTO> dtoPage = page.convert(RoleConvert::toDTO);
        return PageResults.of(dtoPage);
    }

    /** 获取角色详情 */
    @Override
    public RoleDTO getById(Long id) {
        Role entity = super.getById(id);
        return RoleConvert.toDTO(entity);
    }

    /** 创建角色 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RoleDTO role) {
        Role entity = RoleConvert.toEntity(role);
        super.save(entity);
        if (role != null) role.setId(entity.getId());
    }

    /** 更新角色（忽略源对象中的空值） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RoleDTO role) {
        if (role == null || role.getId() == null) return;
        Role entity = super.getById(role.getId());
        if (entity == null) return;
        RoleConvert.mergeNonNull(role, entity);
        super.updateById(entity);
    }

    /** 删除角色 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == null) return;
        super.removeById(id);
    }

    /** 为角色分配菜单（覆盖式：先删后插） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) return;
        baseMapper.deleteRolePermissions(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            baseMapper.insertRolePermissions(roleId, menuIds);
        }
    }

    /** 查询指定角色的菜单列表（仅返回按钮型作为权限） */
    @Override
    public List<MenuDTO> listMenusByRole(Long roleId) {
        if (roleId == null) return java.util.Collections.emptyList();
        List<Long> ids = baseMapper.selectPermissionIdsByRoleId(roleId);
        if (ids == null || ids.isEmpty()) return java.util.Collections.emptyList();
        List<Menu> menus = menuMapper.selectBatchIds(ids);
        return MenuConvert.toDTOList(menus);
    }
}
