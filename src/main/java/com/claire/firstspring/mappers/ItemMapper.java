package com.claire.firstspring.mappers;

import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
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

        return new SimpleItem(
            webItem.id,
            webItem.name,
            webItem.description,
            webItem.price,
            featureMapper.toFirsts(webItem.features)
        );

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
