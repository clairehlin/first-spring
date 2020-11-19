package com.claire.firstspring.mappers;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.web.model.WebMenu;
import org.springframework.stereotype.Component;

@Component
public class MenuMapper implements Mapper<Menu, WebMenu> {
    private final SectionMapper sectionMapper;

    public MenuMapper(SectionMapper sectionMapper) {
        this.sectionMapper = sectionMapper;
    }

    @Override
    public Menu toFirst(WebMenu webMenu) {
        throw new UnsupportedOperationException("Have not yet implemented WebMenu to Menu mapping.");
    }

    @Override
    public WebMenu toSecond(Menu menu) {
        WebMenu webMenu = new WebMenu();
        webMenu.id = menu.id();
        webMenu.name = menu.name();
        webMenu.sections = sectionMapper.toSeconds(menu.sections());
        return webMenu;
    }
}
