package ru.ifmo.hymp.entities;

import java.util.List;

public class Link {
    private String url;
    private String title;
    private List<Operation> operations;

    public Link(String url, String title, List<Operation> operations) {
        this.url = url;
        this.title = title;
        this.operations = operations;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public List<Operation> getOperations() {
        return operations;
    }

}
