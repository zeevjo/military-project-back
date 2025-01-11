package org.example.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.services.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    public void getAllUsers(HttpExchange exchange) throws IOException {
        logger.info("Received request to get all users");
        try {
            String response = userService.getAllUsers();
            logger.info("Successfully fetched all users, responding with status 200");
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            logger.error("Error while fetching all users: {}", e.getMessage(), e);
            sendError(exchange, 500, e.getMessage());
        }
    }

    public void addUser(HttpExchange exchange) throws IOException {
        logger.info("Received request to add a user");
        try {
            String requestBody = readRequestBody(exchange);
            logger.debug("Request body for addUser: {}", requestBody);
            String response = userService.addUser(requestBody);
            logger.info("User added successfully, responding with status 201");
            sendResponse(exchange, 201, response);
        } catch (Exception e) {
            logger.error("Error while adding user: {}", e.getMessage(), e);
            sendError(exchange, 500, e.getMessage());
        }
    }

    public void updateUser(HttpExchange exchange) throws IOException {
        logger.info("Received request to update a user");
        try {
            String requestBody = readRequestBody(exchange);
            logger.debug("Request body for updateUser: {}", requestBody);
            String response = userService.updateUser(requestBody);
            logger.info("User updated successfully, responding with status 200");
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            logger.error("Error while updating user: {}", e.getMessage(), e);
            sendError(exchange, 500, e.getMessage());
        }
    }

    public void deleteUser(HttpExchange exchange) throws IOException {
        logger.info("Received request to delete a user");
        try {
            String query = exchange.getRequestURI().getQuery();
            logger.debug("Query parameters for deleteUser: {}", query);
            Map<String, String> params = parseQuery(query);

            if (params.containsKey("id")) {
                String response = userService.deleteUser(params.get("id"));
                logger.info("User deleted successfully, responding with status 200");
                sendResponse(exchange, 200, response);
            } else {
                logger.warn("Missing 'id' parameter in deleteUser request");
                sendError(exchange, 400, "Missing 'id' parameter");
            }

        } catch (Exception e) {
            logger.error("Error while deleting user: {}", e.getMessage(), e);
            sendError(exchange, 500, e.getMessage());
        }
    }

    public void test(HttpExchange exchange) throws IOException {
        logger.info("Received request for test endpoint");
        try {
            String query = exchange.getRequestURI().getQuery();
            logger.debug("Query parameters for test: {}", query);

            Map<String, String> queryParams = parseQuery(query);

            String id = queryParams.getOrDefault("id", "defaultId");
            String param = queryParams.getOrDefault("param", "defaultParam");

            String responseMessage = String.format("Received id=%s and param=%s", id, param);
            String jsonString = gson.toJson(responseMessage);

            logger.info("Test endpoint successfully processed, responding with status 200");
            sendResponse(exchange, 200, jsonString);
        } catch (Exception e) {
            logger.error("Error in test endpoint: {}", e.getMessage(), e);
            sendError(exchange, 500, e.getMessage());
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        logger.debug("Reading request body");
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        logger.debug("Request body read successfully");
        return requestBody.toString();
    }

    private Map<String, String> parseQuery(String query) {
        logger.debug("Parsing query: {}", query);
        Map<String, String> queryPairs = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2); // Ensure split works even if '=' is missing
                queryPairs.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
            }
        }
        logger.debug("Parsed query parameters: {}", queryPairs);
        return queryPairs;
    }

//    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
//        logger.info("Sending response with status {} and body: {}", statusCode, response);
//        exchange.getResponseHeaders().set("Content-Type", "application/json");
//        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
//        try (OutputStream os = exchange.getResponseBody()) {
//            os.write(response.getBytes(StandardCharsets.UTF_8));
//        }
//        logger.info("Response sent successfully");
//    }
private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
    logger.info("Sending response with status {} and body: {}", statusCode, response);

    String requestMethod = exchange.getRequestMethod();
    if ("OPTIONS".equalsIgnoreCase(requestMethod)) {
        logger.info("Handling preflight OPTIONS request");
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
        exchange.sendResponseHeaders(204, -1); // No content for preflight response
        return;
    }


    // Add CORS headers for actual responses
    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
    exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");

    // Set content type
    exchange.getResponseHeaders().set("Content-Type", "application/json");

    // Send response
    exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
    try (OutputStream os = exchange.getResponseBody()) {
        os.write(response.getBytes(StandardCharsets.UTF_8));
    }
    logger.info("Response sent successfully");
}


    private void sendError(HttpExchange exchange, int statusCode, String errorMessage) throws IOException {
        logger.error("Sending error response with status {} and message: {}", statusCode, errorMessage);
        Map<String, String> errorResponseMap = new HashMap<>();
        errorResponseMap.put("error", errorMessage);
        String errorResponse = gson.toJson(errorResponseMap);
        sendResponse(exchange, statusCode, errorResponse);
    }
}