-- Создание таблицы Users
CREATE TABLE Users
(
    ID       SERIAL PRIMARY KEY,
    Login    VARCHAR(255) UNIQUE NOT NULL,
    Password VARCHAR(255)        NOT NULL
);

-- Создание таблицы Locations
CREATE TABLE Locations
(
    ID        SERIAL PRIMARY KEY,
    Name      VARCHAR(255)   NOT NULL,
    User_Id   INT            NOT NULL,
    Latitude  DECIMAL(10, 8) NOT NULL,
    Longitude DECIMAL(11, 8) NOT NULL,
    FOREIGN KEY (User_Id) REFERENCES Users (ID),
    UNIQUE (User_Id, Latitude, Longitude)
);

-- Создание таблицы Sessions
CREATE TABLE Sessions
(
    ID         UUID PRIMARY KEY,
    User_Id    INT       NOT NULL,
    Expires_At TIMESTAMP NOT NULL,
    FOREIGN KEY (User_Id) REFERENCES Users (ID)
);