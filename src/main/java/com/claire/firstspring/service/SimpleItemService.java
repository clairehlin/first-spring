package com.claire.firstspring.service;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.repository.FeatureRepository;
import com.claire.firstspring.repository.ItemRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Service
public class SimpleItemService implements ItemService {

    private final ItemRepository itemRepository;
    private final FeatureRepository featureRepository;

    public SimpleItemService(ItemRepository itemRepository, FeatureRepository featureRepository) {
        this.itemRepository = itemRepository;
        this.featureRepository = featureRepository;
    }

    @Override
    public Item addNewItemToSection(Integer sectionId, Item item) {
        Validate.notNull(sectionId, "section id cannot be null.");
        Validate.notNull(item, "item cannot be null.");
        Validate.isTrue(item.id() == null, "item id must be null for a new item");
        return itemRepository.create(sectionId, item);
    }

    @Override
    public List<Item> list() {
        return itemRepository.list();
    }

    @Override
    public Item getItem(Integer itemId) {
        return itemRepository.getItem(itemId);
    }

    @Override
    public void deleteItem(Integer itemId) {
        Item item = itemRepository.getItem(itemId);
        itemRepository.disassociateFeatures(itemId, item.features());
        itemRepository.deleteItem(itemId);
    }

    @Override
    public void updateItem(Item item) {
        final Set<Feature> features = featureRepository.itemFeatures(item.id());

        final Set<Feature> removedFeatures = removedFeatures(features, item.features());
        itemRepository.disassociateFeatures(item.id(), removedFeatures);

        final Set<Feature> addedFeatures = addedFeatures(features, item.features());
        itemRepository.associateFeatures(item.id(), addedFeatures);

        itemRepository.updateItemIgnoringFeatures(item);
    }

    private Set<Feature> addedFeatures(Set<Feature> originalFeatures, Set<Feature> newFeatures) {
        return newFeatures.stream()
            .filter(newFeature -> !originalFeatures.contains(newFeature))
            .collect(toSet());
    }

    private Set<Feature> removedFeatures(Set<Feature> originalFeatures, Set<Feature> newFeatures) {
        return originalFeatures.stream()
            .filter(originalFeature -> !newFeatures.contains(originalFeature))
            .collect(toSet());
    }
}
