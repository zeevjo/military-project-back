package org.example.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.db.DatabaseConnection;
import org.example.entities.HistoryEntity;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HistoryRepository {

    private static final Logger logger = LogManager.getLogger(HistoryRepository.class);

    public List<HistoryEntity> getProductStockHistory() {
        logger.info("Attempting to execute stored procedure: GetProductStockHistory");

        List<HistoryEntity> historyList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL GetProductStockHistory()}")) {


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String productName    = rs.getString("Product_Name");
                    String currentStatus  = rs.getString("Current_Status");

                    Integer soldierId     = rs.getInt("Soldier_Id");
                    if (rs.wasNull()) {
                        soldierId = null;
                    }

                    String soldierName    = rs.getString("Soldier_Name");

                    String modificationDate = rs.getString("Modification_Date");

                    HistoryEntity entity = new HistoryEntity(
                            productName,
                            currentStatus,
                            soldierId,
                            soldierName,
                            modificationDate
                    );

                    historyList.add(entity);
                }
            }

            logger.info("Stored procedure GetProductStockHistory executed successfully.");

        } catch (SQLException e) {
            logger.error("SQL error while executing stored procedure: {}", e.getMessage(), e);
            throw new RuntimeException("Error executing stored procedure", e);
        }

        return historyList;
    }
}
