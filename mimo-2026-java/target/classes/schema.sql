DROP TABLE IF EXISTS plushies;
DROP TABLE IF EXISTS factories;
DROP TABLE IF EXISTS brands;
DROP TABLE IF EXISTS distributors;

-- Create brands table
CREATE TABLE IF NOT EXISTS brands (
   id BIGINT AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(100),
    founded_year INT
    );

-- Create distributors table
CREATE TABLE IF NOT EXISTS distributors (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(100)
    );

-- Create factories table (each factory belongs to a distributor)
CREATE TABLE IF NOT EXISTS factories (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(100),
    number_of_employees INT,
    distributor_id BIGINT NOT NULL,
    FOREIGN KEY (distributor_id) REFERENCES distributors(id)
    );

CREATE TABLE IF NOT EXISTS plushies(
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(255) NOT NULL,
       brand_id BIGINT NOT NULL,
       distributor_id BIGINT NOT NULL,
       factory_id BIGINT NOT NULL,
       category VARCHAR(50) NOT NULL,
       FOREIGN KEY (brand_id) REFERENCES brands(id) ON DELETE cascade,
       FOREIGN KEY (distributor_id) REFERENCES distributors(id),
       FOREIGN KEY (factory_id) REFERENCES factories(id)
);
