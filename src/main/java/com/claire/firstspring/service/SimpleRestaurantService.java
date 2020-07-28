package com.claire.firstspring.service;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import com.claire.firstspring.repository.MenuRepository;
import com.claire.firstspring.repository.RestaurantRepository;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;


@Service
public class SimpleRestaurantService implements RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final MenuService menuService;

    public SimpleRestaurantService(
        RestaurantRepository restaurantRepository,
        MenuRepository menuRepository,
        MenuService menuService) {
        this.restaurantRepository = restaurantRepository;
        this.menuRepository = menuRepository;
        this.menuService = menuService;
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
        Validate.notNull(restaurant, "restaurant cannot be null.");
        Validate.isTrue(restaurant.id() == null, "restaurant id must be null to create a restaurant.");

        Restaurant createdRestaurant = restaurantRepository.create(restaurant.name());

        final Set<Menu> newMenus = restaurant.menus()
            .stream()
            .map(menu -> menuService.addMenu(createdRestaurant.id(), menu))
            .collect(toSet());

        return new SimpleRestaurant(createdRestaurant.id(), createdRestaurant.name(), newMenus);
    }

    @Override
    public void updateRestaurant(Restaurant restaurant) {
        final Restaurant currentRestaurant = restaurantRepository.restaurant(restaurant.id()).orElseThrow();
        Set<Menu> currentMenus = currentRestaurant.menus();

        List<Menu> removedMenus = removedMenus(restaurant.menus(), currentMenus);
        removedMenus.stream()
            .map(Menu::id)
            .forEach(menuService::deleteMenu);

        List<Menu> updatedMenus = updatedMenus(restaurant.menus());
        updatedMenus.forEach(updatedMenu -> menuService.updateMenu(updatedMenu));

        List<Menu> addedMenus = addedMenus(restaurant.menus());
        addedMenus.forEach(addedMenu -> menuService.addMenu(restaurant.id(), addedMenu));

        if (!Objects.equals(restaurant.name(), currentRestaurant.name())) {
            restaurantRepository.updateRestaurantName(restaurant.id(), restaurant.name());
        }
    }

    private List<Menu> addedMenus(Set<Menu> inputMenus) {
        return inputMenus.stream()
            .filter(item -> item.id() == null)
            .collect(toList());
    }

    private List<Menu> updatedMenus(Set<Menu> inputMenus) {
        return inputMenus.stream()
            .filter(item -> item.id() != null)
            .collect(toList());
    }

    private List<Menu> removedMenus(Set<Menu> inputMenus, Set<Menu> currentMenus) {
        List<Integer> inputMenusId = inputMenus.stream()
            .map(Menu::id)
            .filter(Objects::nonNull)
            .collect(toList());

        return currentMenus.stream()
            .filter(currentMenu -> !inputMenusId.contains(currentMenu.id()))
            .collect(toList());
    }

    @Override
    public void deleteRestaurant(Integer restaurantId) {
        Validate.notNull(restaurantId, "restaurant id cannot be null.");
        Restaurant restaurant = this.get(restaurantId);
        Set<Menu> menus = restaurant.menus();
        menus.forEach(menu -> menuService.deleteMenu(menu.id()));
        restaurantRepository.delete(restaurantId);
    }
}
