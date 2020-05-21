package com.claire.firstspring.model;

import java.util.List;

public interface Section {

    Integer id();

    String name();

    List<Item> items();
}
