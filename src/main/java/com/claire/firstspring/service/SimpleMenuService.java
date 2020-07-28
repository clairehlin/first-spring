package com.claire.firstspring.service;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleMenu;
import com.claire.firstspring.model.SimpleRestaurant;
import com.claire.firstspring.repository.MenuRepository;
import com.claire.firstspring.repository.SectionRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
public class SimpleMenuService implements MenuService {

    private final MenuRepository menuRepository;
    private final SectionRepository sectionRepository;
    private final SectionService sectionService;

    public SimpleMenuService(MenuRepository menuRepository, SectionRepository sectionRepository, SectionService sectionService) {
        this.menuRepository = menuRepository;
        this.sectionRepository = sectionRepository;
        this.sectionService = sectionService;
    }

    @Override
    public Menu addMenu(Integer restaurantId, Menu menu) {
        Validate.notNull(restaurantId, "restaurant id cannot be null.");
        Validate.notNull(menu, "menu cannot be null.");
        Validate.isTrue(menu.id() == null, "menu id must be null to create a new menu.");

        final Menu createdMenu = menuRepository.create(restaurantId, menu.name());

        final Set<Section> newSections = menu.sections()
            .stream()
            .map(section -> sectionService.addSection(createdMenu.id(), section))
            .collect(toSet());

        return new SimpleMenu(createdMenu.id(), createdMenu.name(), newSections);
    }

    @Override
    public List<Menu> list() {
        return menuRepository.menus();
    }

    @Override
    public Menu menu(Integer menuId) {
        Validate.notNull(menuId);
        final Optional<Menu> optionalMenu = menuRepository.menu(menuId);
        return optionalMenu.orElseThrow();
    }

    @Override
    public void updateMenu(Menu menu) {
        final Menu currentMenu = menuRepository.menu(menu.id()).orElseThrow();
        final Set<Section> currentSections = currentMenu.sections();

        Set<Section> removedSections = removedSections(menu.sections(), currentSections);
        removedSections.stream()
            .map(Section::id)
            .forEach(sectionService::deleteSection);

        Set<Section> addedSections = addedSections(menu.sections());
        addedSections.forEach(addedSection -> sectionService.addSection(menu.id(), addedSection));

        Set<Section> updatedSections = updatedSections(menu.sections());
        updatedSections.forEach(sectionService::updateSection);

        if (!menu.name().equals(currentMenu.name())) {
            menuRepository.updateMenuName(menu.id(), menu.name());
        }
    }

    private Set<Section> updatedSections(Set<Section> inputSections) {
        return inputSections.stream()
            .filter(inputSection -> inputSection.id() != null)
            .collect(toSet());
    }

    private Set<Section> addedSections(Set<Section> inputSections) {
        return inputSections.stream()
            .filter(inputSection -> inputSection.id() == null)
            .collect(toSet());
    }

    private Set<Section> removedSections(Set<Section> inputSections, Set<Section> currentSections) {
        List<Integer> inputSectionsId = inputSections.stream()
            .map(inputSection -> inputSection.id())
            .filter(Objects::nonNull)
            .collect(toList());

        return currentSections.stream()
            .filter(currentSection -> !inputSectionsId.contains(currentSection.id()))
            .collect(toSet());
    }


    @Override
    public void deleteMenu(Integer menuId) {
        Validate.notNull(menuId, "menu id cannot be null.");
        Menu menu = this.menu(menuId);
        Set<Section> sections = menu.sections();
        sections.forEach(section -> sectionService.deleteSection(section.id()));
        menuRepository.delete(menuId);
    }
}
