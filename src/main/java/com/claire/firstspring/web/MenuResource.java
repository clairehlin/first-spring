package com.claire.firstspring.web;

import com.claire.firstspring.mappers.MenuMapper;
import com.claire.firstspring.service.MenuService;
import com.claire.firstspring.web.model.WebMenu;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/menus")
public class MenuResource {

    private final MenuService menuService;
    private final MenuMapper menuMapper;

    public MenuResource(MenuService menuService, MenuMapper menuMapper) {
        this.menuService = menuService;
        this.menuMapper = menuMapper;
    }

    @GetMapping
    public List<WebMenu> menus() {
        return menuService.list()
            .stream()
            .map(menuMapper::toSecond)
            .collect(toList());
    }
}
