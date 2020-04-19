package com.claire.firstspring.model;

import java.util.Set;

import static java.util.Collections.unmodifiableSet;

public class SimpleItem implements Item{

    private final String itemName;
    private final String itemDescription;
    private final double price;
    private final Set<Feature> features;

    public SimpleItem(String itemName, String itemDescription, double price, Set<Feature> features) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.price = price;
        this.features = features;
    }

    @Override
    public String name() {
        return itemName;
    }

    @Override
    public String description() {
        return itemDescription;
    }

    @Override
    public double price() {
        return price;
    }

    @Override
    public Set<Feature> features() {
        return unmodifiableSet(features);
    }

    @Override
    public String toString() {
        return "SimpleItem{" +
                "itemName='" + itemName + '\'' +
                ", itemDescription='" + itemDescription + '\'' +
                ", price=" + price +
                ", features=" + features +
                '}';
    }
}
