package com.claire.firstspring.web;

import com.claire.firstspring.mappers.FeatureMapper;
import com.claire.firstspring.mappers.SectionMapper;
import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
import com.claire.firstspring.service.ItemService;
import com.claire.firstspring.service.SectionService;
import com.claire.firstspring.web.model.WebItem;
import com.claire.firstspring.web.model.WebSection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@RestController
@RequestMapping("/sections")
public class SectionResource {

    private final SectionService sectionService;
    private final ItemService itemService;
    private final SectionMapper sectionMapper;
    private final FeatureMapper featureMapper;

    public SectionResource(
        SectionService sectionService,
        ItemService itemService,
        SectionMapper sectionMapper,
        FeatureMapper featureMapper
    ) {
        this.sectionService = sectionService;
        this.itemService = itemService;
        this.sectionMapper = sectionMapper;
        this.featureMapper = featureMapper;
    }

    @GetMapping
    public List<WebSection> sections() {
        return sectionService.list()
            .stream()
            .map(sectionMapper::toSecond)
            .collect(toList());
    }

    @PostMapping("/{section-id}/items")
    public void createItems(@PathVariable("section-id") Integer sectionId, @RequestBody WebItem webItem) {
        Item item = new SimpleItem(
            sectionId,
            webItem.name,
            webItem.description,
            webItem.price,
            toFeatures(webItem.features)
        );
        itemService.addItem(sectionId, item);
    }

    private Set<Feature> toFeatures(Set<String> features) {
        return features.stream()
            .map(featureMapper::toFirst)
            .collect(toSet());
    }
}
