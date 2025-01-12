//package org.example.router;
//
//import com.sun.net.httpserver.HttpServer;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.example.controllers.UserController;
//import org.example.interfaces.HttpHandlerWithMethod;
//import org.example.utils.Properties;
//
//import java.nio.charset.StandardCharsets;
//import java.util.HashMap;
//import java.util.Map;
//
//public class Router {
//    private final Map<String, Map<String, HttpHandlerWithMethod>> routes = new HashMap<>();
//    private static final String SECRET_KEY = Properties.get("SECRET_KEY");
//    private static final Logger logger = LogManager.getLogger(Router.class);
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
////    public void wrap(HttpServer server) {
////        routes.forEach((path, methods) -> server.createContext(path, exchange -> {
////            String method = exchange.getRequestMethod();
////            logger.info("Received {} request for path: {}", method, path);
////
////            try {
////                // Skip validation for login endpoint
////                if (!"/api/login".equalsIgnoreCase(path)) {
////                    String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");
////                    String apiKeyHeader = exchange.getRequestHeaders().getFirst("x-api-key");
////
////                    if (authorizationHeader == null && apiKeyHeader == null) {
////                        logger.warn("Request missing both Authorization token and API key for path: {}", path);
////                        sendCustomResponse(exchange, 400, "{\"error\": \"Missing Authorization token or API key\"}");
////                        return;
////                    }
////
////                    if (authorizationHeader != null && !validateJwt(authorizationHeader)) {
////                        logger.warn("Invalid Authorization token for path: {}", path);
////                        sendCustomResponse(exchange, 401, "{\"error\": \"Invalid Authorization token\"}");
////                        return;
////                    }
////
////                    if (apiKeyHeader != null && !validateJwt(apiKeyHeader)) {
////                        logger.warn("Invalid API key for path: {}", path);
////                        sendCustomResponse(exchange, 401, "{\"error\": \"Invalid API key\"}");
////                        return;
////                    }
////
////                    logger.info("Authorization validated successfully for path: {}", path);
////                }
////
////                if ("OPTIONS".equalsIgnoreCase(method)) {
////                    logger.debug("Handling OPTIONS request for path: {}", path);
////                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*"); // Allow all origins (or specify your origin)
////                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
////                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, x-api-key");
////                    exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
////                    exchange.sendResponseHeaders(204, -1);
////                } else {
////                    // Add the same CORS headers to actual requests
////                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
////                    HttpHandlerWithMethod handler = methods.get(method);
////                    if (handler != null) {
////                        logger.info("Handler found for {} request on path: {}", method, path);
////                        handler.handle(exchange);
////                    } else {
////                        logger.warn("No handler found for {} request on path: {}", method, path);
////                        exchange.sendResponseHeaders(405, -1);
////                    }
////                }
////
////            } catch (Exception e) {
////                logger.error("Error handling request for path: {} - {}", path, e.getMessage());
////                sendCustomResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
////            }
////        }));
////    }
//public void wrap(HttpServer server) {
//    routes.forEach((path, methods) -> server.createContext(path, exchange -> {
//        String method = exchange.getRequestMethod();
//        logger.info("Received {} request for path: {}", method, path);
//
//        try {
//            // Handle OPTIONS requests for CORS
//            if ("OPTIONS".equalsIgnoreCase(method)) {
//                logger.debug("Handling OPTIONS request for path: {}", path);
//                setCorsHeaders(exchange);
//                exchange.sendResponseHeaders(204, -1);
//                exchange.close();
//                return;
//            }
//
//            // Set CORS headers once for all other requests
//            setCorsHeaders(exchange);
//
//            // Skip validation for login endpoint
//            if (!"/api/login".equalsIgnoreCase(path)) {
//                String authorizationHeader = exchange.getRequestHeaders().getFirst("Authorization");
//                String apiKeyHeader = exchange.getRequestHeaders().getFirst("x-api-key");
//
//                if (authorizationHeader == null && apiKeyHeader == null) {
//                    logger.warn("Request missing both Authorization token and API key for path: {}", path);
//                    sendCustomResponse(exchange, 400, "{\"error\": \"Missing Authorization token or API key\"}");
//                    return;
//                }
//
//                if (authorizationHeader != null && !validateJwt(authorizationHeader)) {
//                    logger.warn("Invalid Authorization token for path: {}", path);
//                    sendCustomResponse(exchange, 401, "{\"error\": \"Invalid Authorization token\"}");
//                    return;
//                }
//
//                if (apiKeyHeader != null && !validateJwt(apiKeyHeader)) {
//                    logger.warn("Invalid API key for path: {}", path);
//                    sendCustomResponse(exchange, 401, "{\"error\": \"Invalid API key\"}");
//                    return;
//                }
//
//                logger.info("Authorization validated successfully for path: {}", path);
//            }
//
//            HttpHandlerWithMethod handler = methods.get(method);
//            if (handler != null) {
//                logger.info("Handler found for {} request on path: {}", method, path);
//                handler.handle(exchange);
//            } else {
//                logger.warn("No handler found for {} request on path: {}", method, path);
//                exchange.sendResponseHeaders(405, -1);
//                exchange.close();
//            }
//
//        } catch (Exception e) {
//            logger.error("Error handling request for path: {} - {}", path, e.getMessage());
//            sendCustomResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
//        }
//    }));
//}
//
//    private void setCorsHeaders(com.sun.net.httpserver.HttpExchange exchange) {
//        // Set each header only once
//        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, x-api-key");
//        exchange.getResponseHeaders().set("Access-Control-Allow-Credentials", "true");
//    }
//
//
//    private boolean validateJwt(String header) {
//        try {
//            if (header == null || (!header.startsWith("Bearer ") && !header.startsWith("eyJ"))) {
//                return false;
//            }
//
//            String token = header.startsWith("Bearer ") ? header.substring(7) : header;
//
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            return claims != null;
//        } catch (Exception e) {
//            logger.warn("JWT validation failed: {}", e.getMessage());
//            return false;
//        }
//    }
//
//    private void sendCustomResponse(com.sun.net.httpserver.HttpExchange exchange, int statusCode, String message) {
//        try {
//            byte[] responseBytes = message.getBytes(StandardCharsets.UTF_8);
//            exchange.getResponseHeaders().add("Content-Type", "application/json");
//            exchange.sendResponseHeaders(statusCode, responseBytes.length);
//            exchange.getResponseBody().write(responseBytes);
//            exchange.getResponseBody().close();
//            logger.info("Sent response with status {} and message: {}", statusCode, message);
//        } catch (Exception e) {
//            logger.error("Error sending response: {}", e.getMessage());
//        }
//    }
//}
package org.example.router;

