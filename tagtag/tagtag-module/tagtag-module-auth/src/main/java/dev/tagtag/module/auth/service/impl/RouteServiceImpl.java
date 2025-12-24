package dev.tagtag.module.auth.service.impl;

import dev.tagtag.contract.auth.dto.RouteMetaDTO;
import dev.tagtag.contract.auth.dto.RouteRecordStringComponentDTO;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.module.auth.service.RouteService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 路由服务实现：处理菜单到路由的转换和过滤
 */
@Service
public class RouteServiceImpl implements RouteService {
    
    @Override
    public List<MenuDTO> filterMenuTreeByIds(List<MenuDTO> tree, Set<Long> ids) {
        if (tree == null || tree.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<MenuDTO> res = new ArrayList<>();
        for (MenuDTO node : tree) {
            List<MenuDTO> children = filterMenuTreeByIds(node.getChildren(), ids);
            boolean isNodeIncluded = ids != null && node.getId() != null && ids.contains(node.getId());
            boolean hasValidChildren = !children.isEmpty();
            
            if (isNodeIncluded || hasValidChildren) {
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
    
    @Override
    public List<RouteRecordStringComponentDTO> convertMenuToRoutes(List<MenuDTO> menuTree) {
        List<RouteRecordStringComponentDTO> routes = new ArrayList<>();
        for (MenuDTO dto : menuTree) {
            RouteRecordStringComponentDTO route = convertMenuToRoute(dto);
            if (route != null) {
                routes.add(route);
            }
        }
        return routes;
    }
    
    @Override
    public RouteRecordStringComponentDTO convertMenuToRoute(MenuDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Integer status = dto.getStatus();
        if (status != null && status == 0) {
            return null;
        }
        
        Integer menuType = dto.getMenuType();
        if (menuType != null && menuType == 2) {
            return null;
        }
        
        RouteRecordStringComponentDTO route = new RouteRecordStringComponentDTO();
        route.setPath(dto.getPath() == null || dto.getPath().isBlank() ? "/" : dto.getPath());
        route.setName(dto.getMenuCode());
        
        RouteMetaDTO meta = new RouteMetaDTO();
        meta.setTitle(dto.getMenuName());
        meta.setIcon(dto.getIcon());
        meta.setKeepAlive(Boolean.TRUE.equals(dto.getKeepAlive()));
        meta.setHideInMenu(Boolean.TRUE.equals(dto.getHideInMenu()));
        meta.setOrder(dto.getSort() == null ? 0 : dto.getSort());
        
        if (dto.getIframeSrc() != null && !dto.getIframeSrc().isBlank()) {
            route.setComponent("IFrameView");
            meta.setIframeSrc(dto.getIframeSrc());
        } else if (dto.getLink() != null && !dto.getLink().isBlank()) {
            meta.setLink(dto.getLink());
            route.setComponent("BasicLayout");
        } else if (dto.getComponent() != null && !dto.getComponent().isBlank()) {
            String comp = dto.getComponent().startsWith("/") ? dto.getComponent() : "/" + dto.getComponent();
            route.setComponent(comp);
        } else {
            route.setComponent("BasicLayout");
        }
        
        route.setMeta(meta);
        
        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            List<RouteRecordStringComponentDTO> children = new ArrayList<>();
            for (MenuDTO child : dto.getChildren()) {
                RouteRecordStringComponentDTO childRoute = convertMenuToRoute(child);
                if (childRoute != null) {
                    children.add(childRoute);
                }
            }
            if (!children.isEmpty()) {
                route.setChildren(children);
                String firstPath = children.get(0).getPath();
                if (firstPath != null && firstPath.startsWith("/")) {
                    route.setRedirect(firstPath);
                }
            }
        }
        
        return route;
    }
}