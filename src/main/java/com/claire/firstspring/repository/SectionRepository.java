package com.claire.firstspring.repository;

import com.claire.firstspring.model.Section;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends MainTableAwareRepository {

    List<Section> sections();

    Optional<Section> section(Integer id);

    List<Section> menuSections(Integer id);

    Section create(Integer menuId, Section section);
}
