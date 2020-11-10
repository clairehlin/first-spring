package com.claire.firstspring.web.model;

import java.util.Objects;
import java.util.Set;

public class WebItem {
    public Integer id;
    public String name;
    public String description;
    public double price;
    public Set<String> features;

    public static WebItem of(Integer id, String name, String description, double price, Set<String> features) {
        WebItem webItem = new WebItem();
        webItem.id = id;
        webItem.name = name;
        webItem.description = description;
        webItem.price = price;
        webItem.features = features;

        return webItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebItem webItem = (WebItem) o;
        return Double.compare(webItem.price, price) == 0 &&
            Objects.equals(id, webItem.id) &&
            Objects.equals(name, webItem.name) &&
            Objects.equals(description, webItem.description) &&
            Objects.equals(features, webItem.features);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, features);
    }
}
