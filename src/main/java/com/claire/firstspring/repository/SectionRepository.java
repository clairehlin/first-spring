package com.claire.firstspring.repository;

import com.claire.firstspring.model.Section;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends MainTableAwareRepository {

    List<Section> sections();

    Optional<Section> section(Integer id);

    List<Section> menuSections(Integer menuId);

    Section create(Integer menuId, String section);

    void deleteSection(Integer sectionId);

    void updateSectionName(Integer id, String name);
}
