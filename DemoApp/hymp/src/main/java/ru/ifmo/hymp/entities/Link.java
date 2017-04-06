package ru.ifmo.hymp.entities;

import java.util.List;

public class Link {
    private String value;
    private List<Operation> operations;

    public Link(String value, List<Operation> operations) {
        this.value = value;
        this.operations = operations;
    }

    public String getValue() {
        return value;
    }

    public List<Operation> getOperations() {
        return operations;
    }

}
