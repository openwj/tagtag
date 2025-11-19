package dev.tagtag.module.iam.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.framework.config.PageProperties;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    private final PageProperties pageProperties;
    private final DeptMapperConvert deptMapperConvert;
    private final UserMapper userMapper;

    

    /** 部门分页查询 */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public PageResult<DeptDTO> page(DeptQueryDTO query, PageQuery pageQuery) {
        IPage<Dept> page = Pages.selectPage(pageQuery, pageProperties, Dept.class, pageProperties.getDept(),
                (p, orderBy) -> baseMapper.selectPage(p, query, orderBy));
        IPage<DeptDTO> dtoPage = page.convert(deptMapperConvert::toDTO);
        return PageResults.of(dtoPage);
    }

    /** 获取部门详情 */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public DeptDTO getById(Long id) {
        Dept entity = super.getById(id);
        return deptMapperConvert.toDTO(entity);
    }

    /** 创建部门 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "deptTree", allEntries = true)
    public void create(DeptDTO dept) {
        validateForCreate(dept);
        Dept entity = deptMapperConvert.toEntity(dept);
        super.save(entity);
        if (dept != null) {
            dept.setId(entity.getId());
        }
        log.info("dept create: id={}, name={}", entity.getId(), entity.getName());
    }

    /** 更新部门（忽略源对象中的空值） */
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

    /** 删除部门 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "deptTree", allEntries = true)
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        if (hasChildren(id)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该部门下存在子部门，无法删除");
        }
        if (hasUsers(id)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该部门下存在用户，无法删除");
        }
        super.removeById(id);
        log.info("dept delete: id={}", id);
    }

    /** 部门树列表 */
    @Override
    @Cacheable(cacheNames = "deptTree", key = "'all'")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<DeptDTO> listTree() {
        List<Dept> all = this.lambdaQuery().orderByAsc(Dept::getSort, Dept::getId).list();
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
     * @param deptId 部门ID
     * @return 是否存在子部门
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public boolean hasChildren(Long deptId) {
        if (deptId == null) return false;
        Dept one = this.lambdaQuery().eq(Dept::getParentId, deptId).last("LIMIT 1").one();
        return one != null;
    }

    /**
     * 判断指定部门是否存在用户
     * @param deptId 部门ID
     * @return 是否存在用户
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public boolean hasUsers(Long deptId) {
        if (deptId == null) return false;
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<dev.tagtag.module.iam.entity.User> qw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        qw.eq(dev.tagtag.module.iam.entity.User::getDeptId, deptId).last("LIMIT 1");
        dev.tagtag.module.iam.entity.User one = userMapper.selectOne(qw);
        return one != null;
    }

    /**
     * 检查部门编码是否占用
     * @param code 部门编码
     * @param excludeId 可选排除的部门ID
     * @return 是否已占用
     */
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public boolean existsByCode(String code, Long excludeId) {
        if (code == null || code.isBlank()) return false;
        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Dept> qw = new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();
        qw.eq(Dept::getCode, code);
        if (excludeId != null) {
            qw.ne(Dept::getId, excludeId);
        }
        Long count = this.baseMapper.selectCount(qw);
        return count != null && count > 0;
    }

    /** 创建校验：唯一性、父ID合法、状态合法 */
    private void validateForCreate(DeptDTO dept) {
        if (dept == null) return;
        if (dept.getCode() == null || dept.getCode().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "部门编码不能为空");
        }
        long codeCount = this.lambdaQuery().eq(Dept::getCode, dept.getCode()).count();
        if (codeCount > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "部门编码已存在");
        }
        if (dept.getName() == null || dept.getName().isBlank()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "部门名称不能为空");
        }
        long nameCount = this.lambdaQuery().eq(Dept::getName, dept.getName()).count();
        if (nameCount > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "部门名称已存在");
        }
        if (dept.getParentId() != null) {
            if (dept.getId() != null && dept.getId().equals(dept.getParentId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "父部门不可为自身");
            }
            if (dept.getId() != null && isAncestor(dept.getId(), dept.getParentId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "不可设置为自身的下级");
            }
        }
        if (dept.getStatus() != null && dept.getStatus() != 0 && dept.getStatus() != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "状态不合法");
        }
    }

    /** 更新校验：唯一性、父ID合法、状态合法 */
    private void validateForUpdate(DeptDTO dept) {
        if (dept == null) return;
        if (dept.getCode() != null && !dept.getCode().isBlank()) {
            long codeCount = this.lambdaQuery().eq(Dept::getCode, dept.getCode()).ne(Dept::getId, dept.getId()).count();
            if (codeCount > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "部门编码已存在");
            }
        }
        if (dept.getName() != null && !dept.getName().isBlank()) {
            long nameCount = this.lambdaQuery().eq(Dept::getName, dept.getName()).ne(Dept::getId, dept.getId()).count();
            if (nameCount > 0) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "部门名称已存在");
            }
        }
        if (dept.getParentId() != null) {
            if (dept.getId().equals(dept.getParentId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "父部门不可为自身");
            }
            if (isAncestor(dept.getId(), dept.getParentId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "不可设置为自身的下级");
            }
        }
        if (dept.getStatus() != null && dept.getStatus() != 0 && dept.getStatus() != 1) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "状态不合法");
        }
    }

    /** 判断 ancestorId 是否为 nodeId 的祖先 */
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

    /** 加载 id->parentId 映射，仅取必要字段 */
    private Map<Long, Long> loadParentMap() {
        List<Dept> all = this.lambdaQuery().select(Dept::getId, Dept::getParentId).list();
        Map<Long, Long> map = new HashMap<>(all.size());
        for (Dept d : all) {
            map.put(d.getId(), d.getParentId());
        }
        return map;
    }

    /** 递归为每个节点的 children 排序（按 sort、id） */
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
