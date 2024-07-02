-- Создание таблицы Users
CREATE TABLE Users
(
    ID       SERIAL PRIMARY KEY,           -- Айди пользователя, автоинкремент, первичный ключ
    Login    VARCHAR(255) UNIQUE NOT NULL, -- Логин пользователя, username или email
    Password VARCHAR(255)        NOT NULL  -- Пароль пользователя (должен храниться в зашифрованном виде)
);

-- Создание таблицы Locations
CREATE TABLE Locations
(
    ID        SERIAL PRIMARY KEY,                -- Айди локации, автоинкремент, первичный ключ
    Name      VARCHAR(255)   NOT NULL,           -- Название локации
    User_Id   INT            NOT NULL,           -- Пользователь, добавивший эту локацию
    Latitude  DECIMAL(10, 8) NOT NULL,           -- Широта локации
    Longitude DECIMAL(11, 8) NOT NULL,           -- Долгота локации
    FOREIGN KEY (User_Id) REFERENCES Users (ID), -- Внешний ключ, ссылающийся на таблицу Users
    UNIQUE (User_Id, Latitude, Longitude)
);

-- Создание таблицы Sessions
CREATE TABLE Sessions
(
    ID         UUID PRIMARY KEY,                -- Айди сессии, GUID, первичный ключ
    User_Id    INT       NOT NULL,              -- Пользователь, для которого сессия создана
    Expires_At TIMESTAMP NOT NULL,              -- Время истечения сессии
    FOREIGN KEY (User_Id) REFERENCES Users (ID) -- Внешний ключ, ссылающийся на таблицу Users
);