package com.claire.firstspring.repository;

import com.claire.firstspring.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> sectionItems(Integer id);
}
