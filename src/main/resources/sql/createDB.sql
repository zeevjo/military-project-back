DROP DATABASE IF EXISTS MilitaryDB_1;
CREATE DATABASE MilitaryDB_1;
USE MilitaryDB_1;

CREATE TABLE Battalion (
  Id integer UNIQUE NOT NULL AUTO_INCREMENT,
  Name nvarchar(255),
  Created_At timestamp
);

CREATE TABLE Company (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Battalion_Id integer,
  Name nvarchar(255),
  Created_At timestamp
);

CREATE TABLE Platoon (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Company_Id integer,
  Name nvarchar(255),
  Created_At timestamp
);

CREATE TABLE Soldier (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Platoon_Id integer,
  Name nvarchar(255),
  Created_At timestamp
);

CREATE TABLE Armory (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Battalion_Id integer UNIQUE NOT NULL,
  Name nvarchar(255),
  Created_At timestamp
);

CREATE TABLE Product_Type (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Name nvarchar(255) COMMENT 'Weapon, Accessory, etc.',
  Created_At timestamp
);

CREATE TABLE Product (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Product_Type_Id integer,
  Name nvarchar(255),
  Description nvarchar(255),
  Created_At timestamp
);

CREATE TABLE Status (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Name nvarchar(255) COMMENT 'e.g., Available, Damaged, UnderRepair',
  Description nvarchar(255) COMMENT 'Optional description for additional context',
  Created_At timestamp
);

CREATE TABLE Product_Stock (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Product_Id integer,
  Armory_Id integer,
  Status_Id integer COMMENT 'References the standardized status',
  Created_At timestamp
);

CREATE TABLE Product_Stock_Status_Log (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Product_Stock_Id integer,
  Previous_Status_Id integer COMMENT 'References the previous status',
  New_Status_Id integer COMMENT 'References the updated status',
  Changed_At timestamp,
  Changed_By integer COMMENT 'Tracks the user who made the change'
);

CREATE TABLE Product_Assignment (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Product_Stock_Id integer,
  Soldier_Id integer,
  Assigned_At timestamp,
  Returned_At timestamp COMMENT 'NULL if not returned'
);

CREATE TABLE Users (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Username nvarchar(255),
  Password nvarchar(255) COMMENT 'Hashed password',
  Created_At timestamp
);

CREATE TABLE Role (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Name nvarchar(255) COMMENT 'Administrator, Company Admin, Platoon Admin, etc.',
  Created_At timestamp
);

CREATE TABLE User_Role (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  User_Id integer,
  Role_Id integer,
  Assigned_At timestamp
);

CREATE TABLE Access_Level (
  Id integer PRIMARY KEY AUTO_INCREMENT,
  Role_Id integer,
  Battalion_Id integer COMMENT 'Access to battalion (for regimental admins)',
  Company_Id integer COMMENT 'Access to company (for company admins)',
  Platoon_Id integer COMMENT 'Access to platoon (for platoon admins)',
  Created_At timestamp
);

ALTER TABLE Battalion ADD FOREIGN KEY (Id) REFERENCES Armory (Id);

ALTER TABLE Company ADD FOREIGN KEY (Battalion_Id) REFERENCES Battalion (Id);

ALTER TABLE Platoon ADD FOREIGN KEY (Company_Id) REFERENCES Company (Id);

ALTER TABLE Soldier ADD FOREIGN KEY (Platoon_Id) REFERENCES Platoon (Id);

ALTER TABLE Armory ADD FOREIGN KEY (Battalion_Id) REFERENCES Battalion (Id);

ALTER TABLE Product ADD FOREIGN KEY (Product_Type_Id) REFERENCES Product_Type (Id);

ALTER TABLE Product_Stock ADD FOREIGN KEY (Product_Id) REFERENCES Product (Id);

ALTER TABLE Product_Stock ADD FOREIGN KEY (Armory_Id) REFERENCES Armory (Id);

