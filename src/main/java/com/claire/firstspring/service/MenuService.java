package com.claire.firstspring.service;

import com.claire.firstspring.model.Menu;

import java.util.List;

public interface MenuService {

    Menu addMenu(Integer restaurantId, Menu menu);

    List<Menu> list();
}
