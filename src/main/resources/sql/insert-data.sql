-- Disable foreign key checks to handle circular dependencies
SET FOREIGN_KEY_CHECKS = 0;

-- Insert mock data into Battalion
INSERT INTO Battalion (Id, Name, Created_At) VALUES
(1, '1st Infantry Battalion', NOW()),
(2, '2nd Armored Battalion', NOW()),
(3, '3rd Recon Battalion', NOW());

-- Insert mock data into Armory
INSERT INTO Armory (Id, Battalion_Id, Name, Created_At) VALUES
(1, 1, 'Main Armory 1', NOW()),
(2, 2, 'Main Armory 2', NOW()),
(3, 3, 'Main Armory 3', NOW());

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Insert mock data into Company
INSERT INTO Company (Id, Battalion_Id, Name, Created_At) VALUES
(1, 1, 'Alpha Company', NOW()),
(2, 1, 'Bravo Company', NOW()),
(3, 2, 'Charlie Company', NOW()),
(4, 2, 'Delta Company', NOW()),
(5, 3, 'Echo Company', NOW());

-- Insert mock data into Platoon
INSERT INTO Platoon (Id, Company_Id, Name, Created_At) VALUES
(1, 1, '1st Platoon', NOW()),
(2, 1, '2nd Platoon', NOW()),
(3, 2, '1st Platoon', NOW()),
(4, 3, '1st Platoon', NOW()),
(5, 4, '2nd Platoon', NOW()),
(6, 5, '1st Platoon', NOW());

-- Insert mock data into Soldier
INSERT INTO Soldier (Id, Platoon_Id, Name, Created_At) VALUES
(1, 1, 'John Doe', NOW()),
(2, 1, 'Jane Smith', NOW()),
(3, 2, 'Paul Adams', NOW()),
(4, 3, 'Lisa Brown', NOW()),
(5, 4, 'Mark Wilson', NOW()),
(6, 5, 'Emily Davis', NOW()),
(7, 1, 'David Johnson', NOW()),
(8, 2, 'Michael White', NOW()),
(9, 3, 'Sarah Taylor', NOW()),
(10, 4, 'Chris Harris', NOW()),
(11, 5, 'Emma Lewis', NOW()),
(12, 1, 'Sophia Walker', NOW()),
(13, 2, 'Olivia Hall', NOW()),
(14, 3, 'James Allen', NOW()),
(15, 4, 'Benjamin Young', NOW()),
(16, 5, 'Charlotte King', NOW()),
(17, 1, 'Isabella Wright', NOW()),
(18, 2, 'Mia Scott', NOW()),
(19, 3, 'Ava Green', NOW()),
(20, 4, 'Liam Baker', NOW()),
(21, 5, 'Noah Hill', NOW()),
(22, 1, 'Elijah Carter', NOW()),
(23, 2, 'Logan Mitchell', NOW()),
(24, 3, 'Lucas Perez', NOW()),
(25, 4, 'Ethan Sanchez', NOW()),
(26, 5, 'Alexander Ward', NOW()),
(27, 1, 'Jacob Cooper', NOW()),
(28, 2, 'Henry Rogers', NOW()),
(29, 3, 'Ella Brooks', NOW()),
(30, 4, 'Aria Kelly', NOW()),
(31, 5, 'Harper Gray', NOW()),
(32, 1, 'Amelia Edwards', NOW()),
(33, 2, 'Scarlett Murphy', NOW()),
(34, 3, 'Grace Hughes', NOW()),
(35, 4, 'Lily Foster', NOW()),
(36, 5, 'Chloe Howard', NOW()),
(37, 1, 'Victoria Ramirez', NOW()),
(38, 2, 'Zoe Rivera', NOW()),
(39, 3, 'Hannah Morris', NOW()),
(40, 4, 'Ella Diaz', NOW()),
(41, 5, 'Gabriel Martinez', NOW()),
(42, 1, 'Aiden Lee', NOW()),
(43, 2, 'Samuel Thompson', NOW()),
(44, 3, 'Jack Walker', NOW()),
(45, 4, 'Matthew Hall', NOW()),
(46, 5, 'Ryan Allen', NOW()),
(47, 1, 'Carter Young', NOW()),
(48, 2, 'Nathan King', NOW()),
(49, 3, 'Thomas Wright', NOW()),
(50, 4, 'Daniel Scott', NOW());



-- Insert mock data into Product_Type
INSERT INTO Product_Type (Id, Name, Created_At) VALUES
(1, 'Weapon', NOW()),
(2, 'Special combat equipment', NOW()),
(3, 'Gear', NOW());

