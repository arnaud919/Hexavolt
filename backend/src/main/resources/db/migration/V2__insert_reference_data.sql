-- =====================================================
-- Hexavolt - Reference data (Flyway V2)
-- =====================================================

-- =====================
-- day_of_week
-- =====================
INSERT INTO day_of_week (id, name) VALUES
  (1, 'lundi'),
  (2, 'mardi'),
  (3, 'mercredi'),
  (4, 'jeudi'),
  (5, 'vendredi'),
  (6, 'samedi'),
  (7, 'dimanche');

-- =====================
-- status_reservation
-- =====================
INSERT INTO status_reservation (id, name) VALUES
  (1, 'EN_ATTENTE'),
  (2, 'CONFIRMEE'),
  (3, 'ANNULEE'),
  (4, 'TERMINEE'),
  (5, 'EXPIREE'),
  (6, 'ABSENCE');

-- =====================
-- notification_type
-- =====================
INSERT INTO notification_type (id, label) VALUES
  (1, 'Activation de compte'),
  (2, 'Réinitialisation de mot de passe'),
  (3, 'Confirmation de réservation'),
  (4, 'Rappel de réservation'),
  (5, 'Borne hors ligne'),
  (6, 'Reçu de paiement');

-- =====================
-- power
-- =====================
INSERT INTO power (id, kva_power) VALUES
  (1, 3.7),
  (2, 7.4),
  (3, 11.0),
  (4, 22.0);

-- =====================
-- city (import CSV)
-- =====================
-- flyway:executeInTransaction=false

LOAD DATA LOCAL INFILE '${csvPath}'
INTO TABLE city
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ',' ENCLOSED BY '"' ESCAPED BY '\\'
LINES TERMINATED BY '\n'
IGNORE 1 LINES
(@name)
SET name = TRIM(TRAILING '\r' FROM @name);
