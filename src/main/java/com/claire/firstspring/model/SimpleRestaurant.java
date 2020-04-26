package com.claire.firstspring.model;

public class SimpleRestaurant implements Restaurant {

    private final Menu menu;

    public SimpleRestaurant(Menu menu) {
        this.menu = menu;
    }

    @Override
    public Menu menu() {
        return menu;
    }
}