-- Insert mock data into Product
INSERT INTO Product (Id, Product_Type_Id, Name, Description, Created_At) VALUES
(1, 1, 'Smart Rifle', 'A rifle equipped with advanced targeting systems.', NOW()),
(2, 1, 'Drone Strike System', 'Remote-controlled drone with precision strike capability.', NOW()),
(3, 1, 'Laser Gun', 'A futuristic weapon that uses concentrated light beams.', NOW()),
(4, 2, 'Thermal Imaging Goggles', 'Allows vision based on heat signatures.', NOW()),
(5, 2, 'Ballistic Helmet', 'High-strength helmet for head protection in combat.', NOW()),
(6, 2, 'C4 Explosives', 'Advanced plastic explosives for demolition.', NOW()),
(7, 3, 'Tactical Backpack', 'Designed for carrying tech and survival equipment.', NOW()),
(8, 3, 'Carbon Fiber Rope', 'Lightweight yet extremely durable climbing rope.', NOW()),
(9, 3, 'Advanced Survival Kit', 'Includes modern tools like solar chargers and water purifiers.', NOW()),
(10, 1, 'Electromagnetic Pulse (EMP) Device', 'Weapon that disables electronics in a targeted area.', NOW()),
(11, 1, 'Railgun', 'Electromagnetic weapon that fires projectiles at extreme speeds.', NOW()),
(12, 2, 'Hazmat Suit', 'Protects against chemical, biological, and radiological hazards.', NOW()),
(13, 2, 'Exoskeleton Suit', 'Wearable suit that enhances strength and endurance.', NOW()),
(14, 3, 'Multi-Tool Device', 'Compact gadget with multiple modern tools and features.', NOW()),
(15, 3, 'GPS Tracker', 'Advanced tracking device with live location updates.', NOW());


-- Insert mock data into Status
INSERT INTO Status (Id, Name, Description, Created_At) VALUES
(1, 'Available', 'Product is available for use', NOW()),
(2, 'Assigned', 'Product is currently assigned to a soldier', NOW()),
(3, 'UnderRepair', 'Product is under maintenance', NOW());

-- Insert mock data into Product_Stock
INSERT INTO Product_Stock (Id, Product_Id, Armory_Id, Status_Id, Created_At) VALUES
(1, 13, 3, 3, NOW()),
(2, 5, 2, 3, NOW()),
(3, 1, 1, 3, NOW()),
(4, 15, 2, 2, NOW()),
(5, 1, 3, 3, NOW()),
(6, 8, 2, 1, NOW()),
(7, 6, 1, 3, NOW()),
(8, 12, 3, 2, NOW()),
(9, 9, 1, 1, NOW()),
(10, 11, 2, 1, NOW()),
(11, 4, 3, 2, NOW()),
(12, 7, 2, 1, NOW()),
(13, 15, 3, 1, NOW()),
(14, 2, 1, 3, NOW()),
(15, 10, 2, 2, NOW()),
(16, 8, 3, 1, NOW()),
(17, 3, 1, 2, NOW()),
(18, 7, 2, 3, NOW()),
(19, 13, 3, 1, NOW()),
(20, 6, 1, 2, NOW()),
(21, 11, 2, 1, NOW()),
(22, 14, 3, 3, NOW()),
(23, 5, 1, 3, NOW()),
(24, 9, 2, 2, NOW()),
(25, 10, 3, 1, NOW()),
(26, 2, 1, 2, NOW()),
(27, 3, 2, 1, NOW()),
(28, 4, 3, 3, NOW()),
(29, 15, 1, 1, NOW()),
(30, 1, 2, 3, NOW()),
(31, 8, 3, 2, NOW()),
(32, 13, 1, 1, NOW()),
(33, 7, 2, 3, NOW()),
(34, 5, 3, 1, NOW()),
(35, 12, 1, 2, NOW()),
(36, 14, 2, 3, NOW()),
(37, 10, 3, 1, NOW()),
(38, 6, 1, 2, NOW()),
(39, 9, 2, 1, NOW()),
(40, 3, 3, 3, NOW()),
(41, 11, 1, 1, NOW()),
(42, 4, 2, 3, NOW()),
(43, 7, 3, 2, NOW()),
(44, 14, 1, 2, NOW()),
(45, 8, 2, 1, NOW()),
(46, 10, 3, 3, NOW()),
(47, 12, 1, 3, NOW()),
(48, 15, 2, 2, NOW()),
(49, 9, 3, 1, NOW()),
(50, 5, 1, 1, NOW()),
(51, 13, 2, 3, NOW()),
(52, 2, 3, 2, NOW()),
(53, 6, 1, 3, NOW()),
(54, 1, 2, 1, NOW()),
(55, 11, 3, 3, NOW()),
(56, 7, 1, 2, NOW()),
(57, 9, 2, 1, NOW()),
(58, 14, 3, 3, NOW()),
(59, 3, 1, 1, NOW()),
(60, 12, 2, 2, NOW()),
(61, 8, 3, 1, NOW()),
(62, 4, 1, 2, NOW()),
(63, 5, 2, 1, NOW()),
(64, 10, 3, 3, NOW()),
(65, 15, 1, 3, NOW()),
(66, 1, 2, 2, NOW()),
(67, 13, 3, 1, NOW()),
(68, 6, 1, 2, NOW()),
(69, 7, 2, 3, NOW()),
(70, 9, 3, 1, NOW()),
(71, 2, 1, 3, NOW()),
(72, 12, 2, 2, NOW()),
(73, 14, 3, 1, NOW()),
(74, 4, 1, 3, NOW()),
(75, 11, 2, 2, NOW()),
(76, 3, 3, 1, NOW()),
(77, 8, 1, 2, NOW()),
(78, 10, 2, 3, NOW()),
(79, 15, 3, 1, NOW()),
(80, 5, 1, 2, NOW()),
(81, 13, 2, 1, NOW()),
(82, 2, 3, 3, NOW()),
(83, 6, 1, 1, NOW()),
(84, 9, 2, 2, NOW()),
(85, 7, 3, 1, NOW()),
(86, 14, 1, 3, NOW()),
(87, 12, 2, 2, NOW()),
(88, 1, 3, 1, NOW()),
(89, 4, 1, 2, NOW()),
(90, 8, 2, 3, NOW()),
(91, 15, 3, 1, NOW()),
(92, 3, 1, 2, NOW()),
(93, 11, 2, 3, NOW()),
(94, 13, 3, 2, NOW()),
(95, 5, 1, 3, NOW()),
(96, 6, 2, 1, NOW()),
(97, 2, 3, 2, NOW()),
(98, 9, 1, 1, NOW()),
(99, 14, 2, 3, NOW()),
(100, 10, 3, 1, NOW());


