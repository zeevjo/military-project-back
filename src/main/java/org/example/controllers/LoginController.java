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

            if (username == null || username.isEmpty()) {
                logger.warn("Validation failed: Username is missing or empty");
                sendResponse(exchange, 400, gson.toJson(Map.of("error", "Username is required")));
                return;
            }
            if (password == null || password.isEmpty()) {
                logger.warn("Validation failed: Password is missing or empty");
                sendResponse(exchange, 400, gson.toJson(Map.of("error", "Password is required")));
                return;
            }

            Map<String, Object> result = loginService.login(username, password);

            sendResponse(exchange, 200, gson.toJson(result));
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage(), e);
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
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        logger.info("Response sent successfully");
    }

}
