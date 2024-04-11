ALTER TABLE race DROP CONSTRAINT fk_first_horse;
ALTER TABLE race DROP CONSTRAINT fk_second_horse;
ALTER TABLE race DROP CONSTRAINT fk_winner;
ALTER TABLE race DROP CONSTRAINT fk_tournament;
ALTER TABLE race DROP CONSTRAINT fk_previous_race_first;
ALTER TABLE race DROP CONSTRAINT fk_previous_race_second;

ALTER TABLE participation DROP CONSTRAINT fk_tournament_id;
ALTER TABLE participation DROP CONSTRAINT fk_horse_id;

ALTER TABLE horse DROP CONSTRAINT fk_breed_id;

DELETE FROM race WHERE id < 0;
DELETE FROM participation WHERE id < 0;
DELETE FROM tournament WHERE id < 0;
DELETE FROM horse WHERE id < 0;
DELETE FROM breed WHERE id < 0;

INSERT INTO breed (id, name)
VALUES
    (-1, 'Andalusian'),
    (-2, 'Appaloosa'),
    (-3, 'Arabian'),
    (-4, 'Belgian Draft'),
    (-5, 'Connemara Pony'),
    (-6, 'Dartmoor Pony'),
    (-7, 'Friesian'),
    (-8, 'Haflinger'),
    (-9, 'Hanoverian'),
    (-10, 'Icelandic Horse'),
    (-11, 'Lipizzaner'),
    (-12, 'Oldenburg'),
    (-13, 'Paint Horse'),
    (-14, 'Quarter Horse'),
    (-15, 'Shetland Pony'),
    (-16, 'Tinker'),
    (-17, 'Trakehner'),
    (-18, 'Warmblood'),
    (-19, 'Welsh Cob'),
    (-20, 'Welsh Pony');

-- Füge Pferdedaten hinzu
INSERT INTO horse (id, name, sex, date_of_birth, height, weight, breed_id)
VALUES
    (-1, 'Wendy', 'FEMALE', '2019-08-05', 1.40, 380, -15),
    (-2, 'Hugo', 'MALE', '2020-02-20', 1.20, 320, -20),
    (-3, 'Bella', 'FEMALE', '2005-04-08', 1.45, 550, -1),
    (-4, 'Thunder', 'MALE', '2008-07-15', 1.60, 600, -2),
    (-5, 'Luna', 'FEMALE', '2012-11-22', 1.65, 650, -3),
    (-6, 'Apollo', 'MALE', '2003-09-03', 1.52, 500, -4),
    (-7, 'Sophie', 'FEMALE', '2010-06-18', 1.70, 700, -5),
    (-8, 'Max', 'MALE', '2006-03-27', 1.55, 580, -6),
    (-9, 'Bella', 'FEMALE', '2002-08-09', 1.48, 520, -7),
    (-10, 'Rocky', 'MALE', '2013-05-05', 1.55, 620, -8),
    (-11, 'Daisy', 'FEMALE', '2007-02-12', 1.30, 350, -9),
    (-12, 'Charlie', 'MALE', '2011-09-21', 1.68, 680, -10),
    (-13, 'Ruby', 'FEMALE', '2004-07-30', 1.10, 280, -11),
    (-14, 'Duke', 'MALE', '2009-03-14', 1.75, 800, -12),
    (-15, 'Rosie', 'FEMALE', '2001-12-08', 1.57, 590, -13),
    (-16, 'Jack', 'MALE', '2014-10-25', 1.52, 560, -14),
    (-17, 'Lilly', 'FEMALE', '2008-06-03', 1.40, 400, -15),
    (-18, 'Sam', 'MALE', '2010-01-17', 1.65, 650, -16),
    (-19, 'Misty', 'FEMALE', '2005-11-09', 1.25, 320, -17),
    (-20, 'Max', 'MALE', '2012-08-29', 1.72, 670, -18),
    (-21, 'Bella', 'FEMALE', '2003-07-06', 1.50, 580, -19),
    (-22, 'Rocky', 'MALE', '2007-04-12', 1.40, 450, -1),
    (-23, 'Misty', 'FEMALE', '2015-03-12', 1.32, 360, -7),
    (-24, 'Rocky', 'MALE', '2018-08-19', 1.42, 480, -6),
    (-25, 'Lucky', 'MALE', '2019-05-25', 1.58, 620, -5),
    (-26, 'Daisy', 'FEMALE', '2017-12-01', 1.28, 340, -9),
    (-27, 'Buddy', 'MALE', '2016-09-14', 1.68, 700, -10),
    (-28, 'Molly', 'FEMALE', '2014-04-03', 1.55, 580, -13),
    (-29, 'Cody', 'MALE', '2019-11-30', 1.45, 550, -2),
    (-30, 'Rosie', 'FEMALE', '2016-06-28', 1.52, 520, -14),
    (-31, 'Leo', 'MALE', '2017-03-05', 1.70, 720, -8),
    (-32, 'Luna', 'FEMALE', '2018-10-10', 1.62, 670, -19);

