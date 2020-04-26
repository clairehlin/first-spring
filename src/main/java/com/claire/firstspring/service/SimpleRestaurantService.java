package com.claire.firstspring.service;

import com.claire.firstspring.model.HolidayMenu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleRestaurantService implements RestaurantService {

    @Override
    public List<Restaurant> list() {
        HolidayMenu holidayMenu = new HolidayMenu();
        return List.of(
                new SimpleRestaurant(holidayMenu),
                new SimpleRestaurant(holidayMenu),
                new SimpleRestaurant(holidayMenu)
        );
    }
}
