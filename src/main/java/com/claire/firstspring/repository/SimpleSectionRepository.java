package com.claire.firstspring.repository;

import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleSection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleSectionRepository implements SectionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SectionRowMapper sectionRowMapper;
    private final IdGeneratingRepository idGeneratingRepository;


    public SimpleSectionRepository(JdbcTemplate jdbcTemplate, SectionRowMapper sectionRowMapper, IdGeneratingRepository idGeneratingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionRowMapper = sectionRowMapper;
        this.idGeneratingRepository = idGeneratingRepository;
    }

    @Override
    public List<Section> sections() {
        return jdbcTemplate.query(
            "SELECT * FROM sections LIMIT 101",
            sectionRowMapper
        );
    }

    @Override
    public Optional<Section> section(Integer id) {
        Section section = jdbcTemplate.queryForObject(
            "SELECT * FROM section WHERE id = ?",
            toArray(id),
            sectionRowMapper
        );
        return Optional.ofNullable(section);
    }

    @Override
    public List<Section> menuSections(Integer menuId) {
        return jdbcTemplate.query(
            "SELECT * FROM section WHERE menu_id = ?",
            toArray(menuId),
            sectionRowMapper
        );

    }

    @Override
    public Section create(Integer menuId, String sectionName) {
        final int sectionId = idGeneratingRepository.nextId(this);

        jdbcTemplate.update(
            "INSERT INTO section (id, name, menu_id) VALUES (?, ?, ?)",
            sectionId,
            sectionName,
            menuId
        );

        return new SimpleSection(
            sectionId,
            sectionName,
            emptyList()
        );
    }

    @Override
    public void deleteSection(Integer sectionId) {
        final int deleted = jdbcTemplate.update(
            "DELETE FROM section WHERE id = ?",
            sectionId
        );

        if (deleted < 1) {
            throw new NoSuchElementException("could not find section with section id " + sectionId);
        }
    }

    @Override
    public void updateSectionName(Integer id, String name) {
        final int updated = jdbcTemplate.update(
            "UPDATE section SET name = ? WHERE id = ?",
            name,
            id
        );

        if (updated < 1) {
            throw new NoSuchElementException(
                String.format("could not update section name %s, perhaps section with id %s does not exist", name, id)
            );
        }
    }

    @Override
    public String getMainTableName() {
        return "section";
    }
}
