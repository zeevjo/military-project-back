package org.example.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entities.ProductFullTableEntity;
import org.example.services.ProductFullTableService;
import org.example.utils.HttpUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProductFullTableController {

    private static final Logger logger = LogManager.getLogger(ProductFullTableController.class);
    private final ProductFullTableService productFullTableService = new ProductFullTableService();
    private final Gson gson = new Gson();

    public void getProductFullTableByName(HttpExchange exchange) throws IOException {
        logger.info("Received request for product details by name");

        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                logger.warn("No query parameters provided");
                HttpUtils.sendResponse(
                        logger,
                        exchange,
                        400,
                        gson.toJson(Map.of("error", "productName query param is required")),
                        null
                );
                return;
            }

            Map<String, String> queryParams = HttpUtils.parseQuery(logger, query);
            String productName = queryParams.get("productName");

            if (productName == null || productName.isEmpty()) {
                logger.warn("productName is missing or empty");
                HttpUtils.sendResponse(
                        logger,
                        exchange,
                        400,
                        gson.toJson(Map.of("error", "productName is required")),
                        null
                );
                return;
            }

            List<ProductFullTableEntity> productDetails = productFullTableService.getProductFullTableByName(productName);

            String jsonResponse = gson.toJson(productDetails);
            logger.debug("Returning JSON response: {}", jsonResponse);

            HttpUtils.sendResponse(logger, exchange, 200, jsonResponse, null);

        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage(), e);
            HttpUtils.sendResponse(
                    logger,
                    exchange,
                    500,
                    gson.toJson(Map.of("error", "Internal server error")),
                    null
            );
        }
    }
}
