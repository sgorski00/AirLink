ALTER TABLE airplanes ADD COLUMN serial_number VARCHAR(255);
UPDATE airplanes SET serial_number = code WHERE serial_number IS NULL;
ALTER TABLE airplanes ALTER COLUMN serial_number SET NOT NULL;
ALTER TABLE airplanes ADD CONSTRAINT airplanes_serial_number_key UNIQUE (serial_number);

ALTER TABLE airplanes DROP CONSTRAINT airplanes_code_key;