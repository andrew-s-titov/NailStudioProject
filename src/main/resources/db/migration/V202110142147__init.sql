CREATE TABLE IF NOT EXISTS users
(
    user_id    IDENTITY NOT NULL,
    first_name VARCHAR(50),
    last_name  VARCHAR(50),
    phone      VARCHAR(12) UNIQUE,
    e_mail     VARCHAR(255),
    discount   ENUM ('ZERO', 'TWO', 'THREE', 'FIVE', 'TEN') DEFAULT 'ZERO',
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS roles
(
    role_id   TINYINT     NOT NULL,
    role_name VARCHAR(20) NOT NULL,
    PRIMARY KEY (role_id),
    UNIQUE (role_name)
);

INSERT INTO roles
VALUES (101, 'admin'),
       (102, 'master'),
       (103, 'client');

CREATE TABLE IF NOT EXISTS user_role
(
    user_id BIGINT  NOT NULL,
    role_id TINYINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id),
    UNIQUE (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS records
(
    record_id IDENTITY NOT NULL,
    user_id   BIGINT   NOT NULL,
    date      DATE,
    time      ENUM ('NINE', 'THIRTEEN', 'SEVENTEEN'),
    PRIMARY KEY (record_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

COMMIT;