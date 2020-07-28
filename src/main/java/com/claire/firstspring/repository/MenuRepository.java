package com.claire.firstspring.repository;

import com.claire.firstspring.model.Menu;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends MainTableAwareRepository {
    List<Menu> menus();

    Optional<Menu> menu(Integer id);

    List<Menu> restaurantMenus(Integer restaurantId);

    Menu create(Integer restaurantId, String menu);

    void delete(Integer menuId);

    void updateMenuName(Integer id, String name);
}
