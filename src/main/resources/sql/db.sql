DROP DATABASE IF EXISTS MilitaryDB;
CREATE DATABASE MilitaryDB;
USE MilitaryDB;

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

