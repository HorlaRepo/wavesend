CREATE TABLE countries (
       country_id SERIAL PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       acronym VARCHAR(10),
       currency VARCHAR(50),
       rating INTEGER
);

CREATE TABLE  payment_method (
         id SERIAL PRIMARY KEY,
         name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE country_payment_method (
        country_id INTEGER,
        payment_method_id INTEGER,
        PRIMARY KEY (country_id, payment_method_id),
        FOREIGN KEY (country_id) REFERENCES countries (country_id),
        FOREIGN KEY (payment_method_id) REFERENCES payment_methods (id)
);