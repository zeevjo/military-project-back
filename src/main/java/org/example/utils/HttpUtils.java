package org.example.utils;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpUtils {

    private static final Logger logger = LogManager.getLogger(HttpUtils.class);

    public static void sendResponse(Logger logger, HttpExchange exchange, int statusCode, String response, Map<String, String> headers) throws IOException {
        logger.info("Sending response with status {} and body: {}", statusCode, response);

        if (headers != null) {
            headers.forEach((key, value) -> exchange.getResponseHeaders().set(key, value));
        }

        if (!exchange.getResponseHeaders().containsKey("Content-Type")) {
            exchange.getResponseHeaders().set("Content-Type", "application/json");
        }

        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        logger.info("Response sent successfully");
    }

    public static String readRequestBody(HttpExchange exchange) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }

    public static Map<String, String> parseQuery(Logger logger, String query) {
        logger.debug("Parsing query: {}", query);

        Map<String, String> queryPairs = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2); // split into at most 2 parts
                String key = keyValue[0];
                String value = keyValue.length > 1 ? keyValue[1] : "";
                queryPairs.put(key, value);
            }
        }

        logger.debug("Parsed query parameters: {}", queryPairs);
        return queryPairs;
    }
}
