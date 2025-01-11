package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticFileHandler implements HttpHandler {
    private final Path rootPath;

    public StaticFileHandler(String rootPath) {
        this.rootPath = Paths.get(rootPath).toAbsolutePath().normalize();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestPath = exchange.getRequestURI().getPath();

        // If the root path is requested, serve index.html
        if (requestPath.equals("/")) {
            requestPath = "/index.html";
        }

        Path filePath = rootPath.resolve(requestPath.substring(1)).normalize();

        // Check if the resolved path is still within the root path
        if (!filePath.startsWith(rootPath)) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        if (!Files.exists(filePath)) {
            sendError(exchange, 404, "Not Found");
            return;
        }

        String contentType = Files.probeContentType(filePath);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, Files.size(filePath));
        try (OutputStream os = exchange.getResponseBody()) {
            Files.copy(filePath, os);
        }
    }

    private void sendError(HttpExchange exchange, int code, String message) throws IOException {
        String response = code + " (" + message + ")\n";
        exchange.sendResponseHeaders(code, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}