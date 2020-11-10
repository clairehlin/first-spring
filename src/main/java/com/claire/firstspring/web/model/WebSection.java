package com.claire.firstspring.web.model;

import java.util.List;
import java.util.Objects;

public class WebSection {
    public Integer id;
    public String name;
    public List<WebItem> items;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebSection that = (WebSection) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, items);
    }
}
