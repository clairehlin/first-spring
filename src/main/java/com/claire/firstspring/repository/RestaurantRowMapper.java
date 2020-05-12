package com.claire.firstspring.repository;

import com.claire.firstspring.model.HolidayMenu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RestaurantRowMapper implements RowMapper<Restaurant> {
    @Override
    public Restaurant mapRow(ResultSet rs, int rowNum) throws SQLException {
        String name = rs.getString("name");
        final SimpleRestaurant simpleRestaurant = new SimpleRestaurant(name, null);
        return simpleRestaurant;
    }
}