INSERT INTO tournament (id, name, date_of_start, date_of_end)
VALUES
    (-1, '2023 Wien Race', '2023-08-05', '2023-11-05'),
    (-2, '2022 Sarajevo Race', '2022-05-05', '2022-05-05'),
    (-3, '2021 Wien 1st Race', '2021-01-01', '2021-02-01'),
    (-4, '2021 Wien 2nd Race', '2021-03-01', '2021-04-01'),
    (-5, '2016 Tournament Mostar 1st', '2016-01-01', '2016-02-01'),
    (-6, '2016 Tournament Mostar 2nd', '2016-03-01', '2016-04-01'),
    (-7, '2016 Tournament Mostar 3rd', '2016-05-01', '2016-06-01'),
    (-8, '2016 Tournament Mostar 4th', '2016-07-01', '2016-08-01'),
    (-9, '2016 Tournament Mostar Final', '2016-09-01', '2016-10-01');

INSERT INTO participation (id, tournament_id, horse_id, entry_number)
VALUES
    (-1, -1, -1, 1),
    (-2, -1, -2, 2),
    (-3, -1, -3, 3),
    (-4, -1, -4, 4),
    (-5, -1, -5, 5),
    (-6, -1, -6, 6),
    (-7, -1, -7, 7),
    (-8, -1, -8, 8),

    (-9, -2, -1, 1),
    (-10, -2, -2, 2),
    (-11, -2, -3, 3),
    (-12, -2, -4, 4),
    (-13, -2, -5, 5),
    (-14, -2, -6, 6),
    (-15, -2, -7, 7),
    (-16, -2, -8, 8),

    (-17, -3, -1, 1),
    (-18, -3, -2, 2),
    (-19, -3, -3, 3),
    (-20, -3, -4, 4),
    (-21, -3, -5, 5),
    (-22, -3, -6, 6),
    (-23, -3, -7, 7),
    (-24, -3, -8, 8),

    (-25, -4, -4, 1),
    (-26, -4, -5, 2),
    (-27, -4, -6, 3),
    (-28, -4, -7, 4),
    (-29, -4, -8, 5),
    (-30, -4, -9, 6),
    (-31, -4, -10, 7),
    (-32, -4, -11, 8),

    (-33, -5, -16, 1),
    (-34, -5, -17, 2),
    (-35, -5, -18, 3),
    (-36, -5, -19, 4),
    (-37, -5, -20, 5),
    (-38, -5, -21, 6),
    (-39, -5, -22, 7),
    (-40, -5, -23, 8),

    (-41, -6, -16, 1),
    (-42, -6, -17, 2),
    (-43, -6, -18, 3),
    (-44, -6, -19, 4),
    (-45, -6, -20, 5),
    (-46, -6, -21, 6),
    (-47, -6, -22, 7),
    (-48, -6, -23, 8),

    (-49, -7, -16, 1),
    (-50, -7, -17, 2),
    (-51, -7, -18, 3),
    (-52, -7, -19, 4),
    (-53, -7, -20, 5),
    (-54, -7, -21, 6),
    (-55, -7, -22, 7),
    (-56, -7, -23, 8),

    (-57, -8, -16, 1),
    (-58, -8, -17, 2),
    (-59, -8, -18, 3),
    (-60, -8, -19, 4),
    (-61, -8, -20, 5),
    (-62, -8, -21, 6),
    (-63, -8, -22, 7),
    (-64, -8, -23, 8),

    (-65, -9, -16, 1),
    (-66, -9, -17, 2),
    (-67, -9, -18, 3),
    (-68, -9, -19, 4),
    (-69, -9, -20, 5),
    (-70, -9, -21, 6),
    (-71, -9, -22, 7),
    (-72, -9, -23, 8);

