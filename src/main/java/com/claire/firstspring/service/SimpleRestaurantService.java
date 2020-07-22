package com.claire.firstspring.service;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.repository.MenuRepository;
import com.claire.firstspring.repository.RestaurantRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
public class SimpleRestaurantService implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;

    public SimpleRestaurantService(
        RestaurantRepository restaurantRepository,
        MenuRepository menuRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
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

    //TODO
    @Override
    public void updateRestaurant(Restaurant restaurant) {

    }

    @Override
    public void deleteRestaurant(Integer restaurantId) {
        Validate.notNull(restaurantId, "restaurant id cannot be null.");
        Restaurant restaurant = this.get(restaurantId);
        Set<Menu> menus = restaurant.menus();
        menus.forEach(menu -> menuRepository.delete(menu.id()));
        restaurantRepository.delete(restaurantId);
    }

}
