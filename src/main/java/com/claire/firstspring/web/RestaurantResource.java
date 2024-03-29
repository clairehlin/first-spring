package com.claire.firstspring.web;

import com.claire.firstspring.mappers.MenuMapper;
import com.claire.firstspring.mappers.SectionMapper;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleMenu;
import com.claire.firstspring.model.SimpleRestaurant;
import com.claire.firstspring.service.MenuService;
import com.claire.firstspring.service.RestaurantService;
import com.claire.firstspring.web.model.WebMenu;
import com.claire.firstspring.web.model.WebRestaurant;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@Transactional
public class RestaurantResource {

    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final SectionMapper sectionMapper;
    private final MenuMapper menuMapper;
    private final RestaurantMapper restaurantMapper;

    public RestaurantResource(
        RestaurantService restaurantService,
        MenuService menuService,
        SectionMapper sectionMapper,
        MenuMapper menuMapper,
        RestaurantMapper restaurantMapper)
    {
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.sectionMapper = sectionMapper;
        this.menuMapper = menuMapper;
        this.restaurantMapper = restaurantMapper;
    }

    @GetMapping
    public List<WebRestaurant> restaurants() {
        List<Restaurant> restaurantsList = restaurantService.list();
        return restaurantMapper.toSeconds(restaurantsList);
    }

    @GetMapping("/{restaurant-id}")
    public WebRestaurant restaurant(@PathVariable("restaurant-id") Integer restaurantId) {
        Restaurant restaurant = restaurantService.get(restaurantId);
        return restaurantMapper.toSecond(restaurant);
    }

    @PutMapping("/{restaurant-id}")
    public void updateRestaurant(@PathVariable("restaurant-id") Integer restaurantId,
                                 @RequestBody WebRestaurant webRestaurant) {
        Restaurant restaurant = new SimpleRestaurant(
            restaurantId,
            webRestaurant.name,
            menuMapper.toFirsts(new HashSet<>(webRestaurant.menus))
        );
        restaurantService.updateRestaurant(restaurant);
    }

    @PutMapping
    public void updateRestaurants(@RequestBody List<WebRestaurant> webRestaurants) {
        webRestaurants.forEach(webRestaurant -> this.updateRestaurant(webRestaurant.id, webRestaurant));
    }

    private void createRestaurant(@RequestBody WebRestaurant webRestaurant) {
        restaurantService.create(
            new SimpleRestaurant(
                null,
                webRestaurant.name,
                new HashSet<>(menuMapper.toFirsts(webRestaurant.menus))
            )
        );
    }

    @PostMapping
    public void createRestaurants(@RequestBody List<WebRestaurant> webRestaurants) {
        webRestaurants.forEach(this::createRestaurant);
    }

    @PostMapping("/{restaurant-id}/menus")
    public void createMenus(@PathVariable("restaurant-id") Integer restaurantId, @RequestBody List<WebMenu> webMenus) {
        webMenus.forEach(webMenu -> {
            Menu menu = new SimpleMenu(
                null,
                webMenu.name,
                sectionMapper.toFirsts(webMenu.sections)
            );
            menuService.addMenu(restaurantId, menu);
        });
    }

    @DeleteMapping("/{restaurant-id}")
    public void deleteRestaurant(@PathVariable("restaurant-id") Integer restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
    }

    @DeleteMapping
    public void deleteRestaurants(@RequestParam("ids") List<Integer> restaurantIds) {
        restaurantIds.forEach(restaurantService::deleteRestaurant);
    }
}