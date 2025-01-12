package org.example.router;

import com.sun.net.httpserver.HttpServer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.interfaces.HttpHandlerWithMethod;
import org.example.utils.Properties;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, Map<String, HttpHandlerWithMethod>> routes = new HashMap<>();
    private static final String SECRET_KEY = Properties.get("SECRET_KEY");

    public void get(String path, HttpHandlerWithMethod handler) {
        register("GET", path, handler);
    }

    public void post(String path, HttpHandlerWithMethod handler) {
        register("POST", path, handler);
    }

    public void put(String path, HttpHandlerWithMethod handler) {
        register("PUT", path, handler);
    }

    public void delete(String path, HttpHandlerWithMethod handler) {
        register("DELETE", path, handler);
    }

    private void register(String method, String path, HttpHandlerWithMethod handler) {
        routes.computeIfAbsent(path, k -> new HashMap<>()).put(method, handler);
    }

    public void wrap(HttpServer server) {
        routes.forEach((path, methods) -> server.createContext(path, exchange -> {
            String method = exchange.getRequestMethod();

            // Skip validation for login endpoint
            if (!"/api/login".equalsIgnoreCase(path)) {
                if (!validateJwt(exchange.getRequestHeaders().getFirst("Authorization"))) {
                    exchange.sendResponseHeaders(401, -1); // Unauthorized
                    return;
                }
            }

            if ("OPTIONS".equalsIgnoreCase(method)) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
                exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
                exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
                exchange.sendResponseHeaders(204, -1);
            } else {
                HttpHandlerWithMethod handler = methods.get(method);
                if (handler != null) {
                    handler.handle(exchange);
                } else {
                    exchange.sendResponseHeaders(405, -1);
                }
            }
        }));
    }

    private boolean validateJwt(String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return false;
            }

            String token = authorizationHeader.substring(7);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims != null;
        } catch (Exception e) {
            return false;
        }
    }
}