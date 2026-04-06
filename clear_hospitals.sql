-- Clear all hospitals and blood inventory data
-- Run this script to reset the database before restarting the server

DELETE FROM blood_inventory;
DELETE FROM hospitals;

-- Reset auto-increment counters (optional)
ALTER TABLE hospitals AUTO_INCREMENT = 1;
ALTER TABLE blood_inventory AUTO_INCREMENT = 1;
