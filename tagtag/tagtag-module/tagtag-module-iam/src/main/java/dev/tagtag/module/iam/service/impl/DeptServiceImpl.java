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
import dev.tagtag.contract.iam.dto.DeptDTO;
import dev.tagtag.contract.iam.dto.DeptQueryDTO;
import dev.tagtag.module.iam.convert.DeptConvert;
import dev.tagtag.module.iam.entity.Dept;
import dev.tagtag.module.iam.mapper.DeptMapper;
import dev.tagtag.module.iam.service.DeptService;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeptServiceImpl extends ServiceImpl<DeptMapper, Dept> implements DeptService {

    private final PageProperties pageProperties;

    

    /** 部门分页查询 */
    @Override
    public PageResult<DeptDTO> page(DeptQueryDTO query, PageQuery pageQuery) {
        IPage<Dept> page = Pages.selectPage(pageQuery, pageProperties, Dept.class, pageProperties.getDept(),
                (p, orderBy) -> baseMapper.selectPage(p, query, orderBy));
        IPage<DeptDTO> dtoPage = page.convert(DeptConvert::toDTO);
        return PageResults.of(dtoPage);
    }

    /** 获取部门详情 */
    @Override
    public DeptDTO getById(Long id) {
        Dept entity = super.getById(id);
        return DeptConvert.toDTO(entity);
    }

    /** 创建部门 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(cacheNames = "deptTree", allEntries = true)
    public void create(DeptDTO dept) {
        validateForCreate(dept);
        Dept entity = DeptConvert.toEntity(dept);
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
        DeptConvert.mergeNonNull(dept, entity);
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
        super.removeById(id);
        log.info("dept delete: id={}", id);
    }

    /** 部门树列表 */
    @Override
    @Cacheable(cacheNames = "deptTree", key = "'all'")
    public java.util.List<DeptDTO> listTree() {
        java.util.List<Dept> all = this.lambdaQuery().orderByAsc(Dept::getSort, Dept::getId).list();
        java.util.Map<Long, DeptDTO> map = new java.util.HashMap<>(all.size());
        java.util.List<DeptDTO> roots = new java.util.ArrayList<>();
        for (Dept d : all) {
            DeptDTO dto = DeptConvert.toDTO(d);
            dto.setChildren(new java.util.ArrayList<>());
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

    /** 创建校验：唯一性、父ID合法、状态合法 */
    private void validateForCreate(DeptDTO dept) {
        if (dept == null) return;
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
        java.util.Map<Long, Long> parentMap = loadParentMap();
        Long p = nodeId;
        int guard = 0;
        while (p != null && guard++ < 1000) {
            if (p.equals(ancestorId)) return true;
            p = parentMap.get(p);
        }
        return false;
    }

    /** 加载 id->parentId 映射，仅取必要字段 */
    private java.util.Map<Long, Long> loadParentMap() {
        java.util.List<Dept> all = this.lambdaQuery().select(Dept::getId, Dept::getParentId).list();
        java.util.Map<Long, Long> map = new java.util.HashMap<>(all.size());
        for (Dept d : all) {
            map.put(d.getId(), d.getParentId());
        }
        return map;
    }

    /** 递归为每个节点的 children 排序（按 sort、id） */
    private void sortTree(java.util.List<DeptDTO> nodes) {
        if (nodes == null || nodes.isEmpty()) return;
        nodes.sort(java.util.Comparator
                .comparing((DeptDTO d) -> d.getSort() == null ? Integer.MAX_VALUE : d.getSort())
                .thenComparing(DeptDTO::getId));
        for (DeptDTO n : nodes) {
            sortTree(n.getChildren());
        }
    }
}
