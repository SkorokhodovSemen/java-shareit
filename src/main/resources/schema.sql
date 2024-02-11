DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

CREATE TYPE IF NOT EXISTS offer AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS users (id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, name VARCHAR NOT NULL, email VARCHAR NOT NULL, CONSTRAINT UQ_USER_EMAIL UNIQUE (email));

CREATE TABLE IF NOT EXISTS items (id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, name VARCHAR NOT NULL, description VARCHAR NOT NULL, available BOOLEAN, owner INTEGER REFERENCES users(id) ON delete CASCADE, request INTEGER);

CREATE TABLE IF NOT EXISTS bookings (id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, item INTEGER REFERENCES items(id) ON delete CASCADE, booker INTEGER REFERENCES users(id) ON delete CASCADE, status offer, start_time TIMESTAMP, end_time TIMESTAMP);

CREATE TABLE IF NOT EXISTS comments (id INTEGER NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, text VARCHAR, item INTEGER REFERENCES items(id) ON delete CASCADE, author VARCHAR, created TIMESTAMP);