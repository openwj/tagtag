package dev.tagtag.contract.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {

    private Long id;
    private String name;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer status;
    private List<MenuDTO> children;
}

