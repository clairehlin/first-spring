package com.claire.firstspring.service;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.repository.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleMenuService implements MenuService {

    private final MenuRepository menuRepository;

    public SimpleMenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public Menu addMenu(Integer restaurantId, Menu menu) {
        return menuRepository.create(restaurantId, menu);
    }

    @Override
    public List<Menu> list() {
        return menuRepository.menus();
    }
}
