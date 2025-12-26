package dev.tagtag.module.iam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import dev.tagtag.module.iam.convert.RoleMapperConvert;
import dev.tagtag.module.iam.convert.UserMapperConvert;
import dev.tagtag.module.iam.entity.User;
import dev.tagtag.module.iam.entity.vo.UserVO;
import dev.tagtag.module.iam.entity.Role;
import dev.tagtag.module.iam.mapper.UserMapper;
import dev.tagtag.module.iam.mapper.RoleMapper;
import dev.tagtag.module.iam.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapperConvert userMapperConvert;
    private final RoleMapper roleMapper;
    private final RoleMapperConvert roleMapperConvert;
    private final PasswordEncoder passwordEncoder;


    /**
     * 用户分页查询（XML 构建 WHERE/ORDER BY，服务层保持轻薄）
     *
     * @param query     查询条件
     * @param pageQuery 分页参数
     * @return 用户分页结果
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<UserDTO> page(UserQueryDTO query, PageQuery pageQuery) {
        IPage<UserVO> page = baseMapper.selectPage(Pages.toPage(pageQuery), query);
        return PageResults.of(page.convert(userMapperConvert::toDTO));
    }

    /**
     * 获取用户详情
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userById", key = "#root.args[0]", unless = "#result == null")
    public UserDTO getById(Long id) {
        User entity = super.getById(id);
        UserDTO dto = userMapperConvert.toDTO(entity);
        return fillUserRoleIds(dto);
    }

    /**
     * 创建用户（服务端安全编码密码）
     *
     * @param user 用户DTO，若包含明文密码则在服务端进行编码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void create(UserDTO user) {
        User entity = userMapperConvert.toEntity(user);
        String raw = entity.getPassword();
        if (raw != null && !raw.isBlank()) {
            String encoded = passwordEncoder.encode(raw);
            entity.setPassword(encoded);
            entity.setPasswordUpdatedAt(LocalDateTime.now());
        }
        super.save(entity);
    }

    /**
     * 更新用户（忽略源对象中的空值，实现 PATCH 语义）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void update(UserDTO user) {
        if (user == null || user.getId() == null) {
            return;
        }
        User entity = super.getById(user.getId());
        if (entity == null) {
            return;
        }
        userMapperConvert.updateEntityFromDTO(user, entity);
        super.updateById(entity);
    }

    /**
     * 删除用户（物理删除；如需逻辑删除可调整实体与全局配置）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        super.removeById(id);
    }

    /**
     * 为用户分配角色（覆盖式：先删后插）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void assignRoles(Long userId, List<Long> roleIds) {
        if (userId == null) {
            return;
        }
        baseMapper.deleteUserRoles(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            baseMapper.insertUserRoles(userId, roleIds);
        }
    }

    /**
     * 根据用户名查询用户详情（包含密码与角色ID）
     *
     * @param username 用户名
     * @return 用户数据
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userByUsername", key = "#root.args[0]", condition = "#root.args[0] != null && #root.args[0].length() > 0", unless = "#result == null")
    public UserDTO getByUsername(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }
        User entity = this.getOne(this.lambdaQuery()
                .eq(User::getUsername, username)
                .getWrapper(), false);
        if (entity == null) {
            return null;
        }
        UserDTO dto = userMapperConvert.toDTO(entity);
        return fillUserRoleIds(dto);
    }

    /**
     * 为用户DTO填充角色ID列表
     *
     * @param dto 用户DTO
     * @return 填充后的用户DTO
     */
    private UserDTO fillUserRoleIds(UserDTO dto) {
        if (dto != null && dto.getId() != null) {
            List<Long> roleIds = baseMapper.selectRoleIdsByUserId(dto.getId());
            dto.setRoleIds(roleIds);
        }
        return dto;
    }

    /**
     * 更新单个用户状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void updateStatus(Long id, Integer status) {
        if (id == null || status == null) return;
        this.lambdaUpdate().eq(User::getId, id).set(User::getStatus, status).update();
    }

    /**
     * 批量更新用户状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty() || status == null) return;
        this.lambdaUpdate().in(User::getId, ids).set(User::getStatus, status).update();
    }

    /**
     * 批量删除用户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        this.removeBatchByIds(ids);
    }

    /**
     * 重置用户密码（编码并记录更新时间）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void resetPassword(Long id, String password) {
        if (id == null || password == null || password.isBlank()) return;
        String encoded = passwordEncoder.encode(password);
        this.lambdaUpdate().eq(User::getId, id)
                .set(User::getPassword, encoded)
                .set(User::getPasswordUpdatedAt, java.time.LocalDateTime.now())
                .update();
    }

    /**
     * 查询用户已分配角色列表（DTO）
     */
    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> listRolesByUserId(Long userId) {
        if (userId == null) return Collections.emptyList();
        List<Long> roleIds = baseMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) return Collections.emptyList();
        List<Role> roles = roleMapper.selectByIds(roleIds);
        return roleMapperConvert.toDTOList(roles);
    }

    /**
     * 批量为用户分配角色（覆盖式：先批量删除后批量插入）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void assignRolesBatch(List<Long> userIds, List<Long> roleIds) {
        if (userIds == null || userIds.isEmpty()) return;
        baseMapper.deleteUserRolesBatch(userIds);
        if (roleIds != null && !roleIds.isEmpty()) {
            baseMapper.insertUserRolesBatch(userIds, roleIds);
        }
    }

    /**
     * 修改用户密码（校验旧密码）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || oldPassword.isBlank() || newPassword == null || newPassword.isBlank()) {
            return;
        }
        User entity = super.getById(userId);
        if (entity == null) {
            return;
        }
        boolean matched = passwordEncoder.matches(oldPassword, entity.getPassword());
        if (!matched) {
            throw new IllegalArgumentException("旧密码不正确");
        }
        String encoded = passwordEncoder.encode(newPassword);
        this.lambdaUpdate().eq(User::getId, userId)
                .set(User::getPassword, encoded)
                .set(User::getPasswordUpdatedAt, java.time.LocalDateTime.now())
                .update();
    }

}
