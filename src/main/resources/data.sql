CREATE TABLE brand_tb
(
    id   INT         NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE model_tb
(
    id       INT         NOT NULL PRIMARY KEY,
    name     VARCHAR(50) NOT NULL,
    brand_id INT,
    FOREIGN KEY (brand_id) REFERENCES brand_tb (id)
);
CREATE TABLE customer_tb
(
    id      INT          NOT NULL PRIMARY KEY,
    name    VARCHAR(50)  NOT NULL,
    email   VARCHAR(70)  NOT NULL,
    phone   VARCHAR(11)  NOT NULL,
    address VARCHAR(100) NOT NULL,
    role    VARCHAR(30)
);
CREATE TABLE car_tb
(
    id               INT         NOT NULL PRIMARY KEY,
    plate            VARCHAR(10) NOT NULL,
    manufacture_year SMALLINT    NOT NULL,
    color            VARCHAR(30) NOT NULL,
    model_id         INT         NOT NULL,
    customer_id      INT         NOT NULL,
    FOREIGN KEY (model_id) REFERENCES model_tb (id),
    FOREIGN KEY (customer_id) REFERENCES customer_tb (id)
);
CREATE TABLE user_tb
(
    id       INT          NOT NULL PRIMARY KEY,
    name     VARCHAR(50)  NOT NULL,
    login    VARCHAR(30)  NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(30)  NOT NULL
);
CREATE TABLE task_tb
(
    id          INT            NOT NULL PRIMARY KEY,
    car_id      INT            NOT NULL,
    user_id     INT            NOT NULL,
    description TEXT           NOT NULL,
    observation TEXT           NOT NULL,
    status      VARCHAR(30)    NOT NULL,
    start_date  DATE           NOT NULL,
    end_date    DATE           NOT NULL,
    estimate    DECIMAL(10, 2) NOT NULL,
    final_value DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (car_id) REFERENCES car_tb (id),
    FOREIGN KEY (user_id) REFERENCES user_tb (id)
);