package com.claire.firstspring.mappers;

import com.claire.firstspring.model.Item;
import com.claire.firstspring.web.model.WebItem;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper implements Mapper<Item, WebItem> {
    private final FeatureMapper featureMapper;

    public ItemMapper(FeatureMapper featureMapper) {
        this.featureMapper = featureMapper;
    }

    @Override
    public Item toFirst(WebItem webItem) {
        throw new UnsupportedOperationException("Have not yet implemented WebItem to Item mapping.");
    }

    @Override
    public WebItem toSecond(Item item) {
        WebItem webItem = new WebItem();
        webItem.id = item.id();
        webItem.name = item.name();
        webItem.description = item.description();
        webItem.price = item.price();
        webItem.features = featureMapper.toSeconds(item.features());
        return webItem;
    }
}
