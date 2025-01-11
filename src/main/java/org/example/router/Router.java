package org.example.router;

import com.sun.net.httpserver.HttpServer;
import org.example.interfaces.HttpHandlerWithMethod;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private final Map<String, Map<String, HttpHandlerWithMethod>> routes = new HashMap<>();

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

//    public void wrap(HttpServer server) {
//        routes.forEach((path, methods) -> server.createContext(path, exchange -> {
//            String method = exchange.getRequestMethod();
//            HttpHandlerWithMethod handler = methods.get(method);
//            if (handler != null) {
//                handler.handle(exchange);
//            } else {
//                exchange.sendResponseHeaders(405, -1);
//            }
//        }));
//    }

public void wrap(HttpServer server) {
    routes.forEach((path, methods) -> server.createContext(path, exchange -> {
        String method = exchange.getRequestMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.getResponseHeaders().add("Access-Control-Allow-Credentials", "true");
            exchange.sendResponseHeaders(204, -1); // No content
        } else {
            HttpHandlerWithMethod handler = methods.get(method);
            if (handler != null) {
                handler.handle(exchange);
            } else {
                exchange.sendResponseHeaders(405, -1); // Method not allowed
            }
        }
    }));
}

}
