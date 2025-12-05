package dev.tagtag.module.system.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.contract.system.dto.DictTypeDTO;
import dev.tagtag.contract.system.dto.DictTypeQueryDTO;
import dev.tagtag.module.system.service.DictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/sys/dict/type")
@RequiredArgsConstructor
public class DictTypeController {

    private final DictTypeService dictTypeService;

    /**
     * 字典类型分页查询
     * @param req 包含查询条件与分页参数
     * @return 分页结果（list/total）
     */
    @PostMapping("/page")
    public Result<PageResult<DictTypeDTO>> page(@RequestBody DictTypePageRequest req) {
        return Result.ok(dictTypeService.page(req.getQuery(), req.getPage()));
    }

    @GetMapping("/list")
    public Result<List<DictTypeDTO>> list() {
        return Result.ok(dictTypeService.listAll());
    }

    @GetMapping("/{id}")
    public Result<DictTypeDTO> get(@PathVariable Long id) {
        return Result.ok(dictTypeService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody @Validated DictTypeDTO dto) {
        dictTypeService.save(dto);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Validated DictTypeDTO dto) {
        dictTypeService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
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
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        dictTypeService.deleteBatch(ids);
        return Result.ok();
    }

    /**
     * 批量删除字典类型（POST 兼容接口）
     * 兼容部分网关/代理不支持 DELETE 携带请求体的场景
     * @param ids 待删除的字典类型 ID 列表
     * @return 空
     */
    @PostMapping("/batch-delete")
    public Result<Void> batchDeleteByPost(@RequestBody List<Long> ids) {
        dictTypeService.deleteBatch(ids);
        return Result.ok();
    }

    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        dictTypeService.refreshCache();
        return Result.ok();
    }

    /**
     * 字典类型分页请求体
     */
    @lombok.Data
    public static class DictTypePageRequest {
        private DictTypeQueryDTO query;
        private PageQuery page;
    }
}
