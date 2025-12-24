package dev.tagtag.module.auth.controller;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.auth.dto.TokenDTO;
import dev.tagtag.contract.auth.dto.LoginRequest;
import dev.tagtag.contract.auth.dto.RefreshRequest;
import dev.tagtag.contract.auth.dto.LogoutRequest;
import dev.tagtag.contract.auth.dto.RegisterRequest;
import dev.tagtag.kernel.annotation.RateLimit;
import dev.tagtag.framework.security.context.AuthContext;
import dev.tagtag.framework.security.model.UserPrincipal;
import dev.tagtag.module.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.tagtag.common.constant.GlobalConstants;
import org.springframework.web.bind.annotation.GetMapping;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.api.UserApi;
import dev.tagtag.module.auth.service.PermissionResolver;
import dev.tagtag.module.auth.service.CaptchaService;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.api.MenuApi;
import dev.tagtag.contract.iam.api.RoleApi;
import dev.tagtag.contract.auth.dto.RouteRecordStringComponentDTO;
import dev.tagtag.contract.auth.dto.RouteMetaDTO;

import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.ArrayList;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 认证控制器，提供登录、刷新与注销接口
 */
@RestController
@Validated
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/auth")
@Tag(name = "认证管理", description = "认证相关 API 接口")
public class AuthController {

    private final AuthService authService;
    private final UserApi userApi;
    private final PermissionResolver permissionResolver;
    private final CaptchaService captchaService;
    private final MenuApi menuApi;
    private final RoleApi roleApi;



    /**
     * 用户登录（返回访问令牌与刷新令牌）
     *
     * @param req 登录请求
     * @return 令牌结果
     */
    @RateLimit(key = "auth:login", periodSeconds = 60, permits = 10, message = "登录请求过多，请稍后再试")
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录，返回访问令牌与刷新令牌")
    public Result<TokenDTO> login(@Valid @RequestBody LoginRequest req) {
        String inCode = req.getCaptcha().getCode();
        String inId = req.getCaptcha().getCaptchaId();
        if (!captchaService.validate(inId, inCode)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码错误或已过期");
        }
        TokenDTO dto = authService.login(req.getUsername(), req.getPassword());
        // 登录成功后再消费验证码，防止重复使用（同样兼容两种入参）
        captchaService.validateAndConsume(inId, inCode);
        return Result.ok(dto);
    }

