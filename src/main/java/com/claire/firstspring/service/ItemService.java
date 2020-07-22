package com.claire.firstspring.service;

import com.claire.firstspring.model.Item;

import java.util.List;

public interface ItemService {

    Item addNewItemToSection(Integer sectionId, Item item);

    List<Item> list();

    Item getItem(Integer itemId);

    void deleteItem(Integer itemId);

    void updateItem(Item item);
}
