package com.claire.firstspring.model;

public class SimpleRestaurant implements Restaurant {

    private final String name;
    private final Menu menu;

    public SimpleRestaurant(String name, Menu menu) {
        this.name = name;
        this.menu = menu;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public Menu menu() {
        return menu;
    }
}
