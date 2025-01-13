package org.example.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.db.DatabaseConnection;
import org.example.entities.ProductFullTableEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductFullTableRepository {

    private static final Logger logger = LogManager.getLogger(ProductFullTableRepository.class);

    public List<ProductFullTableEntity> getProductFullTableByName(String productName) {
        logger.info("Fetching product full table for product: {}", productName);

        List<ProductFullTableEntity> resultList = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL GetProductFullTableByName(?)}")) {

            callableStatement.setString(1, productName);

            try (ResultSet rs = callableStatement.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("Product_Name");
                    String currentStatus = rs.getString("Current_Status");
                    Timestamp lastModified = rs.getTimestamp("Last_Modified");
                    String modifiedBy = rs.getString("Modified_By_Soldier");

                    ProductFullTableEntity entity = new ProductFullTableEntity(
                            name,
                            currentStatus,
                            lastModified,
                            modifiedBy
                    );
                    resultList.add(entity);
                }
            }
        } catch (SQLException e) {
            logger.error("Error executing stored procedure GetProductFullTableByName: {}", e.getMessage(), e);
            throw new RuntimeException("Database error during stored procedure execution", e);
        }

        return resultList;
    }
}
