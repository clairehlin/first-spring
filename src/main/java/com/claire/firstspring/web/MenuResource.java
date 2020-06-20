package com.claire.firstspring.web;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleMenu;
import com.claire.firstspring.service.MenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuResource {

    private final MenuService menuService;

    public MenuResource(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public List<Menu> menus() {
        return menuService.list();
    }
}
