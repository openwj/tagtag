package dev.tagtag.module.auth.service;

import dev.tagtag.contract.auth.dto.RouteRecordStringComponentDTO;
import dev.tagtag.contract.iam.dto.MenuDTO;

import java.util.List;
import java.util.Set;

/**
 * 路由服务：处理菜单到路由的转换和过滤
 */
public interface RouteService {
    
    /**
     * 依据分配的菜单ID集合过滤菜单树
     * 
     * @param tree 完整菜单树
     * @param ids 已分配菜单ID集合
     * @return 过滤后的树
     */
    List<MenuDTO> filterMenuTreeByIds(List<MenuDTO> tree, Set<Long> ids);
    
    /**
     * 将菜单DTO列表转换为前端路由记录列表
     * 
     * @param menuTree 菜单树
     * @return 前端路由记录列表
     */
    List<RouteRecordStringComponentDTO> convertMenuToRoutes(List<MenuDTO> menuTree);
    
    /**
     * 将单个菜单DTO转换为前端路由记录
     * 
     * @param menu 菜单DTO
     * @return 前端路由记录
     */
    RouteRecordStringComponentDTO convertMenuToRoute(MenuDTO menu);
}