package com.claire.firstspring.repository;

import com.claire.firstspring.model.Restaurant;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SimpleRestaurantRepository implements RestaurantRepository {

    private final JdbcTemplate jdbcTemplate;

    public SimpleRestaurantRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Restaurant> restaurants() {
        return jdbcTemplate.query(
                "SELECT * FROM restaurant",
                new RestaurantRowMapper()
        );
    }
}
