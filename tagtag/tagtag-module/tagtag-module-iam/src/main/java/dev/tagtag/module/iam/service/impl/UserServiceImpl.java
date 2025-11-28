package dev.tagtag.module.iam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import dev.tagtag.module.iam.convert.UserMapperConvert;
import dev.tagtag.module.iam.entity.User;
import dev.tagtag.module.iam.entity.Role;
import dev.tagtag.module.iam.entity.vo.UserVO;
import dev.tagtag.module.iam.mapper.UserMapper;
import dev.tagtag.module.iam.mapper.RoleMapper;
import dev.tagtag.module.iam.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.tagtag.framework.config.PageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PageProperties pageProperties;
    private final UserMapperConvert userMapperConvert;
    private final RoleMapper roleMapper;
    private final dev.tagtag.module.iam.convert.RoleMapperConvert roleMapperConvert;

    

    /**
     * 用户分页查询（XML 构建 WHERE/ORDER BY，服务层保持轻薄）
     * @param query 查询条件
     * @param pageQuery 分页与排序（包含 sortFields）
     * @return 用户分页结果
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<UserDTO> page(UserQueryDTO query, PageQuery pageQuery) {
        IPage<User> page = Pages.selectPage(pageQuery, pageProperties, User.class, pageProperties.getUser(),
                (p, orderBy) -> baseMapper.selectPage(p, query, orderBy));
        IPage<UserDTO> dtoPage = page.convert(e -> {
            if (e instanceof UserVO vo) {
                return userMapperConvert.toDTO(vo);
            }
            return userMapperConvert.toDTO(e);
        });
        return PageResults.of(dtoPage);
    }

    /** 获取用户详情 */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "userById", key = "#root.args[0]", unless = "#result == null")
    public UserDTO getById(Long id) {
        User entity = super.getById(id);
        UserDTO dto = userMapperConvert.toDTO(entity);
        if (dto != null && dto.getId() != null) {
            List<Long> roleIds = baseMapper.selectRoleIdsByUserId(dto.getId());
            dto.setRoleIds(roleIds);
        }
        return dto;
    }

    /**
     * 创建用户（服务端安全编码密码）
     * @param user 用户DTO，若包含明文密码则在服务端进行BCrypt编码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void create(UserDTO user) {
        User entity = userMapperConvert.toEntity(user);
        if (user != null && user.getPassword() != null && !user.getPassword().isBlank()) {
            String encoded = new BCryptPasswordEncoder().encode(user.getPassword());
            entity.setPassword(encoded);
            entity.setPasswordUpdatedAt(LocalDateTime.now());
        }
        super.save(entity);
    }

    /** 更新用户（忽略源对象中的空值，实现 PATCH 语义） */
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

    /** 删除用户（物理删除；如需逻辑删除可调整实体与全局配置） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        super.removeById(id);
    }

    /** 为用户分配角色（覆盖式：先删后插） */
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
        /**
         * 将实体转换为DTO并补充角色ID
         * Convert entity to DTO via MapStruct and enrich roleIds
         */
        UserDTO dto = userMapperConvert.toDTO(entity);
        if (dto != null && dto.getId() != null) {
            List<Long> roleIds = baseMapper.selectRoleIdsByUserId(dto.getId());
            dto.setRoleIds(roleIds);
        }
        return dto;
    }

    /** 更新单个用户状态 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void updateStatus(Long id, Integer status) {
        if (id == null || status == null) return;
        this.lambdaUpdate().eq(User::getId, id).set(User::getStatus, status).update();
    }

    /** 批量更新用户状态 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void batchUpdateStatus(List<Long> ids, Integer status) {
        if (ids == null || ids.isEmpty() || status == null) return;
        this.lambdaUpdate().in(User::getId, ids).set(User::getStatus, status).update();
    }

    /** 批量删除用户 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return;
        this.removeBatchByIds(ids);
    }

    /** 重置用户密码（BCrypt 加密并记录更新时间） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void resetPassword(Long id, String password) {
        if (id == null || password == null || password.isBlank()) return;
        String encoded = new BCryptPasswordEncoder().encode(password);
        this.lambdaUpdate().eq(User::getId, id)
            .set(User::getPassword, encoded)
            .set(User::getPasswordUpdatedAt, java.time.LocalDateTime.now())
            .update();
    }

    /** 查询用户已分配角色列表（DTO） */
    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> listRolesByUserId(Long userId) {
        if (userId == null) return java.util.Collections.emptyList();
        List<Long> roleIds = baseMapper.selectRoleIdsByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) return java.util.Collections.emptyList();
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        return roleMapperConvert.toDTOList(roles);
    }

    /** 批量为用户分配角色（覆盖式：每个用户先删后插） */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = {"userById", "userByUsername"}, allEntries = true)
    public void assignRolesBatch(List<Long> userIds, List<Long> roleIds) {
        if (userIds == null || userIds.isEmpty()) return;
        for (Long uid : userIds) {
            if (uid == null) continue;
            baseMapper.deleteUserRoles(uid);
            if (roleIds != null && !roleIds.isEmpty()) {
                baseMapper.insertUserRoles(uid, roleIds);
            }
        }
    }

    
}
