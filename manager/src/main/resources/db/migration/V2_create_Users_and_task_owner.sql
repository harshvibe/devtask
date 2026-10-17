CREATE TABLE users (
                       id       BIGINT       NOT NULL AUTO_INCREMENT,
                       username VARCHAR(50)  NOT NULL,
                       email    VARCHAR(100) NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       PRIMARY KEY (id),
                       CONSTRAINT uk_users_username UNIQUE (username),
                       CONSTRAINT uk_users_email UNIQUE (email)
);

ALTER TABLE task ADD COLUMN user_id BIGINT NULL;

ALTER TABLE task
    ADD CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE;