package com.claire.firstspring.web;

import com.claire.firstspring.mappers.FeatureMapper;
import com.claire.firstspring.mappers.ItemMapper;
import com.claire.firstspring.mappers.SectionMapper;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleItem;
import com.claire.firstspring.model.SimpleSection;
import com.claire.firstspring.service.ItemService;
import com.claire.firstspring.service.SectionService;
import com.claire.firstspring.web.model.WebItem;
import com.claire.firstspring.web.model.WebSection;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/sections")
@Transactional
public class SectionResource {

    private final SectionService sectionService;
    private final ItemService itemService;
    private final SectionMapper sectionMapper;
    private final FeatureMapper featureMapper;
    private final ItemMapper itemMapper;

    public SectionResource(
        SectionService sectionService,
        ItemService itemService,
        SectionMapper sectionMapper,
        FeatureMapper featureMapper,
        ItemMapper itemMapper) {
        this.sectionService = sectionService;
        this.itemService = itemService;
        this.sectionMapper = sectionMapper;
        this.featureMapper = featureMapper;
        this.itemMapper = itemMapper;
    }

    @GetMapping
    public List<WebSection> sections() {
        return sectionService.list()
            .stream()
            .map(sectionMapper::toSecond)
            .collect(toList());
    }

    @GetMapping("/{section-id}")
    public WebSection section(@PathVariable("section-id") Integer sectionId) {
        return sectionMapper.toSecond(sectionService.getSection(sectionId));
    }

    @PutMapping("/{section-id}")
    public void updateSection(@PathVariable("section-id") Integer sectionId, @RequestBody WebSection webSection) {
        Section section = new SimpleSection(
            sectionId,
            webSection.name,
            itemMapper.toFirsts(webSection.items)
        );
        sectionService.updateSection(section);
    }

    @PutMapping
    public void updateSections(@RequestBody List<WebSection> webSections) {
        webSections.forEach(
            webSection -> this.updateSection(webSection.id, webSection)
        );
    }

    @PostMapping("/{section-id}/items")
    public void createItems(@PathVariable("section-id") Integer sectionId, @RequestBody List<WebItem> webItems) {
        webItems.forEach(webItem -> createItem(sectionId, webItem));
    }

    @DeleteMapping("/{section-id}")
    public void deleteSection(@PathVariable("section-id") Integer sectionId) {
        sectionService.deleteSection(sectionId);
    }

    @DeleteMapping
    public void deleteSections(@RequestParam("ids") List<Integer> sectionIds) {
        sectionIds.forEach(sectionService::deleteSection);
    }

    private void createItem(Integer sectionId, WebItem webItem) {

            Item item = new SimpleItem(
                null,
                webItem.name,
                webItem.description,
                webItem.price,
                featureMapper.toFirsts(webItem.features)
            );
            itemService.addNewItemToSection(sectionId, item);

    }
}
