package org.example.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.services.LoginService;
import org.example.utils.HttpUtils;

import java.io.IOException;
import java.util.Map;

public class LoginController {

    private static final Logger logger = LogManager.getLogger(LoginController.class);
    private final LoginService loginService = new LoginService();
    private final Gson gson = new Gson();

    public void login(HttpExchange exchange) throws IOException {
        logger.info("Received login request");

        try {
            String requestBody = HttpUtils.readRequestBody(exchange);
            logger.debug("Request body: {}", requestBody);

            Map<String, String> requestMap = gson.fromJson(requestBody, Map.class);
            
            String username = requestMap.get("username");
            String password = requestMap.get("password");
            
            if (username == null || username.isEmpty()) {
                logger.warn("Validation failed: Username is missing or empty");
                HttpUtils.sendResponse(logger, exchange, 400, gson.toJson(Map.of("error", "Username is required")), null);
                return;
            }
            
            if (password == null || password.isEmpty()) {
                logger.warn("Validation failed: Password is missing or empty");
                HttpUtils.sendResponse(logger, exchange, 400, gson.toJson(Map.of("error", "Password is required")), null);
                return;
            }


            Map<String, Object> result = loginService.login(username, password);

            if (!(boolean) result.get("success")) {
                logger.warn("Invalid credentials for user: {}", username);
                HttpUtils.sendResponse(logger, exchange, 401, gson.toJson(Map.of("error", "Invalid username or password")), null);
                return;
            }

            HttpUtils.sendResponse(logger, exchange, 200, gson.toJson(result), null);
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage(), e);
            HttpUtils.sendResponse(logger, exchange, 500, gson.toJson(Map.of("error", "Internal server error")), null);
        }
    }
}
