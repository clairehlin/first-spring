package com.claire.firstspring.web;

import com.claire.firstspring.mappers.ItemMapper;
import com.claire.firstspring.mappers.MenuMapper;
import com.claire.firstspring.mappers.SectionMapper;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleMenu;
import com.claire.firstspring.model.SimpleSection;
import com.claire.firstspring.service.MenuService;
import com.claire.firstspring.service.SectionService;
import com.claire.firstspring.web.model.WebMenu;
import com.claire.firstspring.web.model.WebSection;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

/**
 * get one - done
 * get multiple - done
 * get all - done
 * update one - done
 * update multiple - done
 * delete one - done
 * delete multiple - done
 * create multiple (including creating one) - done
 */

@RestController
@RequestMapping("/menus")
public class MenuResource {

    private final MenuService menuService;
    private final MenuMapper menuMapper;
    private final SectionService sectionService;
    private final ItemMapper itemMapper;
    private final SectionMapper sectionMapper;

    public MenuResource(MenuService menuService, MenuMapper menuMapper, SectionService sectionService, ItemMapper itemMapper, SectionMapper sectionMapper) {
        this.menuService = menuService;
        this.menuMapper = menuMapper;
        this.sectionService = sectionService;
        this.itemMapper = itemMapper;
        this.sectionMapper = sectionMapper;
    }

    private List<WebMenu> menus() {
        return menuService.list()
            .stream()
            .map(menuMapper::toSecond)
            .collect(toList());
    }

    @GetMapping("/{menu-id}")
    public WebMenu menu(@PathVariable("menu-id") Integer menuId) {
        Menu menu = menuService.menu(menuId);
        return menuMapper.toSecond(menu);
    }

    @GetMapping
    public List<WebMenu> menus(@RequestParam List<Integer> ids) {
        return emptyIfNull(ids).isEmpty()
            ? menus()
            : ids.stream()
            .map(menuService::menu)
            .map(menuMapper::toSecond)
            .collect(toList());
    }

    @PutMapping("/{menu-id}")
    public void updateMenu(@PathVariable("menu-id") Integer menuId, @RequestBody WebMenu webMenu) {
        Menu menu = new SimpleMenu(
            menuId,
            webMenu.name,
            sectionMapper.toFirsts(webMenu.sections)
        );
        menuService.updateMenu(menu);
    }

    @PutMapping
    public void updateMenus(@RequestBody List<WebMenu> webMenus) {
        webMenus.forEach(
            webMenu -> this.updateMenu(webMenu.id, webMenu)
        );
    }

    @PostMapping("/{menu-id}/sections")
    public void createSections(@PathVariable("menu-id") Integer menuId, @RequestBody List<WebSection> webSections) {
        webSections.forEach(webSection -> {
            Section section = new SimpleSection(
                webSection.id,
                webSection.name,
                itemMapper.toFirsts(webSection.items)
            );
            sectionService.addSection(menuId, section);
        });
    }

    @PostMapping("/{menu-id}/section")
    public void createSection(@PathVariable("menu-id") Integer menuId, @RequestBody WebSection webSection) {
        Section section = new SimpleSection(
            webSection.id,
            webSection.name,
            itemMapper.toFirsts(webSection.items)
        );
        sectionService.addSection(menuId, section);
    }

    @DeleteMapping("/{menu-id}")
    public void deleteMenu(@PathVariable("menu-id") Integer menuId) {
        menuService.deleteMenu(menuId);
    }

    @DeleteMapping
    public void deleteMenus(@RequestParam("ids") List<Integer> menuIds) {
        menuIds.forEach(menuService::deleteMenu);
    }
}
