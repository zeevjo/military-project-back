package org.example.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.services.LoginService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);
    private final LoginService loginService = new LoginService();
    private final Gson gson = new Gson();

    public void login(HttpExchange exchange) throws IOException {
        logger.info("Received login request");

        try {
            String requestBody = readRequestBody(exchange);
            logger.debug("Request body: {}", requestBody);

            Map<String, String> requestMap = gson.fromJson(requestBody, Map.class);

            String username = requestMap.get("username");
            String password = requestMap.get("password");

            String result = loginService.login(username, password);

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", result);

            sendResponse(exchange, 200, gson.toJson(responseMap));
        } catch (IllegalArgumentException e) {
            logger.error("Invalid login request: {}", e.getMessage());
            sendResponse(exchange, 400, gson.toJson(Map.of("error", e.getMessage())));
        } catch (Exception e) {
            logger.error("Error processing login request: {}", e.getMessage(), e);
            sendResponse(exchange, 500, gson.toJson(Map.of("error", "Internal server error")));
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
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

}
