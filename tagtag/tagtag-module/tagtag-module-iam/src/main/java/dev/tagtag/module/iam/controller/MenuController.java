package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import dev.tagtag.module.iam.service.MenuService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/iam/menus")
public class MenuController {

    private final MenuService menuService;

    /** 菜单分页查询 */
    @PostMapping("/page")
    public Result<PageResult<MenuDTO>> page(@Valid @RequestBody MenuPageRequest req) {
        PageResult<MenuDTO> pr = menuService.page(req.getQuery(), req.getPage());
        return Result.ok(pr);
    }

    /** 获取菜单详情 */
    @GetMapping("/{id}")
    public Result<MenuDTO> get(@PathVariable("id") Long id) {
        return Result.ok(menuService.getById(id));
    }

    /** 创建菜单 */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody MenuDTO menu) {
        menuService.create(menu);
        return Result.okMsg("创建成功");
    }

    /** 更新菜单（忽略源对象中的空值） */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody MenuDTO menu) {
        menuService.update(menu);
        return Result.okMsg("更新成功");
    }

    /** 删除菜单 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        menuService.delete(id);
        return Result.okMsg("删除成功");
    }

    @Data
    public static class MenuPageRequest {
        private MenuQueryDTO query;
        @Valid
        private PageQuery page;
    }
}
