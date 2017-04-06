package ru.ifmo.hymp.entities;

import java.util.ArrayList;
import java.util.List;

public class Link {
    private String value;
    private List<Operation> operations = new ArrayList<>();

    public Link(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void addOperations(Operation operation) {
        operations.add(operation);
    }
}
