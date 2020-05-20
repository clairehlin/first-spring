package com.claire.firstspring.model;

import java.util.Set;

public class SimpleMenu implements Menu {
    private final Integer id;
    private final String name;
    private final Set<Section> sections;

    public SimpleMenu(Integer id, String name, Set<Section> sections) {
        this.id = id;
        this.name = name;
        this.sections = sections;
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
    public Set<Section> sections() {
        return sections;
    }
}
