package dev.tagtag.module.auth.controller;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.auth.dto.TokenDTO;
import dev.tagtag.contract.auth.dto.LoginRequest;
import dev.tagtag.contract.auth.dto.RefreshRequest;
import dev.tagtag.contract.auth.dto.LogoutRequest;
import dev.tagtag.contract.auth.dto.RegisterRequest;
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
import org.springframework.web.bind.annotation.RequestHeader;
import dev.tagtag.framework.security.JwtService;
import dev.tagtag.contract.iam.api.UserApi;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.module.auth.service.PermissionResolver;
import dev.tagtag.module.auth.service.CaptchaService;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import dev.tagtag.contract.iam.api.MenuApi;
import dev.tagtag.contract.auth.dto.RouteRecordStringComponentDTO;
import dev.tagtag.contract.auth.dto.RouteMetaDTO;

import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.Objects;
import java.util.ArrayList;

import dev.tagtag.kernel.annotation.RateLimit;

/**
 * 认证控制器，提供登录、刷新与注销接口
 */
@RestController
@Validated
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserApi userApi;
    private final PermissionResolver permissionResolver;
    private final CaptchaService captchaService;
    private final MenuApi menuApi;

    /**
     * 用户登录（返回访问令牌与刷新令牌）
     *
     * @param req 登录请求
     * @return 令牌结果
     */
    @RateLimit(key = "auth:login", periodSeconds = 60, permits = 10, message = "登录请求过多，请稍后再试")
    @PostMapping("/login")
    public Result<TokenDTO> login(@Valid @RequestBody LoginRequest req) {
        // 验证码前置校验（兼容嵌套与扁平两种入参）：优先使用 req.getCaptcha() 的值
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
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req.getUsername(), req.getPassword());
        return Result.ok();
    }

    /**
     * 获取当前登录用户信息
     *
     * @param authorization Authorization头（Bearer Token）
     * @return 用户信息
     */
    @GetMapping("/me")
    public Result<UserDTO> me(@RequestHeader(name = GlobalConstants.HEADER_AUTHORIZATION, required = false) String authorization) {
        String token = authorization == null ? null : authorization.replace(GlobalConstants.TOKEN_PREFIX, "").trim();
        String subject = jwtService.getSubject(token);
        UserDTO user = userApi.getUserByUsername(subject).getData();
        if (user != null) {
            user.setPassword(null);
        }
        return Result.ok(user);
    }

    /**
     * 获取当前用户的权限编码集合
     *
     * @param authorization Authorization头（Bearer Token）
     * @return 权限码列表
     */
    @GetMapping("/codes")
    public Result<Set<String>> codes(@RequestHeader(name = GlobalConstants.HEADER_AUTHORIZATION, required = false) String authorization) {
        String token = authorization == null ? null : authorization.replace(GlobalConstants.TOKEN_PREFIX, "").trim();
        String subject = jwtService.getSubject(token);
        UserDTO user = userApi.getUserByUsername(subject).getData();
        List<Long> roleIds = user == null ? Collections.emptyList() : Objects.requireNonNullElse(user.getRoleIds(), Collections.emptyList());
        Set<String> perms = permissionResolver.resolvePermissions(roleIds);
        return Result.ok(perms);
    }

    /**
     * 获取当前用户可访问的路由记录（后端适配）
     */
    @GetMapping("/menu/all")
    public Result<List<RouteRecordStringComponentDTO>> allMenus() {
        List<MenuDTO> tree = menuApi.listMenuTree((MenuQueryDTO) null).getData();
        List<RouteRecordStringComponentDTO> routes = new ArrayList<>();
        if (tree != null) {
            for (MenuDTO dto : tree) {
                RouteRecordStringComponentDTO r = toRoute(dto);
                if (r != null) routes.add(r);
            }
        }
        return Result.ok(routes);
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
        meta.setKeepAlive(Boolean.TRUE.equals(dto.getIsKeepalive()));
        meta.setHide(Boolean.TRUE.equals(dto.getIsHidden()));
        meta.setOrder(dto.getSort() == null ? 0 : dto.getSort());

        if (Boolean.TRUE.equals(dto.getIsExternal())) {
            r.setComponent("IFrameView");
            meta.setIframeSrc(dto.getExternalUrl());
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
