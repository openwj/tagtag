package dev.tagtag.module.system.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.contract.system.dto.DictItemDTO;
import dev.tagtag.contract.system.dto.DictItemQueryDTO;
import dev.tagtag.module.system.service.DictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/sys/dict/data")
@RequiredArgsConstructor
public class DictDataController {

    private final DictDataService dictDataService;

    /**
     * 字典数据分页查询
     * @param req 包含查询条件与分页参数
     * @return 分页结果（list/total）
     */
    @PostMapping("/page")
    public Result<PageResult<DictItemDTO>> page(@RequestBody DictDataPageRequest req) {
        return Result.ok(dictDataService.page(req.getQuery(), req.getPage()));
    }

    @GetMapping("/type/{dictType}")
    public Result<List<DictItemDTO>> listByDictType(@PathVariable String dictType) {
        return Result.ok(dictDataService.listByDictType(dictType));
    }

    @GetMapping("/{id}")
    public Result<DictItemDTO> get(@PathVariable Long id) {
        return Result.ok(dictDataService.getById(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody @Validated DictItemDTO dto) {
        dictDataService.save(dto);
        return Result.ok();
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Validated DictItemDTO dto) {
        dictDataService.update(dto);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dictDataService.delete(id);
        return Result.ok();
    }

    /**
     * 批量删除字典数据（DELETE）
     * @param ids 待删除的字典数据 ID 列表
     * @return 空
     */
    @DeleteMapping
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        dictDataService.deleteBatch(ids);
        return Result.ok();
    }

    /**
     * 字典数据分页请求体
     */
    @lombok.Data
    public static class DictDataPageRequest {
        private DictItemQueryDTO query;
        private PageQuery page;
    }

    /**
     * 批量删除字典数据（POST 兼容接口）
     * 兼容部分网关/代理不支持 DELETE 携带请求体的场景
     * @param ids 待删除的字典数据 ID 列表
     * @return 空
     */
    @PostMapping("/batch-delete")
    public Result<Void> batchDeleteByPost(@RequestBody List<Long> ids) {
        dictDataService.deleteBatch(ids);
        return Result.ok();
    }
}