ALTER TABLE Product_Stock ADD FOREIGN KEY (Status_Id) REFERENCES Status (Id);

ALTER TABLE Product_Stock_Status_Log ADD FOREIGN KEY (Product_Stock_Id) REFERENCES Product_Stock (Id);

ALTER TABLE Product_Stock_Status_Log ADD FOREIGN KEY (Previous_Status_Id) REFERENCES Status (Id);

ALTER TABLE Product_Stock_Status_Log ADD FOREIGN KEY (New_Status_Id) REFERENCES Status (Id);

ALTER TABLE Product_Stock_Status_Log ADD FOREIGN KEY (Changed_By) REFERENCES Soldier (Id);

ALTER TABLE Product_Assignment ADD FOREIGN KEY (Product_Stock_Id) REFERENCES Product_Stock (Id);

ALTER TABLE Product_Assignment ADD FOREIGN KEY (Soldier_Id) REFERENCES Soldier (Id);

ALTER TABLE User_Role ADD FOREIGN KEY (User_Id) REFERENCES Users (Id);

ALTER TABLE User_Role ADD FOREIGN KEY (Role_Id) REFERENCES Role (Id);

ALTER TABLE Access_Level ADD FOREIGN KEY (Role_Id) REFERENCES Role (Id);

ALTER TABLE Access_Level ADD FOREIGN KEY (Battalion_Id) REFERENCES Battalion (Id);

ALTER TABLE Access_Level ADD FOREIGN KEY (Company_Id) REFERENCES Company (Id);

ALTER TABLE Access_Level ADD FOREIGN KEY (Platoon_Id) REFERENCES Platoon (Id);


---------------------------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE GetProductStockDetails(IN stockId INT)
BEGIN
    SELECT
        ps.Id AS StockId,
        p.Name AS ProductName,
        pt.Name AS ProductType,
        s.Name AS CurrentStatus, -- Fetch status name from the Status table
        a.Name AS ArmoryLocation,
        IF(pa.Id IS NOT NULL AND pa.Returned_At IS NULL, 'Yes', 'No') AS IsCurrentlyAssigned,
        IF(pa.Id IS NOT NULL AND pa.Returned_At IS NULL, sol.Name, NULL) AS AssignedTo,
        IF(pa.Id IS NOT NULL AND pa.Returned_At IS NULL, pa.Assigned_At, NULL) AS AssignmentDate
    FROM
        Product_Stock ps
        INNER JOIN Product p ON ps.Product_Id = p.Id
        INNER JOIN Product_Type pt ON p.Product_Type_Id = pt.Id
        INNER JOIN Status s ON ps.Status_Id = s.Id -- Join with the Status table
        INNER JOIN Armory a ON ps.Armory_Id = a.Id
        LEFT JOIN Product_Assignment pa ON ps.Id = pa.Product_Stock_Id AND pa.Returned_At IS NULL
        LEFT JOIN Soldier sol ON pa.Soldier_Id = sol.Id
    WHERE
        ps.Id = stockId;
END$$

DELIMITER ;

---------------------------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE GetProductStock()
BEGIN
    SELECT
        p.Name AS ProductName,
        COUNT(ps.Id) AS TotalInStock,
        SUM(CASE WHEN s.Name = 'Available' THEN 1 ELSE 0 END) AS TotalAvailable,
        SUM(CASE WHEN s.Name = 'UnderRepair' THEN 1 ELSE 0 END) AS TotalUnderMaintenance,
        SUM(CASE WHEN s.Name = 'Assigned' THEN 1 ELSE 0 END) AS TotalAssigned
    FROM
        Product_Stock ps
    INNER JOIN
        Product p ON ps.Product_Id = p.Id
    INNER JOIN
        Status s ON ps.Status_Id = s.Id -- Join the Status table to get status names
    GROUP BY
        p.Name;
END$$

DELIMITER ;

