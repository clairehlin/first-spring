package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
@Transactional
class SimpleRestaurantRepositoryTest {
    @Autowired
    SimpleRestaurantRepository simpleRestaurantRepository;


    @Test
    void can_read_restaurants_from_repo() {
        final List<Restaurant> restaurants = simpleRestaurantRepository.restaurants();
        assertThat(restaurants)
            .hasSize(2)
            .isNotEmpty()
            .extracting(Restaurant::name)
            .containsExactlyInAnyOrder("Ruth Steakhouse", "Sam Steakhouse");
    }

    @Test
    void can_return_a_restaurant_with_id() {
        assertThat(simpleRestaurantRepository.restaurant(2))
            .isNotEmpty();
    }

    @Test
    void can_create_a_restaurant() {
        //given
        Set<Menu> menus = new HashSet<>();
        SimpleRestaurant simpleRestaurant = new SimpleRestaurant(3, "Maui Cafe", menus);

        //when
        simpleRestaurantRepository.create(simpleRestaurant.name());

        //then
        final List<Restaurant> restaurants = simpleRestaurantRepository.restaurants();
        assertThat(restaurants)
            .hasSize(3)
            .extracting(Restaurant::name)
            .containsExactlyInAnyOrder("Ruth Steakhouse", "Sam Steakhouse", "Maui Cafe");
    }
}
