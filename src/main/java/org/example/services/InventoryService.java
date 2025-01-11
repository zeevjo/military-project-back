package org.example.services;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entities.InventoryEntity;
import org.example.entities.ItemEntity;
import org.example.repositories.InventoryRepository;

import java.util.List;
import java.util.Map;


public class InventoryService {
    private static final Logger logger = LogManager.getLogger(InventoryService.class);
    private final InventoryRepository inventoryRepository = new InventoryRepository();
    private final Gson gson = new Gson();

    public String getAllInventory() {
        logger.info("Starting process to fetch all inventory items from the database.");
        List<InventoryEntity> inventoryList = inventoryRepository.findAll();
        logger.info("Successfully fetched {} inventory item(s).", inventoryList.size());
        return gson.toJson(inventoryList);
    }

    public String getItemDetailsByStockId(int stockId) {
        logger.info("Fetching item details from repository for stock ID: {}", stockId);
        ItemEntity item = inventoryRepository.findItemDetailsByStockId(stockId);

        if (item != null) {
            logger.info("Successfully fetched item details for stock ID: {}", stockId);
            return gson.toJson(item);
        } else {
            logger.warn("No item details found for stock ID: {}", stockId);
            return gson.toJson(Map.of("error", "Item not found for stock ID: " + stockId));
        }
    }
}