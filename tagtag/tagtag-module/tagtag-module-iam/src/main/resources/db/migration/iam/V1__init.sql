-- 初始化 IAM 相关表结构（与业务一致）
DROP TABLE IF EXISTS iam_user_role;
DROP TABLE IF EXISTS iam_role_menu;
DROP TABLE IF EXISTS iam_user;
DROP TABLE IF EXISTS iam_role;
DROP TABLE IF EXISTS iam_menu;
DROP TABLE IF EXISTS iam_dept;

CREATE TABLE iam_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  username VARCHAR(64) NOT NULL COMMENT '用户登录名（唯一）',
  password VARCHAR(128) NOT NULL COMMENT '用户密码（BCrypt 加密）',
  nickname VARCHAR(128) COMMENT '用户昵称',
  email VARCHAR(128) COMMENT '邮箱',
  mobile VARCHAR(32) COMMENT '手机号',
  gender TINYINT COMMENT '性别：0未知/1男/2女',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用/1启用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  create_by BIGINT COMMENT '创建人ID',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  update_by BIGINT COMMENT '更新人ID',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除/1已删除',
  UNIQUE KEY uk_username (username),
  KEY idx_status (status),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE iam_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  code VARCHAR(64) NOT NULL COMMENT '角色编码（唯一）',
  name VARCHAR(128) NOT NULL COMMENT '角色名称',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用/1启用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by BIGINT COMMENT '创建人ID',
  update_by BIGINT COMMENT '更新人ID',
  UNIQUE KEY uk_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

CREATE TABLE iam_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID，0表示顶级菜单',
  menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
  menu_code VARCHAR(100) NOT NULL COMMENT '菜单编码（唯一，按钮作为后端权限码）',
  path VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
  component VARCHAR(200) DEFAULT NULL COMMENT '组件路径',
  icon VARCHAR(50) DEFAULT NULL COMMENT '菜单图标',
  sort INT NOT NULL DEFAULT 0 COMMENT '排序',
  status TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态：0禁用/1启用',
  menu_type TINYINT(1) NOT NULL DEFAULT 1 COMMENT '菜单类型：0目录/1菜单/2按钮',
  is_hidden TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否隐藏：0显示/1隐藏',
  is_external TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否外链：0否/1是',
  external_url VARCHAR(500) DEFAULT NULL COMMENT '外链地址',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  is_keepalive TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否缓存：0不缓存/1缓存',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  create_by BIGINT DEFAULT NULL COMMENT '创建人ID',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  update_by BIGINT DEFAULT NULL COMMENT '更新人ID',
  deleted TINYINT(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除/1已删除',
  UNIQUE KEY uk_menu_code (menu_code) USING BTREE COMMENT '菜单编码唯一索引',
  KEY idx_parent_id (parent_id) USING BTREE COMMENT '父菜单ID索引',
  KEY idx_menu_name (menu_name) USING BTREE COMMENT '菜单名称索引',
  KEY idx_status (status) USING BTREE COMMENT '状态索引',
  KEY idx_sort (sort) USING BTREE COMMENT '排序索引',
  KEY idx_menu_type (menu_type) USING BTREE COMMENT '菜单类型索引',
  KEY idx_is_hidden (is_hidden) USING BTREE COMMENT '是否隐藏索引',
  KEY idx_is_external (is_external) USING BTREE COMMENT '是否外链索引',
  KEY idx_create_time (create_time) USING BTREE COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

CREATE TABLE iam_dept (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  name VARCHAR(128) NOT NULL COMMENT '部门名称',
  parent_id BIGINT COMMENT '父部门ID',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态：0禁用/1启用',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  create_by BIGINT COMMENT '创建人ID',
  update_by BIGINT COMMENT '更新人ID',
  INDEX idx_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

CREATE TABLE iam_user_role (
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  UNIQUE KEY uk_user_role (user_id, role_id),
  INDEX idx_user (user_id),
  INDEX idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

CREATE TABLE iam_role_menu (
  role_id BIGINT NOT NULL COMMENT '角色ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  UNIQUE KEY uk_role_menu (role_id, menu_id),
  INDEX idx_role2 (role_id),
  INDEX idx_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';