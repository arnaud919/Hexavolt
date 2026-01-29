-- 3) Données de référence
INSERT INTO day_of_week (id, name) VALUES
  (1, 'LUNDI'),
  (2, 'MARDI'),
  (3, 'MERCREDI'),
  (4, 'JEUDI'),
  (5, 'VENDREDI'),
  (6, 'SAMEDI'),
  (7, 'DIMANCHE')
ON DUPLICATE KEY UPDATE name = VALUES(name);