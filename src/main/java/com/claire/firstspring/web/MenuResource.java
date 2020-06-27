package com.claire.firstspring.web;

import com.claire.firstspring.mappers.MenuMapper;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleSection;
import com.claire.firstspring.service.MenuService;
import com.claire.firstspring.service.SectionService;
import com.claire.firstspring.web.model.WebMenu;
import com.claire.firstspring.web.model.WebSection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/menus")
public class MenuResource {

    private final MenuService menuService;
    private final MenuMapper menuMapper;
    private final SectionService sectionService;

    public MenuResource(MenuService menuService, MenuMapper menuMapper, SectionService sectionService) {
        this.menuService = menuService;
        this.menuMapper = menuMapper;
        this.sectionService = sectionService;
    }

    @GetMapping
    public List<WebMenu> menus() {
        return menuService.list()
            .stream()
            .map(menuMapper::toSecond)
            .collect(toList());
    }

    @PostMapping("/{menu-id}/sections")
    public void createSections(@PathVariable("menu-id") Integer menuId, @RequestBody WebSection webSection) {
        Section section = new SimpleSection(menuId, webSection.name, emptyList());
        sectionService.addSection(menuId, section);
    }
}
