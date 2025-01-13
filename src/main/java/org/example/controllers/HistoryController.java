package org.example.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.services.HistoryService;
import org.example.utils.HttpUtils;

import java.io.IOException;
import java.util.Map;

public class HistoryController {

    private static final Logger logger = LogManager.getLogger(HistoryController.class);
    private final HistoryService historyService = new HistoryService();
    private final Gson gson = new Gson();

    public void getHistory(HttpExchange exchange) throws IOException {
        logger.info("Received request to retrieve product stock history");

        try {
            String jsonResponse = historyService.getHistory();

            HttpUtils.sendResponse(logger, exchange, 200, jsonResponse, null);

        } catch (Exception e) {
            logger.error("Unexpected error retrieving history: {}", e.getMessage(), e);
            HttpUtils.sendResponse(logger, exchange, 500, gson.toJson(
                    Map.of("error", "Internal server error")), null);
        }
    }
}
