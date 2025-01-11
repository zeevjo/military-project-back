package org.example.entities;

import java.sql.Timestamp;

public class AssignmentEntity {
    private int stockId;
    private int soldierId;
    private Timestamp assignmentDate;

    public AssignmentEntity(int stockId, int soldierId, Timestamp assignmentDate) {
        this.stockId = stockId;
        this.soldierId = soldierId;
        this.assignmentDate = assignmentDate;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getSoldierId() {
        return soldierId;
    }

    public void setSoldierId(int soldierId) {
        this.soldierId = soldierId;
    }

    public Timestamp getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(Timestamp assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    @Override
    public String toString() {
        return "AssignmentEntity{" +
                "stockId=" + stockId +
                ", soldierId=" + soldierId +
                ", assignmentDate=" + assignmentDate +
                '}';
    }
}
