package com.claire.firstspring.repository;

import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleSection;
import org.apache.commons.lang3.Validate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
            "SELECT * FROM section LIMIT 101",
            sectionRowMapper
        );
    }

    @Override
    public Optional<Section> section(Integer id) {
        validateSectionExists(id);
        Section section = jdbcTemplate.queryForObject(
            "SELECT * FROM section WHERE id = ?",
            toArray(id),
            sectionRowMapper
        );
        return Optional.ofNullable(section);
    }

    @Override
    public List<Section> menuSections(Integer menuId) {
        Validate.notNull(menuId, "menu id cannot be null");
        validateMenuExists(menuId);
        return jdbcTemplate.query(
            "SELECT * FROM section WHERE menu_id = ?",
            toArray(menuId),
            sectionRowMapper
        );
    }

    @Override
    public Section create(Integer menuId, String sectionName) {
        Validate.notNull(menuId);
        Validate.notNull(sectionName, "section name cannot be null");
        Validate.notEmpty(sectionName, "section name cannot be empty");
        validateMenuExists(menuId);


        final int sectionId = idGeneratingRepository.nextId(this);

        final int updated = jdbcTemplate.update(
            "INSERT INTO section (id, name, menu_id) VALUES (?, ?, ?)",
            sectionId,
            sectionName,
            menuId
        );

        Validate.isTrue(
            updated > 0,
            "failed to create a section with name %s for menu with id %s",
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
        Validate.notNull(sectionId);

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
        Validate.notNull(name);
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

    private void validateMenuExists(Integer menuId) {
        final boolean menuExists = Objects.requireNonNull(
            jdbcTemplate.queryForObject(
                "SELECT count(*) FROM menu WHERE id = ?",
                toArray(menuId),
                Integer.class
            )
        ) > 0;

        if (!menuExists) {
            throw new NoSuchElementException(String.format(
                "menu id %s does not exist",
                menuId));
        }
    }

    private void validateSectionExists(Integer sectionId) {
        final boolean sectionExists = Objects.requireNonNull(
            jdbcTemplate.queryForObject(
            "SELECT count(*) FROM section WHERE id = ?",
            toArray(sectionId),
            Integer.class
            )
        ) > 0;

        if (!sectionExists) {
            throw new NoSuchElementException(String.format("section id %s does not exist", sectionId));
        }
    }
}
