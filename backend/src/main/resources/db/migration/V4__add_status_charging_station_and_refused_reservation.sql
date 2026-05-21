INSERT INTO status_reservation (name)
VALUES ('REFUSEE');

CREATE TABLE status_charging_station (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO status_charging_station (name)
VALUES
    ('ACTIVE'),
    ('HORS_SERVICE'),
    ('MAINTENANCE'),
    ('DESACTIVEE');

ALTER TABLE charging_station
ADD COLUMN status BIGINT NULL;

UPDATE charging_station
SET status = (
    SELECT id
    FROM status_charging_station
    WHERE name = 'ACTIVE'
);

ALTER TABLE charging_station
MODIFY COLUMN status BIGINT NOT NULL;

ALTER TABLE charging_station
ADD CONSTRAINT fk_charging_station_status
FOREIGN KEY (status)
REFERENCES status_charging_station(id);