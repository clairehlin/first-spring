package com.claire.firstspring.web;

import com.claire.firstspring.mappers.FeatureMapper;
import com.claire.firstspring.mappers.ItemMapper;
import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
import com.claire.firstspring.service.ItemService;
import com.claire.firstspring.web.model.WebItem;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * get all - done
 * get one - done
 * update multiple - done
 * update one - done
 * delete one - done
 * delete multiple - done
 * create one item- exists in section resource
 * create multiple items - exists in section resource
 */
@RestController
@RequestMapping("/items")
public class ItemResource {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final FeatureMapper featureMapper;

    public ItemResource(
        ItemService itemService,
        ItemMapper itemMapper,
        FeatureMapper featureMapper
    ) {
        this.itemService = itemService;
        this.itemMapper = itemMapper;
        this.featureMapper = featureMapper;
    }

    @GetMapping
    public List<WebItem> items() {
        return itemService.list()
            .stream()
            .map(itemMapper::toSecond)
            .collect(toList());
    }

    @GetMapping("/{item-id}")
    public WebItem item(@PathVariable("item-id") Integer itemId) {
        return itemMapper.toSecond(itemService.getItem(itemId));
    }

    @DeleteMapping("/{item-id}")
    public void deleteItem(@PathVariable("item-id") Integer itemId) {
        itemService.deleteItem(itemId);
    }

    @DeleteMapping
    public void deleteItems(@RequestParam("ids") List<Integer> itemIds) {
        itemIds.forEach(itemService::deleteItem);
    }

    @PutMapping("/{item-id}")
    public void updateItem(@PathVariable("item-id") Integer itemId, @RequestBody WebItem webItem) {
        Item item = new SimpleItem(
            itemId,
            webItem.name,
            webItem.description,
            webItem.price,
            featureMapper.toFirsts(webItem.features)
        );
        itemService.updateItem(item);
    }

    @PutMapping
    public void updateItems(@RequestBody List<WebItem> webItems) {
        webItems.forEach(
            webItem -> this.updateItem(webItem.id, webItem)
        );
    }
}
