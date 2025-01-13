package org.example.services;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entities.HistoryEntity;
import org.example.repositories.HistoryRepository;

import java.util.List;

public class HistoryService {
    private static final Logger logger = LogManager.getLogger(HistoryService.class);

    private final HistoryRepository historyRepository = new HistoryRepository();
    private final Gson gson = new Gson();

    public String getHistory() {
        logger.info("Retrieving product stock history...");

        // Get the history records from the repository
        List<HistoryEntity> historyList = historyRepository.getProductStockHistory();

        String jsonResult = gson.toJson(historyList);

        logger.info("History retrieval successful. Returning JSON response.");

        return jsonResult;
    }
}
