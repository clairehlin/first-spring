package com.claire.firstspring.model;

import java.util.List;

import static java.util.Collections.unmodifiableList;

public class SimpleSection implements Section {

    private final String sectionName;
    private final List<Item> items;

    public SimpleSection(String sectionName, List<Item> items) {
        this.sectionName = sectionName;
        this.items = items;
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
