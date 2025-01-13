package org.example.entities;

public class UpdateProductStatusEntity {
    private int productStockId;  // corresponds to p_product_stock_id
    private int newStatusId;     // corresponds to p_new_status_id
    private int changedBy;       // corresponds to p_changed_by

    public UpdateProductStatusEntity() {
    }

    public UpdateProductStatusEntity(int productStockId, int newStatusId, int changedBy) {
        this.productStockId = productStockId;
        this.newStatusId = newStatusId;
        this.changedBy = changedBy;
    }

    public int getProductStockId() {
        return productStockId;
    }

    public void setProductStockId(int productStockId) {
        this.productStockId = productStockId;
    }

    public int getNewStatusId() {
        return newStatusId;
    }

    public void setNewStatusId(int newStatusId) {
        this.newStatusId = newStatusId;
    }

    public int getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(int changedBy) {
        this.changedBy = changedBy;
    }

    @Override
    public String toString() {
        return "UpdateProductStatusEntity{" +
                "productStockId=" + productStockId +
                ", newStatusId=" + newStatusId +
                ", changedBy=" + changedBy +
                '}';
    }
}
