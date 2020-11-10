package com.claire.firstspring.mappers;

import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleSection;
import com.claire.firstspring.web.model.WebSection;
import org.springframework.stereotype.Component;

@Component
public class SectionMapper implements Mapper<Section, WebSection> {
    private final ItemMapper itemMapper;

    public SectionMapper(ItemMapper itemMapper) {
        this.itemMapper = itemMapper;
    }

    @Override
    public Section toFirst(WebSection webSection) {
        return new SimpleSection(
            webSection.id,
            webSection.name,
            itemMapper.toFirsts(webSection.items)
        );
//        throw new UnsupportedOperationException("Have not yet implemented WebSection to Section mapping.");
    }

    @Override
    public WebSection toSecond(Section section) {
        WebSection webSection = new WebSection();
        webSection.id = section.id();
        webSection.name = section.name();
        webSection.items = itemMapper.toSeconds(section.items());
        return webSection;
    }
}
