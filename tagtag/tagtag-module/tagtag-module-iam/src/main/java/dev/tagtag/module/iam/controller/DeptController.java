package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.DeptDTO;
import dev.tagtag.contract.iam.dto.DeptQueryDTO;
import dev.tagtag.module.iam.service.DeptService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import dev.tagtag.kernel.validation.CreateGroup;
import dev.tagtag.kernel.validation.UpdateGroup;
import dev.tagtag.kernel.constant.AppMessages;
import dev.tagtag.framework.security.RequirePerm;
import dev.tagtag.kernel.constant.Permissions;
import dev.tagtag.common.constant.GlobalConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/iam/depts")
@Validated
@Tag(name = "IAM - 部门管理", description = "部门相关 API 接口")
public class DeptController {

    private final DeptService deptService;


    /** 创建部门 */
    @PostMapping
    @RequirePerm(Permissions.DEPT_CREATE)
    @Operation(summary = "创建部门", description = "创建新部门")
    public Result<Void> create(@Validated(CreateGroup.class) @RequestBody DeptDTO dept) {
        deptService.create(dept);
        return Result.okMsg(AppMessages.CREATE_SUCCESS);
    }

    /** 更新部门（忽略源对象中的空值） */
    @PutMapping
    @RequirePerm(Permissions.DEPT_UPDATE)
    @Operation(summary = "更新部门", description = "更新部门信息")
    public Result<Void> update(@Validated(UpdateGroup.class) @RequestBody DeptDTO dept) {
        deptService.update(dept);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 删除部门 */
    @DeleteMapping("/{id}")
    @RequirePerm(Permissions.DEPT_DELETE)
    @Operation(summary = "删除部门", description = "根据部门ID删除部门")
    public Result<Void> delete(@PathVariable("id") Long id) {
        deptService.delete(id);
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /**
     * 更新部门状态
     * @param id 部门ID
     * @param req 包含 status（0=禁用，1=启用）
     */
    @PutMapping("/{id}/status")
    @RequirePerm(Permissions.DEPT_UPDATE)
    @Operation(summary = "更新部门状态", description = "启用或禁用部门")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @RequestBody DeptStatusUpdateRequest req) {
        deptService.updateStatus(id, req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量更新部门状态
     * @param req 包含 id 列表与 status
     */
    @PutMapping("/status/batch")
    @RequirePerm(Permissions.DEPT_UPDATE)
    @Operation(summary = "批量更新部门状态", description = "批量启用或禁用部门")
    public Result<Void> batchUpdateStatus(@RequestBody DeptStatusBatchRequest req) {
        deptService.batchUpdateStatus(req.getIds(), req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 部门树列表（支持查询条件） */
    @GetMapping("/tree")
    @RequirePerm(Permissions.DEPT_READ)
    @Operation(summary = "获取部门树", description = "获取部门树列表，支持查询条件")
    public Result<List<DeptDTO>> listTree(DeptQueryDTO query) {
        return Result.ok(deptService.listTree(query));
    }



    /** 部门状态更新请求 */
    @Data
    public static class DeptStatusUpdateRequest {
        private int status;
    }

    /** 部门批量状态更新请求 */
    @Data
    public static class DeptStatusBatchRequest {
        private List<Long> ids;
        private int status;
    }
}
