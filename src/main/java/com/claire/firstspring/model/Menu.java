package com.claire.firstspring.model;

import java.util.Set;

public interface Menu {

    Integer id();

    String name();

    Set<Section> sections();
}
