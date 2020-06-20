package com.claire.firstspring.repository;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleMenu;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleMenuRepository implements MenuRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MenuRowMapper menuRowMapper;
    private final IdGeneratingRepository idGeneratingRepository;
    private final String schemaName;
    private final SectionRepository sectionRepository;

    public SimpleMenuRepository(JdbcTemplate jdbcTemplate, MenuRowMapper menuRowMapper, IdGeneratingRepository idGeneratingRepository, String schemaName, SectionRepository sectionRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.menuRowMapper = menuRowMapper;
        this.idGeneratingRepository = idGeneratingRepository;
        this.schemaName = schemaName;
        this.sectionRepository = sectionRepository;
    }

    @Override
    public List<Menu> menus() {
        final List<Menu> menus = jdbcTemplate.query(
            String.format("SELECT * FROM %s.menu LIMIT 101", schemaName),
            menuRowMapper
        );
        if (menus.size() > 100) {
            final Integer menusCount = jdbcTemplate.queryForObject(
                String.format("SELECT COUNT(*) FROM %s.menu", schemaName),
                Integer.class
            );
            throw new RuntimeException("too many rows of menus in database: " + menusCount);
        }
        return menus;
    }

    @Override
    public Optional<Menu> menu(Integer menuId) {
        Menu menu = jdbcTemplate.queryForObject(
            String.format("SELECT * FROM %s.menu WHERE id = ?", schemaName),
            toArray(menuId),
            menuRowMapper
        );
        return Optional.ofNullable(menu);
    }

    @Override
    public List<Menu> restaurantMenus(Integer restaurantId) {
        return jdbcTemplate.query(
            String.format("SELECT * FROM %s.menu WHERE restaurant_id = ?", schemaName),
            toArray(restaurantId),
            menuRowMapper
        );
    }

    @Override
    public Menu create(Integer restaurantId, Menu menu) {
        final int menuId = idGeneratingRepository.nextId(this);
        String name = menu.name();

        jdbcTemplate.update(
            String.format("INSERT INTO %s.menu (id, name, restaurant_id) VALUES (?, ?, ?)", schemaName),
            menuId,
            name,
            restaurantId
        );

        return new SimpleMenu(
            menuId,
            name,
            createdSections(menuId, menu.sections())
        );
    }

    private Set<Section> createdSections(int menuId, Set<Section> sections) {
        Set<Section> createdSections = new HashSet<>();

        for (Section section : sections) {
            final Section sectionWithId = sectionRepository.create(menuId, section);
            createdSections.add(sectionWithId);
        }
        return createdSections;
    }

    @Override
    public String getMainTableName() {
        return String.format("%s.menu", schemaName);
    }
}
