-- Create accounts table
CREATE TABLE IF NOT EXISTS account (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       username TEXT NOT NULL UNIQUE,
                                       email TEXT NOT NULL UNIQUE,
                                       password TEXT NOT NULL -- Plain text for simplicity (not recommended for production)
);

-- Create characters table
CREATE TABLE IF NOT EXISTS character (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         account_id BIGINT NOT NULL,
                                         name TEXT NOT NULL,
                                         character_class TEXT NOT NULL, -- Renamed 'class' to 'character_class' for clarity
                                         health INT NOT NULL,
                                         attack_power INT NOT NULL, -- Renamed 'attack' to 'attack_power'
                                         experience INT NOT NULL DEFAULT 0,
                                         defense_power INT, -- Renamed 'defense' to 'defense_power'
                                         stamina INT,
                                         healing_power INT, -- Renamed 'healing' to 'healing_power'
                                         mana INT,
                                         FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE
    );

-- Create leaderboard table
CREATE TABLE IF NOT EXISTS leaderboard (
                                           character_id BIGINT NOT NULL,
                                           wins INT NOT NULL DEFAULT 0,
                                           losses INT NOT NULL DEFAULT 0,
                                           draws INT NOT NULL DEFAULT 0,
                                           FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE
    );

-- Create matches table
CREATE TABLE IF NOT EXISTS match (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     challenger_id BIGINT NOT NULL,
                                     opponent_id BIGINT NOT NULL,
                                     match_outcome TEXT NOT NULL,
                                     challenger_xp INT NOT NULL DEFAULT 0,
                                     opponent_xp INT NOT NULL DEFAULT 0,
                                     FOREIGN KEY (challenger_id) REFERENCES character(id) ON DELETE CASCADE,
    FOREIGN KEY (opponent_id) REFERENCES character(id) ON DELETE CASCADE
    );

-- Create rounds table
CREATE TABLE IF NOT EXISTS round (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     match_id BIGINT NOT NULL,
                                     round_number INT NOT NULL,
                                     character_id BIGINT NOT NULL,
                                     health_delta INT NOT NULL,
                                     stamina_delta INT DEFAULT 0, -- Set default to prevent NULL issues
                                     mana_delta INT DEFAULT 0, -- Set default to prevent NULL issues
                                     FOREIGN KEY (match_id) REFERENCES match(id) ON DELETE CASCADE,
    FOREIGN KEY (character_id) REFERENCES character(id) ON DELETE CASCADE
    );
