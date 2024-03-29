package com.claire.firstspring.repository;

import com.claire.firstspring.model.Restaurant;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends MainTableAwareRepository {
    List<Restaurant> restaurants();

    Optional<Restaurant> restaurant(Integer id);

    void delete(Integer restaurantId);

    void updateRestaurantName(Integer id, String name);

    Restaurant create(String name);
}
