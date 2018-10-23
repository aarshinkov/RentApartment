CREATE TABLE APARTMENTS
(
    apartment_id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    address nvarchar2(200) NOT NULL,
    price double NOT NULL,
    area double NOT NULL,
    floor int,
    rooms int NOT NULL,
    status nvarchar2(6)
);