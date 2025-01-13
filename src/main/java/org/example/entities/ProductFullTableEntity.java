package org.example.entities;

import java.sql.Timestamp;

public class ProductFullTableEntity {
    private String productName;
    private String currentStatus;
    private Timestamp lastModified;
    private String modifiedBySoldier;

    public ProductFullTableEntity() {
    }

    public ProductFullTableEntity(String productName,
                                  String currentStatus,
                                  Timestamp lastModified,
                                  String modifiedBySoldier) {
        this.productName = productName;
        this.currentStatus = currentStatus;
        this.lastModified = lastModified;
        this.modifiedBySoldier = modifiedBySoldier;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    public String getModifiedBySoldier() {
        return modifiedBySoldier;
    }

    public void setModifiedBySoldier(String modifiedBySoldier) {
        this.modifiedBySoldier = modifiedBySoldier;
    }

    @Override
    public String toString() {
        return "ProductFullTableEntity{" +
                "productName='" + productName + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", lastModified=" + lastModified +
                ", modifiedBySoldier='" + modifiedBySoldier + '\'' +
                '}';
    }
}
