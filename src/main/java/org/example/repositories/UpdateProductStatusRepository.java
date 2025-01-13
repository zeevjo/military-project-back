package org.example.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.db.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class UpdateProductStatusRepository {

    private static final Logger logger = LogManager.getLogger(UpdateProductStatusRepository.class);

    public void updateProductStatus(int productStockId, int newStatusId, int changedBy) {
        logger.info("Updating product status. ProductStockId: {}, NewStatusId: {}, ChangedBy: {}",
                productStockId, newStatusId, changedBy);

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL update_product_status(?, ?, ?)}")) {

            callableStatement.setInt(1, productStockId);
            callableStatement.setInt(2, newStatusId);
            callableStatement.setInt(3, changedBy);

            callableStatement.executeUpdate();

            logger.info("Product status updated successfully.");

        } catch (SQLException e) {
            logger.error("Error executing update_product_status procedure: {}", e.getMessage(), e);
            throw new RuntimeException("Database error during update product status", e);
        }
    }
}
