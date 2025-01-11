package org.example.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entities.AssignmentEntity;
import org.example.db.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssignRepository {

    private static final Logger logger = LogManager.getLogger(AssignRepository.class);

    public String assignProductToSoldier(AssignmentEntity assignmentEntity) {
        logger.info("Attempting to execute stored procedure to assign product");

        String resultMessage = null;

        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL AssignProductToSoldier(?, ?, ?)}")) {

            stmt.setInt(1, assignmentEntity.getStockId());
            stmt.setInt(2, assignmentEntity.getSoldierId());
            stmt.setTimestamp(3, assignmentEntity.getAssignmentDate());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    resultMessage = rs.getString("Message");
                }
            }

            logger.info("Stored procedure executed successfully, result: {}", resultMessage);

        } catch (SQLException e) {
            logger.error("SQL error while executing stored procedure: {}", e.getMessage(), e);
            throw new RuntimeException("Error executing stored procedure", e);
        }

        return resultMessage;
    }
}
