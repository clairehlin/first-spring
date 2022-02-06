package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleRestaurant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
class SimpleRestaurantRepositoryTest {
    @Autowired
    SimpleRestaurantRepository simpleRestaurantRepository;

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {

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
        void fails_to_list_a_restaurant_with_empty_restaurant_id() {
            assertThatCode(() -> simpleRestaurantRepository.restaurant(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("restaurant id cannot be empty");
        }

        @Test
        void fails_to_list_a_restaurant_with_non_existing_restaurant_id() {
            assertThatCode(() -> simpleRestaurantRepository.restaurant(222))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Creation {
        @Test
        void can_create_a_restaurant() {
            //given
            Set<Menu> menus = new HashSet<>();
            SimpleRestaurant simpleRestaurant = new SimpleRestaurant(3, "Safari Cafe", menus);

            //when
            simpleRestaurantRepository.create(simpleRestaurant.name());

            //then
            final List<Restaurant> restaurants = simpleRestaurantRepository.restaurants();
            assertThat(restaurants)
                .hasSize(3)
                .extracting(Restaurant::name)
                .containsExactlyInAnyOrder("Ruth Steakhouse", "Sam Steakhouse", "Safari Cafe");
        }

        @Test
        void fails_to_create_a_restaurant_with_blank_restaurant_name() {
            assertThatCode(() -> simpleRestaurantRepository.create(""))
                .hasMessageContaining("restaurant name cannot be blank");

            assertThatCode(() -> simpleRestaurantRepository.create(null))
                .isInstanceOf(NullPointerException.class);

            assertThatCode(() -> simpleRestaurantRepository.create(" \n \t"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Deletion {
        @Test
        @Sql(statements = "INSERT INTO restaurant (id, name) VALUES (5, 'restaurant to delete');")
        void can_delete_a_restaurant_with_id() {
            // given
            simpleRestaurantRepository.delete(5);

            // when
            final List<Restaurant> restaurants = simpleRestaurantRepository.restaurants();

            // then
            assertThat(restaurants).extracting(Restaurant::name).doesNotContain("restaurant to delete");
        }

        @Test
        void fails_to_delete_a_restaurant_with_non_existing_restaurant_id() {
            assertThatCode(() -> simpleRestaurantRepository.delete(222))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void fails_to_delete_a_restaurant_with_empty_restaurant_id() {
            assertThatCode(() -> simpleRestaurantRepository.delete(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Updating {

        @Test
        void can_update_a_restaurant_name_with_restaurant_id() {
            // given
            simpleRestaurantRepository.updateRestaurantName(1, "Safari restaurant");

            // when
            final List<Restaurant> restaurants = simpleRestaurantRepository.restaurants();

            // then
            assertThat(restaurants).extracting(Restaurant::name).contains("Safari restaurant");
        }

        @Test
        void fails_to_update_a_restaurant_name_with_empty_restaurant_id() {
            assertThatCode(() -> simpleRestaurantRepository.updateRestaurantName(null, "Safari restaurant"))
            .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_update_a_restaurant_name_with_non_existing_restaurant_id() {
            assertThatCode(() -> simpleRestaurantRepository.updateRestaurantName(222, "Safari restaurant"))
                .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("restaurant id 222 does not exist");
        }

        @Test
        void fails_to_update_a_restaurant_name_with_empty_restaurant_name() {
            assertThatCode(() -> simpleRestaurantRepository.updateRestaurantName(1, null))
                .isInstanceOf(NullPointerException.class);

            assertThatCode(() -> simpleRestaurantRepository.updateRestaurantName(1, ""))
                .isInstanceOf(IllegalArgumentException.class);

            assertThatCode(() -> simpleRestaurantRepository.updateRestaurantName(1, " \n \t "))
                .isInstanceOf(IllegalArgumentException.class);
        }

    }
}
