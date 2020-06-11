package com.claire.firstspring.model;

import java.util.Set;

public interface Item {

    Integer id();

    String name();

    String description();

    double price();

    Set<Feature> features();
}
