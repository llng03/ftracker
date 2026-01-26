UPDATE cost
SET amount = betrag
WHERE amount IS NULL;

ALTER TABLE cost
DROP COLUMN betrag;