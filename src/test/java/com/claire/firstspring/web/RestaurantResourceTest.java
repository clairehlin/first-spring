package com.claire.firstspring.web;

import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import com.claire.firstspring.service.RestaurantService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RestaurantResourceTest {

    @Test
    void can_get_list_of_restaurants() {
        //given
        List<Restaurant> expectedRestaurants = asList(
                new SimpleRestaurant(),
                new SimpleRestaurant()
        );
        RestaurantService restaurantService = new TestRestaurantService(expectedRestaurants);

        //when
        RestaurantResource restaurantResource = new RestaurantResource(restaurantService);
        final List<Restaurant> restaurants = restaurantResource.restaurants();

        //then
        assertThat(restaurants)
                .hasSize(2)
                .containsExactlyElementsOf(expectedRestaurants);
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
    }
}