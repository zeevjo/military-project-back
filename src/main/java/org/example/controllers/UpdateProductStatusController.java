package org.example.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.services.UpdateProductStatusService;
import org.example.utils.HttpUtils;

import java.io.IOException;
import java.util.Map;

public class UpdateProductStatusController {

    private static final Logger logger = LogManager.getLogger(UpdateProductStatusController.class);
    private final UpdateProductStatusService updateProductStatusService = new UpdateProductStatusService();
    private final Gson gson = new Gson();

    /**
     * Handles HTTP requests to update a product's status by calling the update_product_status stored procedure.
     */
    public void updateStatus(HttpExchange exchange) throws IOException {
        logger.info("Received request to update product status");

        try {
            String requestBody = HttpUtils.readRequestBody(exchange);
            logger.debug("Request body: {}", requestBody);

            Map<String, Double> requestMap = gson.fromJson(requestBody, Map.class);

            if (!requestMap.containsKey("productStockId")
                    || !requestMap.containsKey("newStatusId")
                    || !requestMap.containsKey("changedBy")) {
                logger.warn("Validation failed: Missing required fields");
                HttpUtils.sendResponse(
                        logger,
                        exchange,
                        400,
                        gson.toJson(Map.of("error", "Missing required fields: productStockId, newStatusId, changedBy")),
                        null
                );
                return;
            }

            int productStockId = requestMap.get("productStockId").intValue();
            int newStatusId    = requestMap.get("newStatusId").intValue();
            int changedBy      = requestMap.get("changedBy").intValue();

            Map<String, Object> serviceResult = updateProductStatusService.updateStatus(productStockId, newStatusId, changedBy);

            if ((boolean) serviceResult.get("success")) {
                HttpUtils.sendResponse(logger, exchange, 200, gson.toJson(serviceResult), null);
            } else {
                HttpUtils.sendResponse(logger, exchange, 400, gson.toJson(serviceResult), null);
            }

        } catch (Exception e) {
            logger.error("Unexpected error while updating product status: {}", e.getMessage(), e);
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
