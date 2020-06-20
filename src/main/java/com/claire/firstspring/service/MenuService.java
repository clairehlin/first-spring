package com.claire.firstspring.service;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.SimpleMenu;

import java.util.List;

public interface MenuService {

    Menu addMenu(Integer restaurantId, Menu menu);

    List<Menu> list();
}
