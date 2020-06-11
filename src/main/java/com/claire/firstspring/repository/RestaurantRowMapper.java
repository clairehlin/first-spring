package com.claire.firstspring.repository;

import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Component
public class RestaurantRowMapper implements RowMapper<Restaurant> {
    private final MenuRepository menuRepository;

    public RestaurantRowMapper(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        return new SimpleRestaurant(
            id,
            name,
            Set.copyOf(menuRepository.restaurantMenus(id))
        );
    }
}
