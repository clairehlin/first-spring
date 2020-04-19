package com.claire.firstspring.web;

import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.service.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantResource {

    private final RestaurantService restaurantService;

    public RestaurantResource(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    public List<Restaurant> restaurants() {
        return restaurantService.list();
    }
}