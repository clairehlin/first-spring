package com.claire.firstspring.service;

import com.claire.firstspring.model.Section;
import com.claire.firstspring.web.model.WebSection;

import java.util.List;

public interface SectionService {

    Section addSection(Integer menuId, Section section);

    List<Section> list();

    Section getSection(Integer sectionId);

    void updateSection(Section section);

    void deleteSection(Integer sectionId);
}
