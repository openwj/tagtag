package dev.tagtag.contract.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDTO {

    private Long id;
    @NotBlank(message = "菜单编码不能为空")
    @Size(max = 100, message = "菜单编码长度不能超过100")
    private String menuCode;

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过50")
    private String menuName;

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    private Long parentId;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer status;
    /** 0目录 1菜单 2按钮 */
    private Integer menuType;
    private Integer isHidden;
    private Integer isExternal;
    private String externalUrl;
    private Integer isKeepalive;

    /** 树结构子节点（树接口返回时使用） */
    private List<MenuDTO> children;
}