-----------------------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE AssignProductToSoldier(
    IN stockId INT,
    IN soldierId INT,
    IN assignmentDate TIMESTAMP
)
BEGIN
    DECLARE availableStatusId INT;
    DECLARE assignedStatusId INT;

    -- Get the Status IDs for 'Available' and 'Assigned'
    SELECT Id INTO availableStatusId FROM Status WHERE Name = 'Available' LIMIT 1;
    SELECT Id INTO assignedStatusId FROM Status WHERE Name = 'Assigned' LIMIT 1;

    -- Check if the product stock is available
    IF EXISTS (
        SELECT 1
        FROM Product_Stock
        WHERE Id = stockId AND Status_Id = availableStatusId
    ) THEN
        -- Update the product stock status
        UPDATE Product_Stock
        SET Status_Id = assignedStatusId
        WHERE Id = stockId;

        -- Insert the assignment record
        INSERT INTO Product_Assignment (Product_Stock_Id, Soldier_Id, Assigned_At)
        VALUES (stockId, soldierId, assignmentDate);

        SELECT 'Product successfully assigned to soldier.' AS Message;
    ELSE
        SELECT 'Error: Product is not available for assignment.' AS Message;
    END IF;
END$$

DELIMITER ;

---------------------------------------------------------------------------------
DELIMITER $$

CREATE PROCEDURE Login(
    IN inputUsername NVARCHAR(255),
    IN inputPassword NVARCHAR(255)
)
BEGIN
    DECLARE userExists INT;

    -- Check if a user exists with the given username and password
    SELECT COUNT(*) INTO userExists
    FROM Users
    WHERE Username = inputUsername AND Password = inputPassword;

    -- Return appropriate message
    IF userExists > 0 THEN
        SELECT 'Login successful' AS Message;
    ELSE
        SELECT 'Invalid username or password' AS Message;
    END IF;
END$$

DELIMITER ;



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
(6, 5, 'Emily Davis', NOW());

-- Insert mock data into Product_Type
INSERT INTO Product_Type (Id, Name, Created_At) VALUES
(1, 'Weapon', NOW()),
(2, 'Ammunition', NOW()),
(3, 'Gear', NOW());

-- Insert mock data into Product
INSERT INTO Product (Id, Product_Type_Id, Name, Description, Created_At) VALUES
(1, 1, 'M16 Rifle', 'Standard issue rifle', NOW()),
(2, 1, 'M4 Carbine', 'Compact assault rifle', NOW()),
(3, 2, '5.56mm Rounds', 'Ammunition for rifles', NOW()),
(4, 3, 'Kevlar Vest', 'Bulletproof vest', NOW());

-- Insert mock data into Status
INSERT INTO Status (Id, Name, Description, Created_At) VALUES
(1, 'Available', 'Product is available for use', NOW()),
(2, 'Assigned', 'Product is currently assigned to a soldier', NOW()),
(3, 'UnderRepair', 'Product is under maintenance', NOW());

-- Insert mock data into Product_Stock
INSERT INTO Product_Stock (Id, Product_Id, Armory_Id, Status_Id, Created_At) VALUES
(1, 1, 1, 1, NOW()), -- M16 Rifle in Main Armory 1, Available
(2, 2, 1, 1, NOW()), -- M4 Carbine in Main Armory 1, Available
(3, 3, 2, 1, NOW()), -- 5.56mm Rounds in Main Armory 2, Available
(4, 4, 3, 2, NOW()); -- Kevlar Vest in Main Armory 3, Assigned

-- Insert mock data into Product_Assignment
INSERT INTO Product_Assignment (Id, Product_Stock_Id, Soldier_Id, Assigned_At, Returned_At) VALUES
(1, 4, 1, NOW(), NULL); -- Kevlar Vest assigned to John Doe

-- Insert mock data into User
INSERT INTO Users (Id, Username, Password, Created_At) VALUES
(1, 'admin', 'admin123', NOW()),
(2, 'officer', 'securepass', NOW()),
(3, 'soldier1', 'soldier1pass', NOW());

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