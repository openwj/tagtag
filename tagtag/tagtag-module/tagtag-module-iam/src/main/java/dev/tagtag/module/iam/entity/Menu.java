package dev.tagtag.module.iam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("iam_menu")
public class Menu {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long parentId;
    private String menuName;
    private String menuCode;
    private String path;
    private String component;
    private String icon;
    private Integer sort;
    private Integer status;
    private Integer menuType; // 0目录 1菜单 2按钮
    private Integer isHidden;
    private Integer isExternal;
    private String externalUrl;
    private String remark;
    private Integer isKeepalive;
    private LocalDateTime createTime;
    private Long createBy;
    private LocalDateTime updateTime;
    private Long updateBy;
    private Integer deleted;
}
