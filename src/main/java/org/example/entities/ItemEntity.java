package org.example.entities;

public class ItemEntity {
    private int stockId;
    private String productName;
    private String productType;
    private String currentStatus;
    private String armoryLocation;
    private String isCurrentlyAssigned;
    private String assignedTo;
    private String assignmentDate;

    public ItemEntity(int stockId, String productName, String productType, String currentStatus,
                              String armoryLocation, String isCurrentlyAssigned, String assignedTo, String assignmentDate) {
        this.stockId = stockId;
        this.productName = productName;
        this.productType = productType;
        this.currentStatus = currentStatus;
        this.armoryLocation = armoryLocation;
        this.isCurrentlyAssigned = isCurrentlyAssigned;
        this.assignedTo = assignedTo;
        this.assignmentDate = assignmentDate;
    }

    public int getStockId() {
        return stockId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductType() {
        return productType;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public String getArmoryLocation() {
        return armoryLocation;
    }

    public String getIsCurrentlyAssigned() {
        return isCurrentlyAssigned;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getAssignmentDate() {
        return assignmentDate;
    }

    @Override
    public String toString() {
        return "ProductStockEntity{" +
                "stockId=" + stockId +
                ", productName='" + productName + '\'' +
                ", productType='" + productType + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", armoryLocation='" + armoryLocation + '\'' +
                ", isCurrentlyAssigned='" + isCurrentlyAssigned + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", assignmentDate='" + assignmentDate + '\'' +
                '}';
    }
}
