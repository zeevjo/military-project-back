package org.example.entities;

public class InventoryEntity {
    private String productName;
    private int totalInStock;
    private int totalAvailable;
    private int totalUnderMaintenance;
    private int totalAssigned;

    public InventoryEntity(String productName, int totalInStock, int totalAvailable,
                               int totalUnderMaintenance, int totalAssigned) {
        this.productName = productName;
        this.totalInStock = totalInStock;
        this.totalAvailable = totalAvailable;
        this.totalUnderMaintenance = totalUnderMaintenance;
        this.totalAssigned = totalAssigned;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTotalInStock() {
        return totalInStock;
    }

    public void setTotalInStock(int totalInStock) {
        this.totalInStock = totalInStock;
    }

    public int getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(int totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public int getTotalUnderMaintenance() {
        return totalUnderMaintenance;
    }

    public void setTotalUnderMaintenance(int totalUnderMaintenance) {
        this.totalUnderMaintenance = totalUnderMaintenance;
    }

    public int getTotalAssigned() {
        return totalAssigned;
    }

    public void setTotalAssigned(int totalAssigned) {
        this.totalAssigned = totalAssigned;
    }

    @Override
    public String toString() {
        return "ProductStockSummary{" +
                "productName='" + productName + '\'' +
                ", totalInStock=" + totalInStock +
                ", totalAvailable=" + totalAvailable +
                ", totalUnderMaintenance=" + totalUnderMaintenance +
                ", totalAssigned=" + totalAssigned +
                '}';
    }
}