INSERT INTO race (id, first_horse, second_horse, winner, tournament_id, round, previous_race_first_horse, previous_race_second_horse)
VALUES
    (-1, -1, -2, null, -1, 1, null, null),
    (-2, -3, -4, null, -1, 1, null, null),
    (-3, -5, -6, null, -1, 1, null, null),
    (-4, -7, -8, null, -1, 1, null, null),
    (-5, null, null, null, -1, 2, -1, -2),
    (-6, null, null, null, -1, 2, -3, -4),
    (-7, null, null, null, -1, 3, -5, -6),

    (-8, -1, -2, -1, -2, 1, null, null),
    (-9, -3, -4, -3, -2, 1, null, null),
    (-10, -5, -6, -5, -2, 1, null, null),
    (-11, -7, -8, -7, -2, 1, null, null),
    (-12, -1, -3, -3, -2, 2, -8, -9),
    (-13, -5, -7, -7, -2, 2, -10, -11),
    (-14, -3, -7, -7, -2, 3, -12, -13),

    (-15, -1, -2, null, -3, 1, null, null),
    (-16, -3, -4, null, -3, 1, null, null),
    (-17, -5, -6, null, -3, 1, null, null),
    (-18, -7, -8, null, -3, 1, null, null),
    (-19, null, null, null, -3, 2, -15, -16),
    (-20, null, null, null, -3, 2, -17, -18),
    (-21, null, null, null, -3, 3, -19, -20),

    (-22, -4, -5, null, -4, 1, null, null),
    (-23, -6, -7, null, -4, 1, null, null),
    (-24, -8, -9, null, -4, 1, null, null),
    (-25, -10, -11, null, -4, 1, null, null),
    (-26, null, null, null, -4, 2, -22, -23),
    (-27, null, null, null, -4, 2, -24, -25),
    (-28, null, null, null, -4, 3, -26, -27),

    (-29, -16, -17, null, -5, 1, null, null),
    (-30, -18, -19, null, -5, 1, null, null),
    (-31, -20, -21, null, -5, 1, null, null),
    (-32, -22, -23, null, -5, 1, null, null),
    (-33, null, null, null, -5, 2, -29, -30),
    (-34, null, null, null, -5, 2, -31, -32),
    (-35, null, null, null, -5, 3, -33, -34),

    (-36, -16, -19, null, -6, 1, null, null),
    (-37, -18, -17, null, -6, 1, null, null),
    (-38, -20, -23, null, -6, 1, null, null),
    (-39, -22, -21, null, -6, 1, null, null),
    (-40, null, null, null, -6, 2, -36, -37),
    (-41, null, null, null, -6, 2, -38, -39),
    (-42, null, null, null, -6, 3, -40, -41),

    (-43, -16, -18, null, -7, 1, null, null),
    (-44, -17, -19, null, -7, 1, null, null),
    (-45, -20, -22, null, -7, 1, null, null),
    (-46, -21, -21, null, -7, 1, null, null),
    (-47, null, null, null, -7, 2, -43, -44),
    (-48, null, null, null, -7, 2, -45, -46),
    (-49, null, null, null, -7, 3, -47, -48),

    (-50, -16, -20, -16, -8, 1, null, null),
    (-51, -17, -21, -17, -8, 1, null, null),
    (-52, -18, -22, -22, -8, 1, null, null),
    (-53, -19, -23, null, -8, 1, null, null),
    (-54, -16, -17, null, -8, 2, -50, -51),
    (-55, -22, null, null, -8, 2, -52, -53),
    (-56, null, null, null, -8, 3, -54, -55),

    (-57, null, null, null, -9, 1, null, null),
    (-58, null, null, null, -9, 1, null, null),
    (-59, null, null, null, -9, 1, null, null),
    (-60, null, null, null, -9, 1, null, null),
    (-61, null, null, null, -9, 2, -57, -58),
    (-62, null, null, null, -9, 2, -59, -60),
    (-63, null, null, null, -9, 3, -61, -62);

ALTER TABLE horse ADD CONSTRAINT fk_breed_id FOREIGN KEY (breed_id) REFERENCES breed(id);

ALTER TABLE participation ADD CONSTRAINT fk_tournament_id FOREIGN KEY (tournament_id) REFERENCES tournament(id);

ALTER TABLE participation ADD CONSTRAINT fk_horse_id FOREIGN KEY (horse_id) REFERENCES horse(id);

ALTER TABLE race ADD CONSTRAINT fk_first_horse FOREIGN KEY (first_horse) REFERENCES horse(id);
ALTER TABLE race ADD CONSTRAINT fk_second_horse FOREIGN KEY (second_horse) REFERENCES horse(id);
ALTER TABLE race ADD CONSTRAINT fk_winner FOREIGN KEY (winner) REFERENCES horse(id);
ALTER TABLE race ADD CONSTRAINT fk_tournament FOREIGN KEY (tournament_id) REFERENCES tournament(id);
ALTER TABLE race ADD CONSTRAINT fk_previous_race_first FOREIGN KEY (previous_race_first_horse) REFERENCES race(id) ON DELETE CASCADE;
ALTER TABLE race ADD CONSTRAINT fk_previous_race_second FOREIGN KEY (previous_race_second_horse) REFERENCES race(id) ON DELETE CASCADE;