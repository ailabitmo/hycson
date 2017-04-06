package ru.ifmo.hymp.entities;

public class Operation {
    private Type type;
    private String expects;
    private String returns;

    public Operation(Type type) {
        this.type = type;
    }

    public Operation(Type type, String returns) {
        this.type = type;
        this.returns = returns;
    }

    public Operation(Type type, String expects, String returns) {
        this(type, returns);
        this.expects = expects;
    }

    public Type getType() {
        return type;
    }

    public String getExpects() {
        return expects;
    }

    public String getReturns() {
        return returns;
    }

    public enum Type {
        GET, POST, UNKNOWN;
    }
}
