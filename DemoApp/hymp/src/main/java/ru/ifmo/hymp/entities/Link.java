package ru.ifmo.hymp.entities;

import java.util.List;

public class Link {
    private String value;
    private String title;
    private List<Operation> operations;

    public Link(String value, String title, List<Operation> operations) {
        this.value = value;
        this.title = title;
        this.operations = operations;
    }

    public String getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }

    public List<Operation> getOperations() {
        return operations;
    }

}
