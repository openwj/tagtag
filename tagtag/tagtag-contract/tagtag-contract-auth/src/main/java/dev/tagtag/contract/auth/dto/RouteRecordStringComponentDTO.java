package dev.tagtag.contract.auth.dto;

import java.util.List;
import lombok.Data;

/**
 * 后端返回的动态路由记录（组件为字符串路径或布局名）
 */
@Data
public class RouteRecordStringComponentDTO {
  private String path;
  private String name;
  private String component;
  private List<RouteRecordStringComponentDTO> children;
  private RouteMetaDTO meta;
  private String redirect;
}