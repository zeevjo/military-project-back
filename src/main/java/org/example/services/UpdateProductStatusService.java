package org.example.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.repositories.UpdateProductStatusRepository;

import java.util.HashMap;
import java.util.Map;

public class UpdateProductStatusService {

    private static final Logger logger = LogManager.getLogger(UpdateProductStatusService.class);
    private final UpdateProductStatusRepository updateProductStatusRepository = new UpdateProductStatusRepository();

    public Map<String, Object> updateStatus(int productStockId, int newStatusId, int changedBy) {
        logger.info("Processing update status request for ProductStockId: {}, NewStatusId: {}, ChangedBy: {}",
                productStockId, newStatusId, changedBy);

        Map<String, Object> response = new HashMap<>();

        try {
            updateProductStatusRepository.updateProductStatus(productStockId, newStatusId, changedBy);

            logger.info("Product status updated successfully for ID: {}", productStockId);
            response.put("success", true);
            response.put("message", "Product status updated successfully");
        } catch (Exception e) {
            logger.error("Error updating product status for ID {}: {}", productStockId, e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to update product status");
        }

        return response;
    }
}
