package com.claire.firstspring.service;

import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

@Service
public class SimpleRestaurantService implements RestaurantService {

    @Override
    public List<Restaurant> list() {
        return asList(
                new SimpleRestaurant(),
                new SimpleRestaurant(),
                new SimpleRestaurant()
        );
    }
}
