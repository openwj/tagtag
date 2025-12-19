package dev.tagtag.module.system.controller;

import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.contract.system.dto.DictItemDTO;
import dev.tagtag.contract.system.dto.DictItemQueryDTO;
import dev.tagtag.module.system.service.DictDataService;
import dev.tagtag.framework.security.RequirePerm;
import dev.tagtag.kernel.constant.Permissions;
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
     * @param req 通用分页请求体，包含查询条件与分页参数
     * @return 分页结果（list/total）
     */
    @PostMapping("/page")
    @RequirePerm(Permissions.DICT_DATA_READ)
    public Result<PageResult<DictItemDTO>> page(@RequestBody dev.tagtag.common.model.PageRequest<DictItemQueryDTO> req) {
        return Result.ok(dictDataService.page(req.query(), req.page()));
    }

    /**
     * 根据字典类型获取字典数据列表
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @GetMapping("/type/{dictType}")
    @RequirePerm(Permissions.DICT_DATA_READ)
    public Result<List<DictItemDTO>> listByDictType(@PathVariable String dictType) {
        return Result.ok(dictDataService.listByDictType(dictType));
    }

    /**
     * 获取字典数据详情
     * @param id 字典数据ID
     * @return 字典数据详情
     */
    @GetMapping("/{id}")
    @RequirePerm(Permissions.DICT_DATA_READ)
    public Result<DictItemDTO> get(@PathVariable Long id) {
        return Result.ok(dictDataService.getById(id));
    }

    /**
     * 新增字典数据
     * @param dto 字典数据传输对象
     * @return 空
     */
    @PostMapping
    @RequirePerm(Permissions.DICT_DATA_CREATE)
    public Result<Void> save(@RequestBody @Validated DictItemDTO dto) {
        dictDataService.save(dto);
        return Result.ok();
    }

    /**
     * 修改字典数据
     * @param dto 字典数据传输对象
     * @return 空
     */
    @PutMapping
    @RequirePerm(Permissions.DICT_DATA_UPDATE)
    public Result<Void> update(@RequestBody @Validated DictItemDTO dto) {
        dictDataService.update(dto);
        return Result.ok();
    }

    /**
     * 删除字典数据
     * @param id 字典数据ID
     * @return 空
     */
    @DeleteMapping("/{id}")
    @RequirePerm(Permissions.DICT_DATA_DELETE)
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
    @RequirePerm(Permissions.DICT_DATA_DELETE)
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        dictDataService.deleteBatch(ids);
        return Result.ok();
    }
    
}
