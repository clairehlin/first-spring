package com.claire.firstspring.service;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Menu;
import com.claire.firstspring.model.Restaurant;
import com.claire.firstspring.model.SimpleMenu;
import com.claire.firstspring.model.SimpleRestaurant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan({"com.claire.firstspring.repository", "com.claire.firstspring.service"})
@Import({PersistenceConfig.class})
class SimpleRestaurantServiceTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SimpleRestaurantService simpleRestaurantService;

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {
        @Test
        void can_get_a_list_of_restaurants() {
            assertThat(simpleRestaurantService.list().stream().map(Restaurant::name))
                .containsExactlyInAnyOrder("Ruth Steakhouse", "Sam Steakhouse");
        }

        @Test
        void can_get_a_restaurant_with_restaurant_id() {
            assertThat(simpleRestaurantService.get(1).name()).isEqualTo("Ruth Steakhouse");
        }

        @Test
        void fails_to_get_a_restaurant_with_empty_restaurant_id() {
            assertThatCode(() -> simpleRestaurantService.get(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_get_a_restaurant_with_non_existing_restaurant_id() {
            int restaurantId = 199;
            assertThatCode(() -> simpleRestaurantService.get(restaurantId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("restaurant id %s does not exist", restaurantId);
        }

        @Nested
        @JdbcTest
        @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
        @Import({PersistenceConfig.class})
        @Transactional
        class Creation {
            @Test
            void can_create_a_restaurant() {
                // given
                Restaurant restaurant = new SimpleRestaurant(null, "test restaurant", emptySet());

                // when
                simpleRestaurantService.create(restaurant);

                // then
                assertThat(simpleRestaurantService.list().stream().map(Restaurant::name)).contains("test restaurant");
            }

            @Test
            void fails_to_create_a_restaurant_with_empty_restaurant() {
                assertThatCode(() -> simpleRestaurantService.create(null))
                    .isInstanceOf(NullPointerException.class);
            }

            @Test
            void fails_to_create_a_restaurant_with_a_restaurant_id() {
                // given
                Restaurant restaurant = new SimpleRestaurant(1, "test restaurant", emptySet());

                // when/then
                assertThatCode(() -> simpleRestaurantService.create(restaurant))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("restaurant id must be null to create a restaurant");
            }
        }

        @Nested
        @JdbcTest
        @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
        @Import({PersistenceConfig.class})
        @Transactional
        class Updating {
            @Test
            void can_update__restaurant() {
                // given
                Restaurant restaurant = new SimpleRestaurant(1, "test restaurant", emptySet());

                // when
                simpleRestaurantService.updateRestaurant(restaurant);

                // then
                assertThat(simpleRestaurantService.get(1).name()).isEqualTo("test restaurant");
            }

            @Test
            void fails_to_update_a_restaurant_without_a_restaurant_id() {
                // given
                Restaurant restaurant = new SimpleRestaurant(null, "test restaurant", emptySet());

                // when/then
                assertThatCode(() -> simpleRestaurantService.updateRestaurant(restaurant))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("restaurant id cannot be empty");
            }

            @Test
            void fails_to_update_a_restaurant_with_an_empty_restaurant_name() {
                // given
                Restaurant restaurant = new SimpleRestaurant(1, "", emptySet());

                // when/then
                assertThatCode(() -> simpleRestaurantService.updateRestaurant(restaurant))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("restaurant name cannot be empty");
            }
        }

        @Nested
        @JdbcTest
        @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
        @Import({PersistenceConfig.class})
        @Transactional
        class Deletion {
            @Test
            void can_delete_a_restaurant() {
                // given
                final String restaurantName = simpleRestaurantService.get(1).name();

                // when
                simpleRestaurantService.deleteRestaurant(1);

                // then
                assertThat(simpleRestaurantService.list().stream().map(Restaurant::name))
                    .doesNotContain(restaurantName);
            }

            @Test
            void fails_to_delete_a_restaurant_with_non_existing_restaurant_id() {
                assertThatCode(() -> simpleRestaurantService.deleteRestaurant(100))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("restaurant id 100 does not exist");
            }

            @Test
            void fails_to_delete_a_restaurant_with_empty_restaurant_id() {
                assertThatCode(() -> simpleRestaurantService.deleteRestaurant(null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("restaurant id cannot be null");
            }
        }
    }
}