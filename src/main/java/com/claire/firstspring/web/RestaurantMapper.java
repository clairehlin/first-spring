package com.claire.firstspring.web;

import com.claire.firstspring.mappers.Mapper;
import com.claire.firstspring.mappers.MenuMapper;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import com.claire.firstspring.web.model.WebRestaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class RestaurantMapper implements Mapper<Restaurant, WebRestaurant> {
    private final MenuMapper menuMapper;

    public RestaurantMapper(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public Restaurant toFirst(WebRestaurant webRestaurant) {
        return new SimpleRestaurant(
            webRestaurant.id,
            webRestaurant.name,
            menuMapper.toFirsts(Set.copyOf(webRestaurant.menus))
        );
    }

    @Override
    public WebRestaurant toSecond(Restaurant restaurant) {
        WebRestaurant webRestaurant = new WebRestaurant();
        webRestaurant.id = restaurant.id();
        webRestaurant.name = restaurant.name();
        webRestaurant.menus = menuMapper.toSeconds(List.copyOf(restaurant.menus()));
        return webRestaurant;
    }
}
