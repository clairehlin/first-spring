package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemRepository extends MainTableAwareRepository{
    List<Item> sectionItems(Integer id);

    Item create(Integer sectionId, Item item);

    List<Item> list();

    Item getItem(Integer itemId);

    void deleteItem(Integer itemId);

    void updateItemIgnoringFeatures(Item item);

    void associateFeatures(Integer itemId, Set<Feature> features);

    void disassociateFeatures(Integer itemId, Set<Feature> features);
}
