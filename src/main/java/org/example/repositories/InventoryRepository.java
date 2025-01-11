package org.example.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.db.DatabaseConnection;
import org.example.entities.InventoryEntity;
import org.example.entities.ItemEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryRepository {
    private static final Logger logger = LogManager.getLogger(InventoryRepository.class);

    public List<InventoryEntity> findAll() {
        List<InventoryEntity> inventoryList = new ArrayList<>();
        logger.info("Attempting to retrieve all inventory items");
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL GetProductStock()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                inventoryList.add(new InventoryEntity(
                        rs.getString("productName"),
                        rs.getInt("totalInStock"),
                        rs.getInt("totalAvailable"),
                        rs.getInt("totalUnderMaintenance"),
                        rs.getInt("totalAssigned")
                ));
            }

            logger.info("Successfully retrieved {} inventory items", inventoryList.size());
        } catch (SQLException e) {
            logger.error("SQL Error while retrieving inventory: {}", e.getMessage(), e);
        }
        return inventoryList;
    }


    public ItemEntity findItemDetailsByStockId(int stockId) {
        logger.info("Fetching item details for stock ID: {}", stockId);
        ItemEntity item = null;
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL GetProductStockDetails(?)}")) {

            stmt.setInt(1, stockId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    item = new ItemEntity(
                            rs.getInt("StockId"),
                            rs.getString("ProductName"),
                            rs.getString("ProductType"),
                            rs.getString("CurrentStatus"),
                            rs.getString("ArmoryLocation"),
                            rs.getString("IsCurrentlyAssigned"),
                            rs.getString("AssignedTo"),
                            rs.getString("AssignmentDate")
                    );
                }
            }

            logger.info("Successfully retrieved item details for stock ID: {}", stockId);
        } catch (SQLException e) {
            logger.error("SQL Error while retrieving item details for stock ID {}: {}", stockId, e.getMessage(), e);
        }
        return item;
    }
}