INSERT INTO Product_Assignment (Id, Product_Stock_Id, Soldier_Id, Assigned_At, Returned_At) VALUES
(1, 1, 1, '2025-01-01 10:00:00', NULL), -- Product 1 assigned to Soldier 1, not yet returned
(2, 2, 2, '2025-01-02 12:00:00', '2025-01-05 15:00:00'), -- Product 2 assigned to Soldier 2 and returned
(3, 4, 4, '2025-01-04 14:20:00', NULL), -- Product 4 assigned to Soldier 4, not yet returned
(4, 6, 6, '2025-01-06 18:15:00', NULL), -- Product 6 assigned to Soldier 6, not yet returned
(5, 10, 10, '2025-01-10 15:30:00', '2025-01-12 10:00:00'); -- Product 10 assigned to Soldier 10 and returned


INSERT INTO Product_Stock_Status_Log (Id, Product_Stock_Id, Previous_Status_Id, New_Status_Id, Changed_At, Changed_By) VALUES
(1, 1, 1, 2, '2025-01-01 10:00:00', 1), -- Product moved from Available to Assigned by Soldier 1
(2, 2, 1, 3, '2025-01-02 12:00:00', 2), -- Product moved from Available to Under Repair by Soldier 2
(3, 3, 2, 1, '2025-01-03 09:30:00', 3), -- Product moved from Assigned to Available by Soldier 3
(4, 4, 1, 2, '2025-01-04 14:20:00', 4), -- Product moved from Available to Assigned by Soldier 4
(5, 5, 3, 1, '2025-01-05 16:45:00', 5), -- Product moved from Under Repair to Available by Soldier 5
(6, 6, 1, 2, '2025-01-06 18:15:00', 6), -- Product moved from Available to Assigned by Soldier 6
(7, 7, 1, 3, '2025-01-07 11:00:00', 7), -- Product moved from Available to Under Repair by Soldier 7
(8, 8, 3, 1, '2025-01-08 08:00:00', 8), -- Product moved from Under Repair to Available by Soldier 8
(9, 9, 2, 1, '2025-01-09 10:00:00', 9), -- Product moved from Assigned to Available by Soldier 9
(10, 10, 1, 2, '2025-01-10 15:30:00', 10); -- Product moved from Available to Assigned by Soldier 10

-- Insert mock data into User
INSERT INTO Users (Id, Username, Password, Created_At) VALUES
(1, 'admin@example.com', 'admin123', NOW()),
(2, 'officer@example.com', 'securepass', NOW()),
(3, 'soldier1@example.com', 'soldier1pass', NOW());


-- Insert mock data into Role
INSERT INTO Role (Id, Name, Created_At) VALUES
(1, 'Administrator', NOW()),
(2, 'Officer', NOW()),
(3, 'Soldier', NOW());

-- Insert mock data into User_Role
INSERT INTO User_Role (Id, User_Id, Role_Id, Assigned_At) VALUES
(1, 1, 1, NOW()), -- Admin role for admin user
(2, 2, 2, NOW()), -- Officer role for officer user
(3, 3, 3, NOW()); -- Soldier role for soldier1 user

-- Insert mock data into Access_Level
INSERT INTO Access_Level (Id, Role_Id, Battalion_Id, Company_Id, Platoon_Id, Created_At) VALUES
(1, 1, NULL, NULL, NULL, NOW()), -- Admin: Full access
(2, 2, 1, 1, NULL, NOW()),       -- Officer: Battalion 1, Alpha Company
(3, 3, 1, 1, 1, NOW());          -- Soldier: Battalion 1, Alpha Company, 1st Platoon



