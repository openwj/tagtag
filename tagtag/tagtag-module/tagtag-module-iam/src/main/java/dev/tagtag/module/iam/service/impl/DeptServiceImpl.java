package dev.tagtag.module.iam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.contract.iam.dto.DeptDTO;
import dev.tagtag.contract.iam.dto.DeptQueryDTO;
import dev.tagtag.module.iam.convert.DeptMapperConvert;
import dev.tagtag.module.iam.entity.Dept;
import dev.tagtag.module.iam.mapper.DeptMapper;
import dev.tagtag.module.iam.mapper.UserMapper;
import dev.tagtag.module.iam.service.DeptService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import dev.tagtag.module.iam.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import org.springframework.util.StringUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    private final DeptMapperConvert deptMapperConvert;
    private final UserMapper userMapper;


    /**
     * 部门分页查询
     */
    @Override
    @Transactional(readOnly = true)
    public PageResult<DeptDTO> page(DeptQueryDTO query, PageQuery pageQuery) {
        var page = baseMapper.selectPage(Pages.toPage(pageQuery), query);
        return PageResults.of(page.convert(deptMapperConvert::toDTO));
    }

    /**
     * 获取部门详情
     */
    @Override
    @Transactional(readOnly = true)
    public DeptDTO getById(Long id) {
        Dept entity = super.getById(id);
        return deptMapperConvert.toDTO(entity);
    }

    /**
     * 创建部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "deptTree", allEntries = true)
    public void create(DeptDTO dept) {
        validateForCreate(dept);
        Dept entity = deptMapperConvert.toEntity(dept);
        super.save(entity);
        log.info("dept create: id={}, name={}", entity.getId(), entity.getName());
    }

    /**
     * 更新部门（忽略源对象中的空值）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "deptTree", allEntries = true)
    public void update(DeptDTO dept) {
        if (dept == null || dept.getId() == null) {
            return;
        }
        validateForUpdate(dept);
        Dept entity = super.getById(dept.getId());
        if (entity == null) {
            return;
        }
        deptMapperConvert.updateEntityFromDTO(dept, entity);
        super.updateById(entity);
        log.info("dept update: id={}", entity.getId());
    }

    /**
     * 删除部门
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "deptTree", allEntries = true)
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        if (hasChildren(id)) {
            throw BusinessException.badRequest("该部门下存在子部门，无法删除");
        }
        if (hasUsers(id)) {
            throw BusinessException.badRequest("该部门下存在用户，无法删除");
        }
        super.removeById(id);
        log.info("dept delete: id={}", id);
    }

    /**
     * 部门树列表（无查询条件）
     *
     * @return 部门树（按 sort、id 升序）
     */
    @Override
    @Transactional(readOnly = true)
    public List<DeptDTO> listTree() {
        return listTree(null);
    }

    /**
     * 部门树列表（支持查询条件）
     *
     * @param query 查询条件：名称模糊、状态精确、父部门精确
     * @return 部门树（按 sort、id 升序）
     */
    @Override
    @Cacheable(cacheNames = "deptTree", key = "'all'", unless = "#query != null")
    @Transactional(readOnly = true)
    public List<DeptDTO> listTree(DeptQueryDTO query) {
        LambdaQueryChainWrapper<Dept> chain = this.lambdaQuery();
        if (query != null) {
            chain.like(StringUtils.hasText(query.getName()), Dept::getName, query.getName())
                    .eq(query.getStatus() != null, Dept::getStatus, query.getStatus())
                    .eq(query.getParentId() != null, Dept::getParentId, query.getParentId())
                    .eq(StringUtils.hasText(query.getCode()), Dept::getCode, query.getCode())
                    .orderByAsc(Dept::getSort, Dept::getId);
        }
        List<Dept> all = chain.list();
        Map<Long, DeptDTO> map = new HashMap<>(all.size());
        List<DeptDTO> roots = new ArrayList<>();
        for (Dept d : all) {
            DeptDTO dto = deptMapperConvert.toDTO(d);
            dto.setChildren(new ArrayList<>());
            map.put(dto.getId(), dto);
        }
        for (Dept d : all) {
            Long pid = d.getParentId();
            DeptDTO dto = map.get(d.getId());
            if (pid == null || pid == 0L || !map.containsKey(pid)) {
                roots.add(dto);
            } else {
                map.get(pid).getChildren().add(dto);
            }
        }
        sortTree(roots);
        return roots;
    }

    /**
     * 判断指定部门是否存在子部门
     *
     * @param deptId 部门ID
     * @return 是否存在子部门
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasChildren(Long deptId) {
        if (deptId == null) return false;
        return this.lambdaQuery().eq(Dept::getParentId, deptId).exists();
    }

    /**
     * 判断指定部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 是否存在用户
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasUsers(Long deptId) {
        if (deptId == null) return false;
        return new LambdaQueryChainWrapper<>(userMapper)
                .eq(User::getDeptId, deptId)
                .exists();
    }

    /**
     * 检查部门编码是否占用
     *
     * @param code      部门编码
     * @param excludeId 可选排除的部门ID
     * @return 是否已占用
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code, Long excludeId) {
        if (code == null || code.isBlank()) return false;
        return this.lambdaQuery()
                .eq(Dept::getCode, code)
                .ne(excludeId != null, Dept::getId, excludeId)
                .exists();
    }

    /**
     * 更新部门状态
     * @param id 部门ID
     * @param status 状态（0=禁用，1=启用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "deptTree", allEntries = true)
    public void updateStatus(Long id, int status) {
        if (id == null) return;
        this.lambdaUpdate().eq(Dept::getId, id).set(Dept::getStatus, status).update();
    }

    /**
     * 批量更新部门状态（单SQL）
     * @param ids 部门ID列表
     * @param status 状态（0=禁用，1=启用）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "deptTree", allEntries = true)
    public void batchUpdateStatus(List<Long> ids, int status) {
        if (ids == null || ids.isEmpty()) return;
        LinkedHashSet<Long> uniq = new LinkedHashSet<>(ids);
        this.lambdaUpdate().in(Dept::getId, uniq).set(Dept::getStatus, status).update();
    }

    /**
     * 创建校验：唯一性、父ID合法、状态合法
     */
    private void validateForCreate(DeptDTO dept) {
        if (dept == null) return;
        if (dept.getCode() == null || dept.getCode().isBlank()) {
            throw BusinessException.badRequest("部门编码不能为空");
        }
        boolean codeExists = this.lambdaQuery().eq(Dept::getCode, dept.getCode()).exists();
        if (codeExists) {
            throw BusinessException.badRequest("部门编码已存在");
        }
        if (dept.getName() == null || dept.getName().isBlank()) {
            throw BusinessException.badRequest("部门名称不能为空");
        }
        boolean nameExists = this.lambdaQuery().eq(Dept::getName, dept.getName()).exists();
        if (nameExists) {
            throw BusinessException.badRequest("部门名称已存在");
        }
        if (dept.getParentId() != null) {
            if (dept.getId() != null && dept.getId().equals(dept.getParentId())) {
                throw BusinessException.badRequest("父部门不可为自身");
            }
            if (dept.getId() != null && isAncestor(dept.getId(), dept.getParentId())) {
                throw BusinessException.badRequest("不可设置为自身的下级");
            }
        }
        if (dept.getStatus() != null && dept.getStatus() != 0 && dept.getStatus() != 1) {
            throw BusinessException.badRequest("状态不合法");
        }
    }

    /**
     * 更新校验：唯一性、父ID合法、状态合法
     */
    private void validateForUpdate(DeptDTO dept) {
        if (dept == null) return;
        if (dept.getCode() != null && !dept.getCode().isBlank()) {
            boolean codeExists = this.lambdaQuery().eq(Dept::getCode, dept.getCode()).ne(Dept::getId, dept.getId()).exists();
            if (codeExists) {
                throw BusinessException.badRequest("部门编码已存在");
            }
        }
        if (dept.getName() != null && !dept.getName().isBlank()) {
            boolean nameExists = this.lambdaQuery().eq(Dept::getName, dept.getName()).ne(Dept::getId, dept.getId()).exists();
            if (nameExists) {
                throw BusinessException.badRequest("部门名称已存在");
            }
        }
        if (dept.getParentId() != null) {
            if (dept.getId().equals(dept.getParentId())) {
                throw BusinessException.badRequest("父部门不可为自身");
            }
            if (isAncestor(dept.getId(), dept.getParentId())) {
                throw BusinessException.badRequest("不可设置为自身的下级");
            }
        }
        if (dept.getStatus() != null && dept.getStatus() != 0 && dept.getStatus() != 1) {
            throw BusinessException.badRequest("状态不合法");
        }
    }

    /**
     * 判断 ancestorId 是否为 nodeId 的祖先
     */
    private boolean isAncestor(Long ancestorId, Long nodeId) {
        Map<Long, Long> parentMap = loadParentMap();
        Long p = nodeId;
        int guard = 0;
        while (p != null && guard++ < 1000) {
            if (p.equals(ancestorId)) return true;
            p = parentMap.get(p);
        }
        return false;
    }

    /**
     * 加载 id->parentId 映射，仅取必要字段
     */
    private Map<Long, Long> loadParentMap() {
        List<Dept> all = this.lambdaQuery().select(Dept::getId, Dept::getParentId).list();
        Map<Long, Long> map = new HashMap<>(all.size());
        for (Dept d : all) {
            map.put(d.getId(), d.getParentId());
        }
        return map;
    }

    /**
     * 递归为每个节点的 children 排序（按 sort、id）
     */
    private void sortTree(List<DeptDTO> nodes) {
        if (nodes == null || nodes.isEmpty()) return;
        nodes.sort(Comparator
                .comparing((DeptDTO d) -> d.getSort() == null ? Integer.MAX_VALUE : d.getSort())
                .thenComparing(DeptDTO::getId));
        for (DeptDTO n : nodes) {
            sortTree(n.getChildren());
        }
    }
}
