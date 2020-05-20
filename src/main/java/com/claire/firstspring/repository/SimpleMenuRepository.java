package com.claire.firstspring.repository;

import com.claire.firstspring.model.Menu;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleMenuRepository implements MenuRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MenuRowMapper menuRowMapper;

    public SimpleMenuRepository(JdbcTemplate jdbcTemplate, MenuRowMapper menuRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.menuRowMapper = menuRowMapper;
    }

    @Override
    public List<Menu> menus() {
        final List<Menu> menus = jdbcTemplate.query(
                "SELECT * FROM menu LIMIT 101",
                menuRowMapper
        );
        if (menus.size() > 100) {
            final Integer menusCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM menu",
                    Integer.class
            );
            throw new RuntimeException("too many rows of menus in database: " + menusCount);
        }
        return menus;
    }

    @Override
    public Optional<Menu> menu(Integer id) {
        Menu menu = jdbcTemplate.queryForObject(
                "SELECT * FROM menu WHERE id = ?",
                toArray(id),
                menuRowMapper
        );
        return Optional.ofNullable(menu);
    }

    @Override
    public List<Menu> restaurantMenus(Integer restaurantId) {
        return jdbcTemplate.query(
                "SELECT * FROM menu WHERE restaurant_id = ?",
                toArray(restaurantId),
                menuRowMapper
        );
    }
}
