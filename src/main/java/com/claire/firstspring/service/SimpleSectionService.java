package com.claire.firstspring.service;

import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.repository.ItemRepository;
import com.claire.firstspring.repository.SectionRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class SimpleSectionService implements SectionService {

    private final SectionRepository sectionRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;

    public SimpleSectionService(SectionRepository sectionRepository, ItemRepository itemRepository, ItemService itemService) {
        this.sectionRepository = sectionRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
    }

    @Override
    public Section addSection(Integer menuId, Section section) {
        return sectionRepository.create(menuId, section);
    }

    @Override
    public List<Section> list() {
        return sectionRepository.sections();
    }

    @Override
    public Section getSection(Integer sectionId) {
        Validate.notNull(sectionId, "section id cannot be null.");
        final Optional<Section> optionalSection = sectionRepository.section(sectionId);
        return optionalSection.orElseThrow();
    }

    @Override
    public void updateSection(Section section) {
        final Section currentSection = sectionRepository.section(section.id()).orElseThrow();
        List<Item> currentItems = currentSection.items();

        List<Item> removedItems = removedItems(section.items(), currentItems);
        removedItems.stream()
            .map(Item::id)
            .forEach(itemService::deleteItem);

        List<Item> addedItems = addedItems(section.items());
        addedItems.forEach(item -> itemService.addNewItemToSection(section.id(), item));

        List<Item> updatedItems = updatedItems(section.items());
        updatedItems.forEach(itemService::updateItem);

        if (!Objects.equals(currentSection.name(), section.name())) {
            sectionRepository.updateSectionName(section.id(), section.name());
        }
    }

    private List<Item> updatedItems(List<Item> inputItems) {
        return inputItems.stream()
            .filter(item -> item.id() != null)
            .collect(toList());
    }

    private List<Item> addedItems(List<Item> inputItems) {
        return inputItems.stream()
            .filter(item -> item.id() == null)
            .collect(toList());
    }

    private List<Item> removedItems(List<Item> inputItems, List<Item> currentItems) {
        List<Integer> inputItemIds = inputItems.stream()
            .map(Item::id)
            .filter(Objects::nonNull)
            .collect(toList());

        return currentItems.stream()
            .filter(currentItem -> !inputItemIds.contains(currentItem.id()))
            .collect(toList());
    }

    @Override
    public void deleteSection(Integer sectionId) {
        Validate.notNull(sectionId, "section id cannot be null.");
        final Section section = this.getSection(sectionId);
        section.items().forEach(
            item -> itemService.deleteItem(item.id())
        );
        sectionRepository.deleteSection(sectionId);
    }
}
