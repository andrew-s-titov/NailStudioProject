CREATE TABLE IF NOT EXISTS users
(
    user_id    IDENTITY      NOT NULL,
    password   VARCHAR(1000) NOT NULL,
    first_name VARCHAR(50)   NOT NULL,
    last_name  VARCHAR(50)   NOT NULL,
    phone      VARCHAR(13) UNIQUE,
    e_mail     VARCHAR(255),
    discount   ENUM ('ZERO', 'TWO', 'THREE', 'FIVE', 'TEN') DEFAULT 'ZERO',
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS roles
(
    role_id   IDENTITY                          NOT NULL,
    role_name ENUM ('ADMIN', 'STAFF', 'CLIENT') NOT NULL,
    PRIMARY KEY (role_id),
    UNIQUE (role_name)
);

INSERT INTO roles (role_name)
VALUES ('ADMIN'),
       ('STAFF'),
       ('CLIENT');

CREATE TABLE IF NOT EXISTS users_roles
(
    user_id BIGINT NOT NULL,
    role_id INT    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id),
    UNIQUE (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS records
(
    record_id IDENTITY                               NOT NULL,
    client_id BIGINT                                 NOT NULL,
    staff_id  BIGINT,
    date      DATE                                   NOT NULL,
    time      ENUM ('NINE', 'THIRTEEN', 'SEVENTEEN') NOT NULL,
    PRIMARY KEY (record_id),
    FOREIGN KEY (client_id) REFERENCES users (user_id),
    CONSTRAINT fk_staff_id
        FOREIGN KEY (staff_id) REFERENCES users (user_id)
            ON DELETE SET NULL,
    UNIQUE (date, time, staff_id)
);

COMMIT;