package org.example.entities;

public class HistoryEntity {
    private String productName;
    private String currentStatus;
    private Integer soldierId;
    private String soldierName;
    private String modificationDate;

    public HistoryEntity(String productName,
                         String currentStatus,
                         Integer soldierId,
                         String soldierName,
                         String modificationDate) {
        this.productName = productName;
        this.currentStatus = currentStatus;
        this.soldierId = soldierId;
        this.soldierName = soldierName;
        this.modificationDate = modificationDate;
    }

    public String getProductName() {
        return productName;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public Integer getSoldierId() {
        return soldierId;
    }

    public String getSoldierName() {
        return soldierName;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    @Override
    public String toString() {
        return "ProductStockHistoryEntity{" +
                "productName='" + productName + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", soldierId=" + soldierId +
                ", soldierName='" + soldierName + '\'' +
                ", modificationDate='" + modificationDate + '\'' +
                '}';
    }
}
