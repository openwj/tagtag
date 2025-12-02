DROP TABLE IF EXISTS storage_file;

CREATE TABLE storage_file (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  public_id VARCHAR(36) NOT NULL,
  name VARCHAR(255) NOT NULL,
  original_name VARCHAR(255),
  ext VARCHAR(32),
  size BIGINT,
  mime_type VARCHAR(128),
  storage_type VARCHAR(32) DEFAULT 'local',
  url VARCHAR(512),
  path VARCHAR(1024),
  checksum VARCHAR(128),
  bucket VARCHAR(128),
  status TINYINT DEFAULT 1,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  create_by BIGINT,
  update_by BIGINT,
  deleted TINYINT(1) NOT NULL DEFAULT 0,
  UNIQUE KEY uk_public_id (public_id),
  KEY idx_ext (ext),
  KEY idx_mime (mime_type),
  KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
