START TRANSACTION;

-- 1) Augmenter la taille de label (accents → utf8mb4 recommandé)
ALTER TABLE notification_type
  MODIFY label VARCHAR(64)
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci
  NOT NULL;

-- 2) (re)seeds
INSERT INTO day_of_week (id, name) VALUES
  (1, 'Lundi'),
  (2, 'Mardi'),
  (3, 'Mercredi'),
  (4, 'Jeudi'),
  (5, 'Vendredi'),
  (6, 'Samedi'),
  (7, 'Dimanche')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO status_reservation (id, name) VALUES
  (1, 'PENDING'),
  (2, 'CONFIRMED'),
  (3, 'CANCELLED'),
  (4, 'COMPLETED'),
  (5, 'EXPIRED'),
  (6, 'NO_SHOW')
ON DUPLICATE KEY UPDATE name = VALUES(name);

INSERT INTO notification_type (id, label) VALUES
  (1, 'Activation de compte'),
  (2, 'Réinitialisation de mot de passe'),
  (3, 'Confirmation de réservation'),
  (4, 'Rappel de réservation'),
  (5, 'Borne hors ligne'),
  (6, 'Reçu de paiement')
ON DUPLICATE KEY UPDATE label = VALUES(label);

COMMIT;