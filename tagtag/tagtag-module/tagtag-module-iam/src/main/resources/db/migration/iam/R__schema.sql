DROP TABLE IF EXISTS iam_user_role;
DROP TABLE IF EXISTS iam_role_menu;
DROP TABLE IF EXISTS iam_user;
DROP TABLE IF EXISTS iam_role;
DROP TABLE IF EXISTS iam_menu;
DROP TABLE IF EXISTS iam_dept;

CREATE TABLE iam_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL,
  nickname VARCHAR(128),
  email VARCHAR(128),
  mobile VARCHAR(32),
  gender TINYINT,
  dept_id BIGINT,
  status TINYINT DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  update_by BIGINT,
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY uk_username (username),
  KEY idx_status (status),
  KEY idx_dept_id (dept_id),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE iam_role (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL,
  name VARCHAR(128) NOT NULL,
  status TINYINT DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_by BIGINT,
  update_by BIGINT,
  UNIQUE KEY uk_role_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE iam_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  parent_id BIGINT NOT NULL DEFAULT 0,
  menu_name VARCHAR(50) NOT NULL,
  menu_code VARCHAR(100) NOT NULL,
  path VARCHAR(200) DEFAULT NULL,
  component VARCHAR(200) DEFAULT NULL,
  icon VARCHAR(50) DEFAULT NULL,
  sort INT NOT NULL DEFAULT 0,
  status TINYINT(1) NOT NULL DEFAULT 1,
  menu_type TINYINT(1) NOT NULL DEFAULT 1,
  is_hidden TINYINT(1) NOT NULL DEFAULT 0,
  is_external TINYINT(1) NOT NULL DEFAULT 0,
  external_url VARCHAR(500) DEFAULT NULL,
  remark VARCHAR(500) DEFAULT NULL,
  is_keepalive TINYINT(1) NOT NULL DEFAULT 0,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  create_by BIGINT DEFAULT NULL,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  update_by BIGINT DEFAULT NULL,
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY uk_menu_code (menu_code) USING BTREE,
  KEY idx_parent_id (parent_id) USING BTREE,
  KEY idx_menu_name (menu_name) USING BTREE,
  KEY idx_status (status) USING BTREE,
  KEY idx_sort (sort) USING BTREE,
  KEY idx_menu_type (menu_type) USING BTREE,
  KEY idx_is_hidden (is_hidden) USING BTREE,
  KEY idx_is_external (is_external) USING BTREE,
  KEY idx_create_time (create_time) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE iam_dept (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(128) NOT NULL,
  code VARCHAR(64) NOT NULL,
  parent_id BIGINT,
  sort INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  leader VARCHAR(64),
  phone VARCHAR(32),
  email VARCHAR(128),
  remark VARCHAR(500),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_by BIGINT,
  update_by BIGINT,
  INDEX idx_parent (parent_id),
  UNIQUE KEY uk_dept_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE iam_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  UNIQUE KEY uk_user_role (user_id, role_id),
  INDEX idx_user (user_id),
  INDEX idx_role (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE iam_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  UNIQUE KEY uk_role_menu (role_id, menu_id),
  INDEX idx_role2 (role_id),
  INDEX idx_menu (menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;