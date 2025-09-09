-- V1: Baseline schema that matches the JPA entities

CREATE TABLE users (
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  email      VARCHAR(255) NOT NULL UNIQUE,
  password   VARCHAR(255) NOT NULL,
  full_name  VARCHAR(255) NOT NULL,
  enabled    BOOLEAN NOT NULL DEFAULT TRUE,
  verified   BOOLEAN NOT NULL DEFAULT FALSE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE user_roles (
  user_id BIGINT NOT NULL,
  role    VARCHAR(32) NOT NULL,
  PRIMARY KEY (user_id, role),
  CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE properties (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  owner_id    BIGINT NOT NULL,
  title       VARCHAR(200) NOT NULL,
  type        VARCHAR(40)  NOT NULL,
  city        VARCHAR(120) NOT NULL,
  address     VARCHAR(255) NOT NULL,
  price       DECIMAL(12,2) NOT NULL,
  bedrooms    INT,
  bathrooms   INT,
  area        DOUBLE,
  status      VARCHAR(40)  NOT NULL,
  description LONGTEXT,
  amenities   LONGTEXT,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_properties_owner (owner_id),
  CONSTRAINT fk_properties_owner
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE property_images (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  property_id BIGINT NOT NULL,
  url         VARCHAR(500) NOT NULL,
  sort_order  INT DEFAULT 0,
  INDEX idx_propimg_property (property_id),
  CONSTRAINT fk_property_images_property
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE rental_applications (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  property_id BIGINT NOT NULL,
  tenant_id   BIGINT NOT NULL,
  message     LONGTEXT,
  status      VARCHAR(32) NOT NULL,
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_app_unique (property_id, tenant_id),
  INDEX idx_app_tenant (tenant_id),
  CONSTRAINT fk_app_property
    FOREIGN KEY (property_id) REFERENCES properties(id) ON DELETE CASCADE,
  CONSTRAINT fk_app_tenant
    FOREIGN KEY (tenant_id)   REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
