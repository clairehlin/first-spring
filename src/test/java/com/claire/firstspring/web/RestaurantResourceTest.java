package com.claire.firstspring.web;

import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.service.MenuService;
import com.claire.firstspring.service.RestaurantService;
import com.claire.firstspring.service.SimpleMenuService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class RestaurantResourceTest {

    @Test
    void can_get_list_of_restaurants() {
        //given
        List<Restaurant> expectedRestaurants = asList(
            new TestRestaurant(),
            new TestRestaurant()
        );
        RestaurantService restaurantService = new TestRestaurantService(expectedRestaurants);
        MenuService menuService = new TestMenuService();

        //when
        RestaurantResource restaurantResource = new RestaurantResource(restaurantService, menuService);
        final List<Restaurant> restaurants = restaurantResource.restaurants();

        //then
        assertThat(restaurants)
            .hasSize(2)
            .containsExactlyElementsOf(expectedRestaurants);
    }

    private static class TestRestaurant implements Restaurant {

        @Override
        public Integer id() {
            return null;
        }

        @Override
        public String name() {
            return null;
        }

        @Override
        public Set<Menu> menus() {
            return null;
        }
    }

    private static class TestRestaurantService implements RestaurantService {
        private final List<Restaurant> expectedRestaurants;

        public TestRestaurantService(List<Restaurant> expectedRestaurants) {
            this.expectedRestaurants = expectedRestaurants;
        }

        @Override
        public List<Restaurant> list() {
            return expectedRestaurants;
        }

        @Override
        public Restaurant get(Integer id) {
            return null;
        }

        @Override
        public Restaurant create(Restaurant restaurant) {
            throw new UnsupportedOperationException();
        }
    }

    private static class TestMenuService implements MenuService {

        @Override
        public Menu addMenu(Integer restaurantId, Menu menu) {
            return null;
        }

        @Override
        public List<Menu> list() {
            return null;
        }
    }
}