package com.claire.firstspring.model;

import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.Set;

import static org.apache.commons.collections4.SetUtils.emptyIfNull;

public class SimpleItem implements Item {

    private final Integer id;
    private final String name;
    private final String description;
    private final double price;
    private final Set<Feature> features;

    public SimpleItem(Integer id, String name, String description, double price, Set<Feature> features) {
        Validate.notBlank(description, "client-error: description cannot be blank");
        Validate.notBlank(name, "client-error: name cannot be blank");
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.features = Set.copyOf(emptyIfNull(features));
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

    public SimpleItem withId(Integer id) {
        return new SimpleItem(id, name, description, price, features);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleItem that = (SimpleItem) o;
        return Double.compare(that.price, price) == 0 &&
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(features, that.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, features);
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
