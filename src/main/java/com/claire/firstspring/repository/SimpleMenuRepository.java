package com.claire.firstspring.repository;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.SimpleMenu;
import org.apache.commons.lang3.Validate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleMenuRepository implements MenuRepository {

    private final JdbcTemplate jdbcTemplate;
    private final MenuRowMapper menuRowMapper;
    private final IdGeneratingRepository idGeneratingRepository;

    public SimpleMenuRepository(JdbcTemplate jdbcTemplate, MenuRowMapper menuRowMapper, IdGeneratingRepository idGeneratingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.menuRowMapper = menuRowMapper;
        this.idGeneratingRepository = idGeneratingRepository;
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
    public Optional<Menu> menu(Integer menuId) {
        Validate.notNull(menuId);
        validateMenuExists(menuId);

        Menu menu = jdbcTemplate.queryForObject(
            "SELECT * FROM menu WHERE id = ?",
            toArray(menuId),
            menuRowMapper
        );
        return Optional.ofNullable(menu);
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
            throw new NoSuchElementException(
                String.format("menu with menu id %s does not exist in menu table", menuId)
            );
        }
    }

    @Override
    public List<Menu> restaurantMenus(Integer restaurantId) {
        Validate.notNull(restaurantId);
        validateRestaurantExists(restaurantId);
        return jdbcTemplate.query(
            "SELECT * FROM menu WHERE restaurant_id = ?",
            toArray(restaurantId),
            menuRowMapper
        );
    }

    private void validateRestaurantExists(Integer restaurantId) {
        final boolean restaurantExists = Objects.requireNonNull(
            jdbcTemplate.queryForObject(
                "SELECT count(*) FROM restaurant WHERE id = ?",
                toArray(restaurantId),
                Integer.class
            )
        ) > 0;

        if (!restaurantExists) {
            throw new NoSuchElementException(
                String.format(
                    "restaurant with restaurant id %s does not exist in restaurant able",
                    restaurantId)
            );
        }
    }

    @Override
    public Menu create(Integer restaurantId, String menuName) {
        final int menuId = idGeneratingRepository.nextId(this);
        Validate.notEmpty(menuName, "menu name cannot be empty");
        Validate.notNull(restaurantId, "restaurant id cannot be empty");
        validateRestaurantExists(restaurantId);

        jdbcTemplate.update(
            "INSERT INTO menu (id, name, restaurant_id) VALUES (?, ?, ?)",
            menuId,
            menuName,
            restaurantId
        );

        return new SimpleMenu(
            menuId,
            menuName,
            emptySet()
        );
    }

    @Override
    public void delete(Integer menuId) {
        Validate.notNull(menuId, "menu id cannot be empty");
        final int deleted = jdbcTemplate.update(
            "DELETE from menu WHERE id = ?",
            menuId
        );

        if (deleted < 1) {
            throw new NoSuchElementException("could not find menu id " + menuId);
        }

    }

    @Override
    public void updateMenuName(Integer id, String name) {
        Validate.notNull(id, "menu id cannot be empty");
        Validate.notEmpty(name, "menu name cannot be empty");
        validateMenuExists(id);

        final int updated = jdbcTemplate.update(
            "UPDATE menu SET name = ? WHERE id = ?",
            name,
            id
        );

        if (updated < 1) {
            throw new NoSuchElementException(
                String.format("could not update menu name with new name %s, perhaps menu id %s does not exist",
                    name,
                    id
                )
            );
        }
    }

    @Override
    public String getMainTableName() {
        return "menu";
    }
}
