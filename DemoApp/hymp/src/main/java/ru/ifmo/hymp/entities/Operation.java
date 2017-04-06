package ru.ifmo.hymp.entities;

public class Operation {
    private String entityType;
    private Type operationType;

    public Operation(String entityType, Type operationType) {
        this.entityType = entityType;
        this.operationType = operationType;
    }

    public String getEntityType() {
        return entityType;
    }

    public Type getOperationType() {
        return operationType;
    }

    public enum Type {
        GET, POST, PUT, DELETE;
    }
}
