package org.example.router;

import com.sun.net.httpserver.HttpServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.controllers.UserController;
import org.example.interfaces.HttpHandlerWithMethod;
import org.example.utils.Properties;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, Map<String, HttpHandlerWithMethod>> routes = new HashMap<>();
    private static final String SECRET_KEY = Properties.get("SECRET_KEY");
    private static final Logger logger = LogManager.getLogger(Router.class);

    public void get(String path, HttpHandlerWithMethod handler) {
        register("GET", path, handler);
        logger.info("GET route registered for path: {}", path);
    }

    public void post(String path, HttpHandlerWithMethod handler) {
        register("POST", path, handler);
        logger.info("POST route registered for path: {}", path);
    }

    public void put(String path, HttpHandlerWithMethod handler) {
        register("PUT", path, handler);
        logger.info("PUT route registered for path: {}", path);
    }

    public void delete(String path, HttpHandlerWithMethod handler) {
        register("DELETE", path, handler);
        logger.info("DELETE route registered for path: {}", path);
    }

    private void register(String method, String path, HttpHandlerWithMethod handler) {
        routes.computeIfAbsent(path, k -> new HashMap<>()).put(method, handler);
    }


    public void wrap(HttpServer server) {
        routes.forEach((path, methods) -> server.createContext(path, exchange -> {
            String method = exchange.getRequestMethod();
            logger.info("Received {} request for path: {}", method, path);

            try {
                // Handle OPTIONS requests for CORS
                if ("OPTIONS".equalsIgnoreCase(method)) {
                    logger.debug("Handling OPTIONS request for path: {}", path);
                    setCorsHeaders(exchange);
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                // Set CORS headers once for all other requests
                setCorsHeaders(exchange);

                // Skip validation for login endpoint
                if (!"/api/login".equalsIgnoreCase(path)) {
                    String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");
                    String apiKeyHeader = exchange.getRequestHeaders().getFirst("x-api-key");

                    if (authorizationHeader == null && apiKeyHeader == null) {
                        logger.warn("Request missing both Authorization token and API key for path: {}", path);
                        sendCustomResponse(exchange, 400, "{\"error\": \"Missing Authorization token or API key\"}");
                        return;
                    }

                    if (authorizationHeader != null && !validateJwt(authorizationHeader)) {
                        logger.warn("Invalid Authorization token for path: {}", path);
                        sendCustomResponse(exchange, 401, "{\"error\": \"Invalid Authorization token\"}");
                        return;
                    }

                    if (apiKeyHeader != null && !validateJwt(apiKeyHeader)) {
                        logger.warn("Invalid API key for path: {}", path);
                        sendCustomResponse(exchange, 401, "{\"error\": \"Invalid API key\"}");
                        return;
                    }

                    logger.info("Authorization validated successfully for path: {}", path);
                }

                HttpHandlerWithMethod handler = methods.get(method);
                if (handler != null) {
                    logger.info("Handler found for {} request on path: {}", method, path);
                    handler.handle(exchange);
                } else {
                    logger.warn("No handler found for {} request on path: {}", method, path);
                    exchange.sendResponseHeaders(405, -1);
                    exchange.close();
                }

            } catch (Exception e) {
                logger.error("Error handling request for path: {} - {}", path, e.getMessage());
                sendCustomResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
            }
        }));
    }

    private void setCorsHeaders(com.sun.net.httpserver.HttpExchange exchange) {
        // Set each header only once
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, x-api-key");
        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
    }


    private boolean validateJwt(String header) {
        try {
            if (header == null || (!header.startsWith("Bearer ") && !header.startsWith("eyJ"))) {
                return false;
            }

            String token = header.startsWith("Bearer ") ? header.substring(7) : header;

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims != null;
        } catch (Exception e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    private void sendCustomResponse(com.sun.net.httpserver.HttpExchange exchange, int statusCode, String message) {
        try {
            byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
            exchange.getResponseBody().close();
            logger.info("Sent response with status {} and message: {}", statusCode, message);
        } catch (Exception e) {
            logger.error("Error sending response: {}", e.getMessage());
        }
    }
}

//---------


//package org.example.router;
//
//import com.sun.net.httpserver.HttpServer;
//import org.example.interfaces.HttpHandlerWithMethod;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Router {
//    private final Map<String, Map<String, HttpHandlerWithMethod>> routes = new HashMap<>();
//
//    public void get(String path, HttpHandlerWithMethod handler) {
//        register("GET", path, handler);
//    }
//
//    public void post(String path, HttpHandlerWithMethod handler) {
//        register("POST", path, handler);
//    }
//
//    public void put(String path, HttpHandlerWithMethod handler) {
//        register("PUT", path, handler);
//    }
//
//    public void delete(String path, HttpHandlerWithMethod handler) {
//        register("DELETE", path, handler);
//    }
//
//    private void register(String method, String path, HttpHandlerWithMethod handler) {
//        routes.computeIfAbsent(path, k -> new HashMap<>()).put(method, handler);
//    }
//
//    public void wrap(HttpServer server) {
//        routes.forEach((path, methods) -> server.createContext(path, exchange -> {
//            String method = exchange.getRequestMethod();
//            if ("OPTIONS".equalsIgnoreCase(method)) {
//                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
//                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
//                exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
//                exchange.sendResponseHeaders(204, -1); // No content
//            } else {
//                HttpHandlerWithMethod handler = methods.get(method);
//                if (handler != null) {
//                    handler.handle(exchange);
//                } else {
//                    exchange.sendResponseHeaders(405, -1); // Method not allowed
//                }
//            }
//        }));
//    }
//}

//package org.example.router;
//
//import com.sun.net.httpserver.HttpServer;
//import com.sun.net.httpserver.HttpExchange;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.example.interfaces.HttpHandlerWithMethod;
//import org.example.utils.Properties;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Router {
//    private static final Logger logger = LogManager.getLogger(Router.class);
//    private final Map<String, Map<String, HttpHandlerWithMethod>> routes = new HashMap<>();
//
//    // Use the same secure key as LoginService
//    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(Properties.get("SECRET_KEY").getBytes(StandardCharsets.UTF_8));
//
//    public void get(String path, HttpHandlerWithMethod handler) {
//        register("GET", path, handler);
//        logger.info("GET route registered for path: {}", path);
//    }
//
//    public void post(String path, HttpHandlerWithMethod handler) {
//        register("POST", path, handler);
//        logger.info("POST route registered for path: {}", path);
//    }
//
//    public void put(String path, HttpHandlerWithMethod handler) {
//        register("PUT", path, handler);
//        logger.info("PUT route registered for path: {}", path);
//    }
//
//    public void delete(String path, HttpHandlerWithMethod handler) {
//        register("DELETE", path, handler);
//        logger.info("DELETE route registered for path: {}", path);
//    }
//
//    private void register(String method, String path, HttpHandlerWithMethod handler) {
//        routes.computeIfAbsent(path, k -> new HashMap<>()).put(method, handler);
//    }
//
//    public void wrap(HttpServer server) {
//        routes.forEach((path, methods) -> server.createContext(path, exchange -> {
//            String method = exchange.getRequestMethod();
//            logger.info("Received {} request for path: {}", method, path);
//
//            try {
//                if ("OPTIONS".equalsIgnoreCase(method)) {
//                    setCorsHeaders(exchange);
//                    exchange.sendResponseHeaders(204, -1); // No content
//                    exchange.close();
//                    return;
//                }
//
//                setCorsHeaders(exchange);
//
//                // Skip validation for /api/login
//                if (!"/api/login".equals(path)) {
//                    String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
//
//                    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                        logger.warn("Missing or invalid Authorization header for path: {}", path);
//                        sendCustomResponse(exchange, 401, "{\"error\": \"Unauthorized\"}");
//                        return;
//                    }
//
//                    if (!validateJwtToken(authHeader)) {
//                        logger.warn("JWT validation failed for path: {}", path);
//                        sendCustomResponse(exchange, 401, "{\"error\": \"Invalid JWT\"}");
//                        return;
//                    }
//
//                    logger.info("JWT validated successfully for path: {}", path);
//                }
//
//                HttpHandlerWithMethod handler = methods.get(method);
//                if (handler != null) {
//                    handler.handle(exchange);
//                } else {
//                    logger.warn("No handler found for method {} on path: {}", method, path);
//                    exchange.sendResponseHeaders(405, -1); // Method not allowed
//                }
//            } catch (Exception e) {
//                logger.error("Error processing request for path: {} - {}", path, e.getMessage());
//                sendCustomResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
//            }
//        }));
//    }
//
//    private boolean validateJwtToken(String authHeader) {
//        try {
//            String token = authHeader.substring(7); // Remove "Bearer " prefix
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            return claims != null;
//        } catch (Exception e) {
//            logger.warn("JWT validation error: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    private void setCorsHeaders(HttpExchange exchange) {
//        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
//    }
//
//    private void sendCustomResponse(HttpExchange exchange, int statusCode, String message) {
//        try {
//            byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
//            exchange.getResponseHeaders().add("Content-Type", "application/json");
//            exchange.sendResponseHeaders(statusCode, responseBytes.length);
//            exchange.getResponseBody().write(responseBytes);
//            exchange.getResponseBody().close();
//        } catch (Exception e) {
//            logger.error("Error sending response: {}", e.getMessage());
//        }
//    }
//}
