package ru.ifmo.hymp.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal representation of Hypermedia API resource
 */
public class Resource {
    private String id;
    private String type;
    private Map<String, Object> propertyMap = new HashMap<>(4);
    private List<Link> links = new ArrayList<>();
    private List<Operation> operations;

    public Resource(String id, String type, List<Operation> operations) {
        this.id = id;
        this.type = type;
        this.operations = operations;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Map<String, Object> getPropertyMap() {
        return propertyMap;
    }

    public List<Link> getLinks() {
        return links;
    }

    public List<Operation> getOperations() {
        return operations;
    }
}
