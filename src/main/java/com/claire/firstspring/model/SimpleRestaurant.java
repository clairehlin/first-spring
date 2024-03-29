package com.claire.firstspring.model;

import java.util.Set;

public class SimpleRestaurant implements Restaurant {

    private final Integer id;
    private final String name;
    private final Set<Menu> menus;

    public SimpleRestaurant(Integer id, String name, Set<Menu> menus) {
        this.id = id;
        this.name = name;
        this.menus = menus;
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
    public Set<Menu> menus() {
        return menus;
    }

}
