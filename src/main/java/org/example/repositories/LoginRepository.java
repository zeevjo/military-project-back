package org.example.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.db.DatabaseConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginRepository {

    private static final Logger logger = LogManager.getLogger(LoginRepository.class);

    public String loginUser(String username, String password) {
        logger.info("Attempting to log in user: {}", username);

        String loginResult = null;

        try (Connection connection = DatabaseConnection.getConnection();
             CallableStatement callableStatement = connection.prepareCall("{CALL Login(?, ?)}")) {

            callableStatement.setString(1, username);
            callableStatement.setString(2, password);

            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    loginResult = resultSet.getString("Message");
                }
            }

        } catch (SQLException e) {
            logger.error("Error executing Login procedure: {}", e.getMessage(), e);
            throw new RuntimeException("Database error during login", e);
        }

        return loginResult;
    }
}