    /**
     * 刷新令牌（返回新的访问令牌与刷新令牌）
     *
     * @param req 刷新请求
     * @return 令牌结果
     */
    @RateLimit(key = "auth:refresh", periodSeconds = 60, permits = 30, message = "刷新频率过快，请稍后再试")
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "刷新令牌，返回新的访问令牌与刷新令牌")
    public Result<TokenDTO> refresh(@Valid @RequestBody RefreshRequest req) {
        TokenDTO dto = authService.refresh(req.getRefreshToken());
        return Result.ok(dto);
    }

    /**
     * 注销登录（无状态 JWT 下返回成功）
     *
     * @param req 注销请求
     * @return 操作结果
     */
    @PostMapping("/logout")
    @Operation(summary = "注销登录", description = "用户注销登录")
    public Result<Void> logout(@Valid @RequestBody LogoutRequest req) {
        authService.logout(req.getAccessToken());
        return Result.ok();
    }

    /**
     * 用户注册（公开接口）
     *
     * @param req 注册请求
     * @return 操作结果
     */
    @RateLimit(key = "auth:register", periodSeconds = 60, permits = 10, message = "注册请求过多，请稍后再试")
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户注册，公开接口")
    public Result<Void> register(@Valid @RequestBody RegisterRequest req) {
        if (req.getCaptcha() == null || req.getCaptcha().getCaptchaId() == null || req.getCaptcha().getCode() == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码不能为空");
        }
        String inId = req.getCaptcha().getCaptchaId();
        String inCode = req.getCaptcha().getCode();
        if (!captchaService.validate(inId, inCode)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码错误或已过期");
        }
        authService.register(req.getUsername(), req.getPassword());
        captchaService.validateAndConsume(inId, inCode);
        return Result.ok();
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserDTO> me() {
        Long uid = AuthContext.getCurrentUserId();
        UserDTO user = userApi.getUserById(uid).getData();
        if (user != null) user.setPassword(null);
        return Result.ok(user);
    }

    /**
     * 获取当前用户的权限编码集合
     *
     * @return 权限码列表
     */
    @GetMapping("/codes")
    @Operation(summary = "获取权限编码", description = "获取当前用户的权限编码集合")
    public Result<Set<String>> codes() {
        UserPrincipal principal = AuthContext.getCurrentPrincipal();
        // 如果是超级管理员，返回所有权限码
        if (principal.isAdmin()) {
            Set<String> all = menuApi.listAllPermissionCodes().getData();
            return Result.ok(all);
        }
        List<Long> roleIds = new ArrayList<>(principal.getRoleIds());
        Set<String> perms = permissionResolver.resolvePermissions(roleIds);
        return Result.ok(perms);
    }

    /**
     * 获取当前用户可访问的路由记录（按角色分配的目录/菜单进行过滤；按钮不生成路由）
     *
     * @return 路由记录列表
     */
    @GetMapping("/menu/all")
    @Operation(summary = "获取用户路由", description = "获取当前用户可访问的路由记录")
    public Result<List<RouteRecordStringComponentDTO>> allMenus() {
        UserPrincipal principal = AuthContext.getCurrentPrincipal();
        List<MenuDTO> filteredTree;
        List<MenuDTO> fullTree = menuApi.listMenuTree(null).getData();

        // 如果是超级管理员，不过滤，直接返回完整树
        if (principal.isAdmin()) {
            filteredTree = fullTree;
        } else {
            List<Long> roleIds = new ArrayList<>(Objects.requireNonNullElse(principal.getRoleIds(), Collections.emptySet()));
            List<Long> assignedMenuIds = roleApi.listMenuIdsByRoleIds(roleIds).getData();
            Set<Long> idSet = assignedMenuIds == null ? Collections.emptySet() : new LinkedHashSet<>(assignedMenuIds);
            filteredTree = filterTreeByIds(fullTree, idSet);
        }

        List<RouteRecordStringComponentDTO> routes = new ArrayList<>();
        for (MenuDTO dto : filteredTree) {
            RouteRecordStringComponentDTO r = toRoute(dto);
            if (r != null) routes.add(r);
        }
        return Result.ok(routes);
    }

    /**
     * 依据分配的菜单ID集合过滤菜单树：保留命中节点或其包含命中子节点的祖先；丢弃未分配的分支
     *
     * @param tree 完整菜单树
     * @param ids  已分配菜单ID集合（目录/菜单/按钮）
     * @return 过滤后的树
     */
    private List<MenuDTO> filterTreeByIds(List<MenuDTO> tree, java.util.Set<Long> ids) {
        if (tree == null || tree.isEmpty()) return java.util.Collections.emptyList();
        List<MenuDTO> res = new ArrayList<>();
        for (MenuDTO node : tree) {
            List<MenuDTO> children = filterTreeByIds(node.getChildren(), ids);
            boolean included = ids != null && node.getId() != null && ids.contains(node.getId()) || !children.isEmpty();
            if (included) {
                MenuDTO copy = new MenuDTO();
                copy.setId(node.getId());
                copy.setMenuCode(node.getMenuCode());
                copy.setMenuName(node.getMenuName());
                copy.setRemark(node.getRemark());
                copy.setParentId(node.getParentId());
                copy.setPath(node.getPath());
                copy.setComponent(node.getComponent());
                copy.setIcon(node.getIcon());
                copy.setSort(node.getSort());
                copy.setStatus(node.getStatus());
                copy.setMenuType(node.getMenuType());
                copy.setHideInMenu(node.getHideInMenu());
                copy.setLink(node.getLink());
                copy.setIframeSrc(node.getIframeSrc());
                copy.setKeepAlive(node.getKeepAlive());
                copy.setChildren(children);
                res.add(copy);
            }
        }
        return res;
    }

    /**
     * 将菜单DTO转换为前端路由记录
     */
    private RouteRecordStringComponentDTO toRoute(MenuDTO dto) {
        if (dto == null) return null;
        Integer status = dto.getStatus();
        if (status != null && status == 0) return null;
        Integer menuType = dto.getMenuType();
        if (menuType != null && menuType == 2) return null;

        RouteRecordStringComponentDTO r = new RouteRecordStringComponentDTO();
        r.setPath(dto.getPath() == null || dto.getPath().isBlank() ? "/" : dto.getPath());
        r.setName(dto.getMenuCode());

        RouteMetaDTO meta = new RouteMetaDTO();
        meta.setTitle(dto.getMenuName());
        meta.setIcon(dto.getIcon());
        meta.setKeepAlive(Boolean.TRUE.equals(dto.getKeepAlive()));
        meta.setHideInMenu(Boolean.TRUE.equals(dto.getHideInMenu()));
        meta.setOrder(dto.getSort() == null ? 0 : dto.getSort());

        if (dto.getIframeSrc() != null && !dto.getIframeSrc().isBlank()) {
            r.setComponent("IFrameView");
            meta.setIframeSrc(dto.getIframeSrc());
        } else if (dto.getLink() != null && !dto.getLink().isBlank()) {
            meta.setLink(dto.getLink());
            r.setComponent("BasicLayout");
        } else if (dto.getComponent() != null && !dto.getComponent().isBlank()) {
            String comp = dto.getComponent().startsWith("/") ? dto.getComponent() : "/" + dto.getComponent();
            r.setComponent(comp);
        } else {
            r.setComponent("BasicLayout");
        }

        r.setMeta(meta);

        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            List<RouteRecordStringComponentDTO> children = new ArrayList<>();
            for (MenuDTO child : dto.getChildren()) {
                RouteRecordStringComponentDTO cr = toRoute(child);
                if (cr != null) children.add(cr);
            }
            if (!children.isEmpty()) {
                r.setChildren(children);
                String firstPath = children.get(0).getPath();
                if (firstPath != null && firstPath.startsWith("/")) {
                    r.setRedirect(firstPath);
                }
            }
        }
        return r;
    }

}
