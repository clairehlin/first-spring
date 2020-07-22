package com.claire.firstspring.service;

import com.claire.firstspring.model.Menu;
import com.google.common.io.Files;

import java.util.List;

public interface MenuService {

    Menu addMenu(Integer restaurantId, Menu menu);

    List<Menu> list();

    Menu menu(Integer menuId);

    void updateMenu(Menu menu);

    void deleteMenu(Integer menuId);
}
