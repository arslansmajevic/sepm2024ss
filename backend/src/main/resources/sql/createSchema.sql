CREATE TABLE IF NOT EXISTS breed
(
  id BIGINT PRIMARY KEY,
  name VARCHAR(32) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS horse
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  -- Instead of an ENUM (H2 specific) this could also be done with a character string type and a check constraint.
  sex ENUM ('MALE', 'FEMALE') NOT NULL,
  date_of_birth DATE NOT NULL,
  height NUMERIC(4,2),
  weight NUMERIC(7,2),
  breed_id BIGINT,
  CONSTRAINT fk_breed_id FOREIGN KEY (breed_id) REFERENCES breed(id)
);

CREATE TABLE IF NOT EXISTS tournament
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  date_of_start DATE NOT NULL,
  date_of_end DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS participation
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  tournament_id BIGINT,
  horse_id BIGINT,
  entry_number BIGINT,
  CONSTRAINT fk_tournament_id FOREIGN KEY (tournament_id) REFERENCES tournament(id),
  CONSTRAINT fk_horse_id FOREIGN KEY (horse_id) REFERENCES horse(id)
);

CREATE TABLE IF NOT EXISTS race
(
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  first_horse BIGINT,
  second_horse BIGINT,
  winner BIGINT,
  tournament_id BIGINT,
  round BIGINT,
  previous_race_first_horse BIGINT,
  previous_race_second_horse BIGINT,
  CONSTRAINT fk_first_horse FOREIGN KEY (first_horse) REFERENCES horse(id),
  CONSTRAINT fk_second_horse FOREIGN KEY (second_horse) REFERENCES horse(id),
  CONSTRAINT fk_winner FOREIGN KEY (winner) REFERENCES horse(id),
  CONSTRAINT fk_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(id),
  CONSTRAINT fk_previous_race_first FOREIGN KEY (previous_race_first_horse) REFERENCES race(id) ON DELETE CASCADE,
  CONSTRAINT fk_previous_race_second FOREIGN KEY (previous_race_second_horse) REFERENCES race(id) ON DELETE CASCADE
);