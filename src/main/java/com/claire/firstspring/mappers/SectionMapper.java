package com.claire.firstspring.mappers;

import com.claire.firstspring.model.Section;
import com.claire.firstspring.web.model.WebSection;
import org.springframework.stereotype.Component;

@Component
public class SectionMapper implements Mapper<Section, WebSection> {
    @Override
    public Section toFirst(WebSection webSection) {
        throw new UnsupportedOperationException("Have not yet implemented WebSection to Section mapping.");
    }

    @Override
    public WebSection toSecond(Section section) {
        WebSection webSection = new WebSection();
        webSection.name = section.name();
        return webSection;
    }
}