import com.sun.net.httpserver.HttpServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
                // Clear existing CORS headers first
                exchange.getResponseHeaders().clear();

                // Set CORS headers
                setCorsHeaders(exchange);

                // Handle OPTIONS request
                if ("OPTIONS".equalsIgnoreCase(method)) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                // Handle authentication for non-login endpoints
                if (!"/api/login".equalsIgnoreCase(path)) {
                    String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
                    if (authHeader == null || !validateJwt(authHeader)) {
                        sendCustomResponse(exchange, 401, "{\"error\": \"Invalid or missing authorization\"}");
                        return;
                    }
                }

                // Handle the actual request
                HttpHandlerWithMethod handler = methods.get(method);
                if (handler != null) {
                    handler.handle(exchange);
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }

            } catch (Exception e) {
                logger.error("Error handling request: {}", e.getMessage());
                sendCustomResponse(exchange, 500, "{\"error\": \"Internal Server Error\"}");
            } finally {
                exchange.close();
            }
        }));
    }

    private void setCorsHeaders(com.sun.net.httpserver.HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization, x-api-key");
        exchange.getResponseHeaders().set("Access-Control-Max-Age", "3600");
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
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
        } catch (Exception e) {
            logger.error("Error sending response: {}", e.getMessage());
        }
    }
}