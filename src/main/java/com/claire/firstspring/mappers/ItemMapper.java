package com.claire.firstspring.mappers;

import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.web.model.WebItem;
import com.claire.firstspring.web.model.WebSection;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper implements Mapper<Item, WebItem> {
    @Override
    public Item toFirst(WebItem webItem) {
        throw new UnsupportedOperationException("Have not yet implemented WebItem to Item mapping.");
    }

    @Override
    public WebItem toSecond(Item item) {
        WebItem webItem = new WebItem();
        webItem.name = item.name();
        return webItem;
    }
}
