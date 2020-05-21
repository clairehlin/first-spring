package com.claire.firstspring.repository;

import com.claire.firstspring.model.Section;

import java.util.List;
import java.util.Optional;

public interface SectionRepository {

    List<Section> sections();

    Optional<Section> section(Integer id);

    List<Section> menuSections(Integer id);

}
