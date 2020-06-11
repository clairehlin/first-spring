package com.claire.firstspring.model;

import java.util.Set;

public class SimpleItem implements Item {

    private final Integer id;
    private final String name;
    private final String description;
    private final double price;
    private final Set<Feature> features;

    public SimpleItem(Integer id, String name, String description, double price, Set<Feature> features) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.features = Set.copyOf(features);
    }

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public double price() {
        return price;
    }

    @Override
    public Set<Feature> features() {
        return Set.copyOf(features);
    }

    @Override
    public String toString() {
        return "SimpleItem{" +
            "name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", price=" + price +
            ", features=" + features +
            '}';
    }
}
