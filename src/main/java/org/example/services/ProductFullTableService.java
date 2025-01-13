package org.example.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.entities.ProductFullTableEntity;
import org.example.repositories.ProductFullTableRepository;

import java.util.List;

public class ProductFullTableService {

    private static final Logger logger = LogManager.getLogger(ProductFullTableService.class);

    private final ProductFullTableRepository productFullTableRepository;

    public ProductFullTableService() {
        this.productFullTableRepository = new ProductFullTableRepository();
    }

    public List<ProductFullTableEntity> getProductFullTableByName(String productName) {
        logger.info("Fetching details for product: {}", productName);

        List<ProductFullTableEntity> productDetails = productFullTableRepository.getProductFullTableByName(productName);

        logger.info("Fetched {} records for product: {}", productDetails.size(), productName);
        return productDetails;
    }
}

