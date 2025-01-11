package org.example.services;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entities.UserEntity;
import org.example.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserRepository userRepository = new UserRepository();
    private final Gson gson = new Gson();

    public String getAllUsers() {
        logger.info("Starting process to fetch all users from the database.");
        List<UserEntity> users = userRepository.findAll();
        logger.info("Successfully fetched {} user(s).", users.size());
        return gson.toJson(users);
    }

    public String addUser(String requestBody) {
        logger.info("Received request to add a new user. Parsing request body.");
        UserEntity user = gson.fromJson(requestBody, UserEntity.class);
        logger.debug("Parsed UserEntity for addition: {}", user);
        boolean isAdded = userRepository.save(user);

        if (isAdded) {
            logger.info("User '{}' added successfully with username: {}", user.getId(), user.getUsername());
        } else {
            logger.warn("Failed to add user with username: {}", user.getUsername());
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", isAdded ? "User added successfully" : "Failed to add user");
        return gson.toJson(response);
    }

    public String updateUser(String requestBody) {
        logger.info("Received request to update user. Parsing request body.");
        UserEntity user = gson.fromJson(requestBody, UserEntity.class);
        logger.debug("Parsed UserEntity for update: {}", user);
        boolean isUpdated = userRepository.update(user);

        if (isUpdated) {
            logger.info("User '{}' updated successfully. New username: {}", user.getId(), user.getUsername());
        } else {
            logger.warn("Failed to update user with ID: {}", user.getId());
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", isUpdated ? "User updated successfully" : "Failed to update user");
        return gson.toJson(response);
    }

    public String deleteUser(String id) {
        logger.info("Received request to delete user with ID: {}", id);
        boolean isDeleted = userRepository.delete(Integer.parseInt(id));

        if (isDeleted) {
            logger.info("User with ID '{}' deleted successfully.", id);
        } else {
            logger.warn("Failed to delete user with ID: {}", id);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", isDeleted ? "User deleted successfully" : "Failed to delete user");
        return gson.toJson(response);
    }
}
