package com.claire.firstspring.repository;

import com.claire.firstspring.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository {
    List<Restaurant> restaurants();

    Optional<Restaurant> restaurant(Integer id);
}
