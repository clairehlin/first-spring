package com.claire.firstspring.service;

import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SimpleRestaurantService implements RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public SimpleRestaurantService(
        RestaurantRepository restaurantRepository
    ) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public List<Restaurant> list() {
        return restaurantRepository.restaurants();
    }

    @Override
    public Restaurant get(Integer id) {
        return restaurantRepository.restaurant(id)
            .orElseThrow();
    }

    @Override
    public Restaurant create(Restaurant restaurant) {
        return restaurantRepository.create(restaurant);
    }
}
