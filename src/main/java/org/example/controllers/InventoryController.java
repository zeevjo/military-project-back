package org.example.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.services.InventoryService;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class InventoryController {
    private static final Logger logger = LogManager.getLogger(InventoryController.class);
    private final InventoryService inventoryService = new InventoryService();
    private final Gson gson = new Gson();

    public void getAllInventory(HttpExchange exchange) throws IOException {
        logger.info("Received request to get all inventory items");
        try {
            String response = inventoryService.getAllInventory();
            logger.info("Successfully fetched all inventory items, responding with status 200");
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            logger.error("Error while fetching all inventory items: {}", e.getMessage(), e);
            sendError(exchange, 500, e.getMessage());
        }
    }

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

    public void getItemDetails(HttpExchange exchange) throws IOException {
        logger.info("Received request to fetch item details");
        try {
            Map<String, String> queryParams = parseQuery(exchange.getRequestURI().getQuery());
            int stockId = Integer.parseInt(queryParams.get("id"));
            logger.info("Fetching details for stock ID: {}", stockId);

            String response = inventoryService.getItemDetailsByStockId(stockId);
            sendResponse(exchange, 200, response);
        } catch (NumberFormatException e) {
            logger.error("Invalid stock ID provided in request", e);
            sendError(exchange, 400, "Invalid stock ID provided");
        } catch (Exception e) {
            logger.error("Error while fetching item details: {}", e.getMessage(), e);
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
}