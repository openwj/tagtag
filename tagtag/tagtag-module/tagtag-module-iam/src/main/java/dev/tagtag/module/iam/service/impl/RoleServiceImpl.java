package dev.tagtag.module.iam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.framework.config.PageProperties;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.module.iam.convert.RoleMapperConvert;
import dev.tagtag.module.iam.convert.MenuMapperConvert;
import dev.tagtag.module.iam.entity.Role;
import dev.tagtag.module.iam.entity.Menu;
import dev.tagtag.module.iam.mapper.RoleMapper;
import dev.tagtag.module.iam.service.RoleService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    
    private final PageProperties pageProperties;
    private final RoleMapperConvert roleMapperConvert;
    private final MenuMapperConvert menuMapperConvert;

    

    /** 角色分页查询 */
    @Override
    @Transactional(readOnly = true)
    public PageResult<RoleDTO> page(RoleQueryDTO query, PageQuery pageQuery) {
        IPage<Role> page = Pages.selectPage(pageQuery, pageProperties, Role.class, pageProperties.getRole(),
                (p, orderBy) -> baseMapper.selectPage(p, query, orderBy));
        IPage<RoleDTO> dtoPage = page.convert(roleMapperConvert::toDTO);
        return PageResults.of(dtoPage);
    }

    /** 获取角色详情 */
    @Override
    @Transactional(readOnly = true)
    public RoleDTO getById(Long id) {
        Role entity = super.getById(id);
        return roleMapperConvert.toDTO(entity);
    }

    /**
     * 根据角色编码查询角色详情（LambdaQuery）
     * @param code 角色编码
     * @return 角色详情
     */
    @Override
    @Transactional(readOnly = true)
    public RoleDTO getByCode(String code) {
        if (code == null || code.isBlank()) return null;
        Role entity = getOne(this.lambdaQuery().eq(Role::getCode, code));
        return roleMapperConvert.toDTO(entity);
    }

    /**
     * 根据角色名称查询角色详情（LambdaQuery）
     * @param name 角色名称
     * @return 角色详情
     */
    @Override
    @Transactional(readOnly = true)
    public RoleDTO getByName(String name) {
        if (name == null || name.isBlank()) return null;
        Role entity = getOne(this.lambdaQuery().eq(Role::getName, name));
        return roleMapperConvert.toDTO(entity);
    }

    /**
     * 判断角色编码是否存在（LambdaQuery）
     * @param code 角色编码
     * @return 是否存在
     */
    @Override
    public boolean existsByCode(String code) {
        if (code == null || code.isBlank()) return false;
        return this.lambdaQuery().eq(Role::getCode, code).exists();
    }

    /**
     * 判断角色名称是否存在（LambdaQuery）
     * @param name 角色名称
     * @return 是否存在
     */
    @Override
    public boolean existsByName(String name) {
        if (name == null || name.isBlank()) return false;
        return this.lambdaQuery().eq(Role::getName, name).exists();
    }

    /** 创建角色 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(RoleDTO role) {
        Role entity = roleMapperConvert.toEntity(role);
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
        roleMapperConvert.updateEntityFromDTO(role, entity);
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
    @CacheEvict(cacheNames = {"roleMenuCodes", "roleMenus"}, allEntries = true)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) return;
        baseMapper.deleteRolePermissions(roleId);
        if (menuIds != null && !menuIds.isEmpty()) {
            baseMapper.insertRolePermissions(roleId, menuIds);
        }
        log.info("assignMenus: roleId={}, menuCount={}", roleId, menuIds == null ? 0 : menuIds.size());
    }

    /** 查询指定角色的菜单列表（仅返回按钮型作为权限；单次 JOIN 查询） */
    @Override
    @Cacheable(cacheNames = "roleMenus", key = "#root.args[0]")
    @Transactional(readOnly = true)
    public List<MenuDTO> listMenusByRole(Long roleId) {
        if (roleId == null) return java.util.Collections.emptyList();
        List<Menu> menus = baseMapper.selectMenusByRoleId(roleId);
        return menuMapperConvert.toDTOList(menus);
    }

    /** 批量查询角色的权限编码集合（按钮型菜单的 menu_code，去重） */
    @Override
    @Cacheable(cacheNames = "roleMenuCodes", key = "#root.args[0]")
    @Transactional(readOnly = true)
    public Set<String> listMenuCodesByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return java.util.Collections.emptySet();
        List<String> codes = baseMapper.selectPermissionCodesByRoleIds(roleIds);
        if (codes == null || codes.isEmpty()) return java.util.Collections.emptySet();
        return codes.stream().filter(java.util.Objects::nonNull).collect(Collectors.toCollection(java.util.LinkedHashSet::new));
    }
}
