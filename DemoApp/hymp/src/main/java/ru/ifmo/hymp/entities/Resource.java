package ru.ifmo.hymp.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Resource {
    private String id;
    private String type;
    private Map<String, Object> propertyMap = new HashMap<>(4);
    private List<Link> links = new ArrayList<>();

    public Resource(String id, String type) {
        this.id = id;
        this.type = type;
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

}
