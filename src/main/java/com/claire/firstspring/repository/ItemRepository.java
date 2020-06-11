package com.claire.firstspring.repository;

import com.claire.firstspring.model.Item;

import java.util.List;

public interface ItemRepository extends MainTableAwareRepository{
    List<Item> sectionItems(Integer id);

    Item create(Integer sectionId, Item item);
}
