package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.DeptDTO;
import dev.tagtag.contract.iam.dto.DeptQueryDTO;
import dev.tagtag.module.iam.service.DeptService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import dev.tagtag.kernel.validation.CreateGroup;
import dev.tagtag.kernel.validation.UpdateGroup;
import jakarta.validation.Valid;
import dev.tagtag.kernel.constant.AppMessages;
import org.springframework.security.access.prepost.PreAuthorize;
import dev.tagtag.kernel.constant.Permissions;
import dev.tagtag.common.constant.GlobalConstants;

@RestController
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/iam/depts")
@Validated
public class DeptController {

    private final DeptService deptService;

    /** 部门分页查询接口 */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_READ + "')")
    public Result<PageResult<DeptDTO>> page(@Valid @RequestBody DeptPageRequest req) {
        PageResult<DeptDTO> pr = deptService.page(req.getQuery(), req.getPage());
        return Result.ok(pr);
    }

    /** 获取部门详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_READ + "')")
    public Result<DeptDTO> get(@PathVariable("id") Long id) {
        return Result.ok(deptService.getById(id));
    }

    /** 创建部门 */
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_CREATE + "')")
    public Result<Void> create(@Validated(CreateGroup.class) @RequestBody DeptDTO dept) {
        deptService.create(dept);
        return Result.okMsg(AppMessages.CREATE_SUCCESS);
    }

    /** 更新部门（忽略源对象中的空值） */
    @PutMapping
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_UPDATE + "')")
    public Result<Void> update(@Validated(UpdateGroup.class) @RequestBody DeptDTO dept) {
        deptService.update(dept);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 删除部门 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_DELETE + "')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        deptService.delete(id);
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /** 部门树列表（支持查询条件） */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_READ + "')")
    public Result<List<DeptDTO>> listTree(DeptQueryDTO query) {
        return Result.ok(deptService.listTree(query));
    }

    /**
     * 检查部门是否存在用户
     * @param deptId 部门ID
     * @return 是否存在用户
     */
    @GetMapping("/check/users")
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_READ + "')")
    public Result<Boolean> checkUsers(@RequestParam("deptId") Long deptId) {
        return Result.ok(deptService.hasUsers(deptId));
    }

    /**
     * 检查部门是否存在子部门
     * @param deptId 部门ID
     * @return 是否存在子部门
     */
    @GetMapping("/check/children")
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_READ + "')")
    public Result<Boolean> checkChildren(@RequestParam("deptId") Long deptId) {
        return Result.ok(deptService.hasChildren(deptId));
    }

    /**
     * 检查部门编码是否占用
     * @param code 部门编码
     * @param excludeId 可选排除的部门ID（用于编辑场景）
     * @return 是否已占用
     */
    @GetMapping("/check/code")
    @PreAuthorize("hasAuthority('" + Permissions.DEPT_READ + "')")
    public Result<Boolean> checkCode(@RequestParam("code") String code,
                                     @RequestParam(value = "excludeId", required = false) Long excludeId) {
        if (code == null || code.isBlank()) return Result.ok(false);
        return Result.ok(deptService.existsByCode(code, excludeId));
    }

    @Data
    public static class DeptPageRequest {
        private DeptQueryDTO query;
        @Valid
        private PageQuery page;
    }
}
