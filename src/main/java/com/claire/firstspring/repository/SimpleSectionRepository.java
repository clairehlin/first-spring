package com.claire.firstspring.repository;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Section;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleSectionRepository implements SectionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SectionRowMapper sectionRowMapper;

    public SimpleSectionRepository(JdbcTemplate jdbcTemplate, SectionRowMapper sectionRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionRowMapper = sectionRowMapper;
    }

    @Override
    public List<Section> sections() {
        final List<Section> sections = jdbcTemplate.query(
                "SELECT * FROM sections LIMIT 101",
                sectionRowMapper
        );
        return sections;
    }

//    @Override
//    public Optional<Section> section(Integer id) {
//        Section section = jdbcTemplate.queryForObject(
//                "SELECT * FROM menu WHERE id = ?",
//                toArray(id),
//                sectionRowMapper
//        );
//        return Optional.ofNullable(section);
//    }

    @Override
    public List<Section> menuSections(Integer menuId) {
        return jdbcTemplate.query(
                "SELECT * FROM section WHERE menu_id = ?",
                toArray(menuId),
                sectionRowMapper
        );

    }
}
