package dev.tagtag.module.system.controller;

 
import dev.tagtag.common.model.PageRequest;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.contract.system.dto.DictTypeDTO;
import dev.tagtag.contract.system.dto.DictTypeQueryDTO;
import dev.tagtag.module.system.service.DictTypeService;
import dev.tagtag.framework.security.RequirePerm;
import dev.tagtag.kernel.constant.Permissions;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/sys/dict/type")
@RequiredArgsConstructor
@Tag(name = "系统管理 - 字典类型", description = "字典类型相关 API 接口")
public class DictTypeController {

    private final DictTypeService dictTypeService;

    /**
     * 字典类型分页查询
     * @param req 通用分页请求体，包含查询条件与分页参数
     * @return 分页结果（list/total）
     */
    @PostMapping("/page")
    @RequirePerm(Permissions.DICT_TYPE_READ)
    @Operation(summary = "字典类型分页查询", description = "根据条件分页查询字典类型列表")
    public Result<PageResult<DictTypeDTO>> page(@RequestBody PageRequest<DictTypeQueryDTO> req) {
        return Result.ok(dictTypeService.page(req.query(), req.page()));
    }

    @GetMapping("/list")
    @RequirePerm(Permissions.DICT_TYPE_READ)
    @Operation(summary = "获取字典类型列表", description = "获取所有字典类型列表")
    public Result<List<DictTypeDTO>> list() {
        return Result.ok(dictTypeService.listAll());
    }

    @GetMapping("/{id}")
    @RequirePerm(Permissions.DICT_TYPE_READ)
    @Operation(summary = "获取字典类型详情", description = "根据ID获取字典类型详情")
    public Result<DictTypeDTO> get(@PathVariable Long id) {
        return Result.ok(dictTypeService.getById(id));
    }

    @PostMapping
    @RequirePerm(Permissions.DICT_TYPE_CREATE)
    @Operation(summary = "创建字典类型", description = "创建新的字典类型")
    public Result<Void> save(@RequestBody @Validated DictTypeDTO dto) {
        dictTypeService.save(dto);
        return Result.ok();
    }

    @PutMapping
    @RequirePerm(Permissions.DICT_TYPE_UPDATE)
    @Operation(summary = "更新字典类型", description = "更新字典类型信息")
    public Result<Void> update(@RequestBody @Validated DictTypeDTO dto) {
        dictTypeService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequirePerm(Permissions.DICT_TYPE_DELETE)
    @Operation(summary = "删除字典类型", description = "根据ID删除字典类型")
    public Result<Void> delete(@PathVariable Long id) {
        dictTypeService.delete(id);
        return Result.ok();
    }

    /**
     * 批量删除字典类型（DELETE）
     * @param ids 待删除的字典类型 ID 列表
     * @return 空
     */
    @DeleteMapping
    @RequirePerm(Permissions.DICT_TYPE_DELETE)
    @Operation(summary = "批量删除字典类型", description = "批量删除字典类型")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        dictTypeService.deleteBatch(ids);
        return Result.ok();
    }

    

    @PostMapping("/refresh")
    @RequirePerm(Permissions.DICT_TYPE_UPDATE)
    @Operation(summary = "刷新字典缓存", description = "刷新字典类型缓存")
    public Result<Void> refreshCache() {
        dictTypeService.refreshCache();
        return Result.ok();
    }

    
}
