package com.claire.firstspring.service;

import com.claire.firstspring.model.Section;
import com.claire.firstspring.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleSectionService implements SectionService {

    private final SectionRepository sectionRepository;

    public SimpleSectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    @Override
    public Section addSection(Integer menuId, Section section) {
        return sectionRepository.create(menuId, section);
    }

    @Override
    public List<Section> list() {
        return null;
    }
}
