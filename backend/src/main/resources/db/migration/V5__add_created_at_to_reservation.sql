ALTER TABLE reservation
ADD COLUMN created_at DATETIME NULL;

UPDATE reservation
SET created_at = NOW();

ALTER TABLE reservation
MODIFY created_at DATETIME NOT NULL;