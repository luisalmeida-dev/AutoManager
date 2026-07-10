CREATE SEQUENCE brands_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE brands
(
    id   INTEGER     NOT NULL DEFAULT nextval('brands_id_seq') PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT uk_brands_name UNIQUE (name)
);

CREATE SEQUENCE models_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE models
(
    id       INTEGER     NOT NULL DEFAULT nextval('models_id_seq') PRIMARY KEY,
    name     VARCHAR(50) NOT NULL,
    brand_id INTEGER     NOT NULL,
    CONSTRAINT fk_models_brand FOREIGN KEY (brand_id) REFERENCES brands (id)
);

CREATE SEQUENCE customers_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE customers
(
    id      INTEGER      NOT NULL DEFAULT nextval('customers_id_seq') PRIMARY KEY,
    name    VARCHAR(50)  NOT NULL,
    cpf     VARCHAR(11)  NOT NULL,
    email   VARCHAR(70)  NOT NULL,
    phone   VARCHAR(11)  NOT NULL,
    address VARCHAR(100) NOT NULL,
    role    VARCHAR(30),
    CONSTRAINT uk_customers_cpf UNIQUE (cpf)
);

CREATE SEQUENCE cars_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE cars
(
    id               INTEGER     NOT NULL DEFAULT nextval('cars_id_seq') PRIMARY KEY,
    plate            VARCHAR(10) NOT NULL,
    manufacture_year INTEGER     NOT NULL,
    color            VARCHAR(30) NOT NULL,
    model_id         INTEGER     NOT NULL,
    customer_id      INTEGER     NOT NULL,
    CONSTRAINT uk_cars_plate UNIQUE (plate),
    CONSTRAINT fk_cars_model FOREIGN KEY (model_id) REFERENCES models (id),
    CONSTRAINT fk_cars_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE SEQUENCE users_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE users
(
    id       INTEGER      NOT NULL DEFAULT nextval('users_id_seq') PRIMARY KEY,
    name     VARCHAR(50)  NOT NULL,
    login    VARCHAR(30)  NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(30)  NOT NULL,
    CONSTRAINT uk_users_login UNIQUE (login)
);

CREATE SEQUENCE service_orders_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE service_orders
(
    id          INTEGER        NOT NULL DEFAULT nextval('service_orders_id_seq') PRIMARY KEY,
    car_id      INTEGER        NOT NULL,
    user_id     INTEGER        NOT NULL,
    description TEXT           NOT NULL,
    observation TEXT,
    status      VARCHAR(30)    NOT NULL,
    start_date  DATE           NOT NULL,
    end_date    DATE,
    estimate    DECIMAL(10, 2) NOT NULL,
    final_value DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_service_orders_car FOREIGN KEY (car_id) REFERENCES cars (id),
    CONSTRAINT fk_service_orders_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_models_brand_id ON models (brand_id);
CREATE INDEX idx_cars_model_id ON cars (model_id);
CREATE INDEX idx_cars_customer_id ON cars (customer_id);
CREATE INDEX idx_service_orders_car_id ON service_orders (car_id);
CREATE INDEX idx_service_orders_user_id ON service_orders (user_id);
