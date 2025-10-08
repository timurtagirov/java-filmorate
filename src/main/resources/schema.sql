CREATE TABLE IF NOT EXISTS ratings (
            id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR(255) NOT NULL UNIQUE
          );

CREATE TABLE IF NOT EXISTS films (
            id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            description VARCHAR(200),
            release_date DATE NOT NULL,
            duration INT NOT NULL,
            rating_id INT REFERENCES ratings(id)
          );

CREATE TABLE IF NOT EXISTS genres (
            id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR(255) NOT NULL UNIQUE
          );

CREATE TABLE IF NOT EXISTS films_to_genres (
            film_id INT NOT NULL REFERENCES films(id),
            genre_id INT NOT NULL REFERENCES genres(id),
            CONSTRAINT uq_Films_genres_pair UNIQUE (film_id, genre_id)
          );

CREATE TABLE IF NOT EXISTS users (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            email VARCHAR(255) NOT NULL,
            login VARCHAR(255) NOT NULL,
            name VARCHAR(255),
            birthday DATE NOT NULL
          );

CREATE TABLE IF NOT EXISTS friends (
            user_id INT NOT NULL REFERENCES users(id),
            friend_id INT NOT NULL REFERENCES users(id),
            CONSTRAINT uq_friends_pair UNIQUE (user_id, friend_id)
          );

CREATE TABLE IF NOT EXISTS likes (
            film_id INT NOT NULL REFERENCES films(id),
            user_id INT NOT NULL REFERENCES users(id)
          );
