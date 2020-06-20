package com.claire.firstspring.service;

import com.claire.firstspring.model.Restaurant;

import java.util.List;

public interface RestaurantService {
    List<Restaurant> list();

    Restaurant get(Integer id);

    Restaurant create(Restaurant restaurant);
}
