CREATE TABLE task (
                      id          BIGINT       NOT NULL AUTO_INCREMENT,
                      title       VARCHAR(100) NOT NULL,
                      description VARCHAR(500),
                      completed   BOOLEAN      NOT NULL DEFAULT FALSE,
                      PRIMARY KEY (id)
);