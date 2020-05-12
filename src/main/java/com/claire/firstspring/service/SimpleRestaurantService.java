package com.claire.firstspring.service;

import com.claire.firstspring.model.HolidayMenu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import com.claire.firstspring.repository.SimpleRestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleRestaurantService implements RestaurantService {

    @Override
    public List<Restaurant> list() {
        HolidayMenu holidayMenu = new HolidayMenu();
        return List.of(
                new SimpleRestaurant("Ruth", holidayMenu),
                new SimpleRestaurant("name", holidayMenu),
                new SimpleRestaurant("name", holidayMenu)
        );
    }
}
