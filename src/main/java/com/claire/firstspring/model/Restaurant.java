package com.claire.firstspring.model;

import java.util.Set;

public interface Restaurant {

    Integer id();

    String name();

    Set<Menu> menus();
}
