package com.claire.firstspring.web;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleMenu;
import com.claire.firstspring.model.SimpleRestaurant;
import com.claire.firstspring.service.MenuService;
import com.claire.firstspring.service.RestaurantService;
import com.claire.firstspring.web.model.WebMenu;
import com.claire.firstspring.web.model.WebRestaurant;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.emptySet;

@RestController
@RequestMapping("/restaurants")
public class RestaurantResource {

    private final RestaurantService restaurantService;
    private final MenuService menuService;

    public RestaurantResource(RestaurantService restaurantService, MenuService menuService) {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Restaurant> restaurants() {
        return restaurantService.list();
    }

    @PostMapping
    public void createRestaurant(@RequestBody WebRestaurant webRestaurant){
        restaurantService.create(new SimpleRestaurant(null, webRestaurant.name, emptySet()));
    }

    @PostMapping("/{restaurant-id}/menus")
    public void createMenu(@PathVariable("restaurant-id") Integer restaurantId, @RequestBody WebMenu webMenu) {
        Menu menu = new SimpleMenu(null, webMenu.name, emptySet());
        menuService.addMenu(restaurantId, menu);
    }
}