package com.claire.firstspring.repository;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleRestaurantRepository implements RestaurantRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RestaurantRowMapper restaurantRowMapper;
    private final String schemaName;
    private final IdGeneratingRepository idGeneratingRepository;
    private final MenuRepository menuRepository;


    public SimpleRestaurantRepository(JdbcTemplate jdbcTemplate, RestaurantRowMapper restaurantRowMapper, String schemaName, IdGeneratingRepository idGeneratingRepository, MenuRepository menuRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.restaurantRowMapper = restaurantRowMapper;
        this.schemaName = schemaName;
        this.idGeneratingRepository = idGeneratingRepository;
        this.menuRepository = menuRepository;
    }

    @Override
    public List<Restaurant> restaurants() {
        final List<Restaurant> restaurants = jdbcTemplate.query(
            String.format("SELECT * FROM %s.restaurant LIMIT 101", schemaName),
            restaurantRowMapper
        );
        if (restaurants.size() > 100) {
            final Integer restaurantCount = jdbcTemplate.queryForObject(
                String.format("SELECT COUNT(*) FROM %s.restaurant", schemaName),
                Integer.class
            );
            throw new RuntimeException("too many rows of restaurants in database: " + restaurantCount);
        }
        return restaurants;
    }

    @Override
    public Optional<Restaurant> restaurant(Integer id) {
        List<Restaurant> listOfRestaurant = jdbcTemplate.query(
            String.format("SELECT * FROM %s.restaurant LIMIT 101", schemaName),
            restaurantRowMapper
        );
        if (listOfRestaurant.size() > 100) {
            throw new RuntimeException("too many rows of restaurants in database");
        }
        return listOfRestaurant
            .stream()
            .filter(r -> r.id().equals(id))
            .findFirst();
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        final int restaurantId = idGeneratingRepository.nextId(this);
        String name = restaurant.name();

        jdbcTemplate.update(
            String.format("INSERT INTO %s.restaurant (id, name) VALUES (?, ?)", schemaName),
            restaurantId,
            name
        );

        return new SimpleRestaurant(
            restaurantId,
            name,
            createdMenus(restaurantId, restaurant.menus())
        );
    }

    //TODO
    @Override
    public void delete(Integer restaurantId) {

    }

    private Set<Menu> createdMenus(int restaurantId, Set<Menu> menus) {
        Set<Menu> createdMenus = new HashSet<>();

        for (Menu menu : menus) {
            final Menu menuWithId = menuRepository.create(restaurantId, menu);
            createdMenus.add(menuWithId);
        }

        return createdMenus;
    }

    public Optional<Restaurant> restaurant2(Integer id) {
        Restaurant restaurant = jdbcTemplate.queryForObject(
            String.format("SELECT * FROM %s.restaurant WHERE id = ?", schemaName),
            toArray(id),
            restaurantRowMapper
        );
        return Optional.ofNullable(restaurant);
    }

    @Override
    public String getMainTableName() {
        return String.format("%s.restaurant", schemaName);
    }

    /*
    method called : queryForObject()
    number of parameters: 3
    parameters: String, Array, RowMapper

    method called: format()
    number of parameters: 2
    parameters: String, varargs Objects

    method called: toArray()
    number of parameters: 1
    parameters: varargs T

    method called: ofNullable()
    number of parameters: 1
    parameters: T

     */
}
