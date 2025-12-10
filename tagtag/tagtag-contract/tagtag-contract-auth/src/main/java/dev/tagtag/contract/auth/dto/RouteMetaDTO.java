package dev.tagtag.contract.auth.dto;

import lombok.Data;

/**
 * 路由 Meta 信息 DTO
 */
@Data
public class RouteMetaDTO {
  private String title;
  private String icon;
  private Boolean keepAlive;
  private Boolean hideInMenu;
  private Integer order;
  private String link;
  private String iframeSrc;
}
