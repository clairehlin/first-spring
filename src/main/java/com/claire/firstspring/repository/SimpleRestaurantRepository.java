package com.claire.firstspring.repository;

import com.claire.firstspring.model.Restaurant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleRestaurantRepository implements RestaurantRepository {

    private final JdbcTemplate jdbcTemplate;
    private final RestaurantRowMapper restaurantRowMapper;

    public SimpleRestaurantRepository(JdbcTemplate jdbcTemplate, RestaurantRowMapper restaurantRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.restaurantRowMapper = restaurantRowMapper;
    }

    @Override
    public List<Restaurant> restaurants() {
        final List<Restaurant> restaurants = jdbcTemplate.query(
                "SELECT * FROM restaurant LIMIT 101",
                restaurantRowMapper
        );
        if(restaurants.size() > 100) {
            final Integer restaurantCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM restaurant",
                    Integer.class
            );
            throw new RuntimeException("too many rows of restaurants in database: " + restaurantCount);
        }
        return restaurants;
    }

    @Override
    public Optional<Restaurant> restaurant(Integer id) {
        List<Restaurant> listOfRestaurant = jdbcTemplate.query(
                "SELECT * FROM restaurant LIMIT 101",
                restaurantRowMapper
        );
        if(listOfRestaurant.size() > 100) {
            throw new RuntimeException("too many rows of restaurants in database");
        }
        return listOfRestaurant
                .stream()
                .filter(r -> r.id().equals(id))
                .findFirst();
    }

    public Optional<Restaurant> restaurant2(Integer id) {
        Restaurant restaurant = jdbcTemplate.queryForObject(
                "SELECT * FROM restaurant WHERE id = ?",
                toArray(id),
                restaurantRowMapper
        );
        return Optional.ofNullable(restaurant);
    }
}
