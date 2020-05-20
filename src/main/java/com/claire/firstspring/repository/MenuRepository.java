package com.claire.firstspring.repository;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    List<Menu> menus();

    Optional<Menu> menu(Integer id);

    List<Menu> restaurantMenus(Integer restaurantId);
}
