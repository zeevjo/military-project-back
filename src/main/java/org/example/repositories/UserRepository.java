package org.example.repositories;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.db.DatabaseConnection;
import org.example.entities.UserEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    public List<UserEntity> findAll() {
        List<UserEntity> users = new ArrayList<>();
        logger.info("Attempting to retrieve all users");
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL GetAllUsers()}");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(new UserEntity(rs.getInt("id"), rs.getString("username")));
            }

            logger.info("Successfully retrieved {} users", users.size());
        } catch (SQLException e) {
            logger.error("SQL Error while retrieving users: {}", e.getMessage(), e);
        }
        return users;
    }

    public boolean save(UserEntity user) {
        logger.info("Attempting to save user with username: {}", user.getUsername());
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL AddUser(?)}")) {

            stmt.setString(1, user.getUsername());
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                logger.info("Successfully saved user with username: {}", user.getUsername());
            } else {
                logger.warn("Failed to save user with username: {}", user.getUsername());
            }
            return success;
        } catch (SQLException e) {
            logger.error("SQL Error while saving user: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean update(UserEntity user) {
        logger.info("Attempting to update user with ID: {}", user.getId());
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL UpdateUser(?, ?)}")) {

            stmt.setInt(1, user.getId());
            stmt.setString(2, user.getUsername());
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                logger.info("Successfully updated user with ID: {}", user.getId());
            } else {
                logger.warn("Failed to update user with ID: {}", user.getId());
            }
            return success;
        } catch (SQLException e) {
            logger.error("SQL Error while updating user: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean delete(int id) {
        logger.info("Attempting to delete user with ID: {}", id);
        try (Connection conn = DatabaseConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL DeleteUser(?)}")) {

            stmt.setInt(1, id);
            boolean success = stmt.executeUpdate() > 0;
            if (success) {
                logger.info("Successfully deleted user with ID: {}", id);
            } else {
                logger.warn("Failed to delete user with ID: {}", id);
            }

            return success;
        } catch (SQLException e) {
            logger.error("SQL Error while deleting user: {}", e.getMessage(), e);
            return false;
        }
    }
}