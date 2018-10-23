CREATE TABLE CLIENTS
(
    client_id int AUTO_INCREMENT PRIMARY KEY NOT NULL,
    first_name nvarchar2(60) NOT NULL,
    last_name nvarchar2(60) NOT NULL,
    gender char NOT NULL,
    personal_number nvarchar2(10) NOT NULL
);