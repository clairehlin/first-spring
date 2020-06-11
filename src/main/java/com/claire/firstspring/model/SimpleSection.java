package com.claire.firstspring.model;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public class SimpleSection implements Section {

    private final Integer id;
    private final String sectionName;
    private final List<Item> items;

    public SimpleSection(Integer id, String sectionName, List<Item> items) {
        this.id = id;
        this.sectionName = sectionName;
        this.items = items;
    }

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public String name() {
        return sectionName;
    }

    @Override
    public List<Item> items() {
        return unmodifiableList(items);
    }

    @Override
    public String toString() {
        return "SimpleSection{" +
            "sectionName='" + sectionName + '\'' +
            ", items=" + items +
            '}';
    }
}
