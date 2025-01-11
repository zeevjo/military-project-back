package org.example.services;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entities.AssignmentEntity;
import org.example.repositories.AssignRepository;

import java.sql.Timestamp;
import java.util.Map;

public class AssignService {

    private static final Logger logger = LogManager.getLogger(AssignService.class);
    private final AssignRepository assignRepository = new AssignRepository();
    private final Gson gson = new Gson();

    public String assign(String requestBody) {
        logger.info("Parsing request body to AssignmentEntity");

        AssignmentEntity assignmentEntity;
        try {
            Map<String, Object> requestMap = gson.fromJson(requestBody, Map.class);
            int stockId = ((Double) requestMap.get("stockId")).intValue();
            int soldierId = ((Double) requestMap.get("soldierId")).intValue();
            Timestamp assignmentDate = Timestamp.valueOf((String) requestMap.get("assignmentDate"));

            assignmentEntity = new AssignmentEntity(stockId, soldierId, assignmentDate);

        } catch (Exception e) {
            logger.error("Error parsing request body: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid request payload", e);
        }

        logger.info("Parsed AssignmentEntity: {}", assignmentEntity);

        String result = assignRepository.assignProductToSoldier(assignmentEntity);

        logger.info("Result from repository: {}", result);

        return gson.toJson(Map.of("message", result));
    }
}
