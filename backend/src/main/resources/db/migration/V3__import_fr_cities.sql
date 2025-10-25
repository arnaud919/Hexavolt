-- V3__import_fr_cities.sql  (MySQL) — importe toutes les communes métropolitaines
START TRANSACTION;

LOAD DATA LOCAL INFILE 'src/main/resources/data/cities_fr.csv'
INTO TABLE city
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@name)
SET name = @name;

COMMIT;