package com.claire.firstspring.web;

import com.claire.firstspring.web.model.WebError;
import com.claire.firstspring.web.model.WebItem;
import com.claire.firstspring.web.model.WebMenu;
import com.claire.firstspring.web.model.WebRestaurant;
import com.claire.firstspring.web.model.WebSection;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RestaurantResourceTest {
    public static final TypeReference<List<WebRestaurant>> WEB_RESTAURANT_LIST_TYPE_REFERENCE =
        new TypeReference<List<WebRestaurant>>() {};

    @Nested
    @Transactional
    class Listing extends AbstractResourceTest {

        @Test
        void can_get_a_restaurant() {
            // given/when
            WebRestaurant webRestaurant = get("/restaurants/1", 200, WebRestaurant.class);

            // then
            assertThat(webRestaurant.name).contains("Ruth Steakhouse");
        }

        @Test
        void can_get_a_list_of_restaurants() {
            // given/when
            List<WebRestaurant> webRestaurantList = get("/restaurants", 200, WEB_RESTAURANT_LIST_TYPE_REFERENCE);

            // then
            assertThat(webRestaurantList.stream().map(webRestaurant -> webRestaurant.name))
                .contains("Ruth Steakhouse", "Sam Steakhouse");
        }

        @Test
        void fails_to_get_a_restaurants_with_a_non_existing_id() {
            // given/when
            WebError webError = get("/restaurants/100", 404, WebError.class);
            assertThat(webError.errorMessage).contains("restaurant id 100 does not exist");
        }
    }

    @Nested
    @Transactional
    class Updating extends AbstractResourceTest {
        @Test
        void can_update_a_restaurant() {
            // given
            WebRestaurant webRestaurant = get("/restaurants/1", 200, WebRestaurant.class);
            assertThat(webRestaurant.name).contains("Ruth Steakhouse");

            // when
            webRestaurant.name = "Ruth Seafood Restaurant";
            final WebMenu webMenuBefore = webRestaurant.menus.stream().filter(webMenu -> webMenu.name.contains("Simple Menu"))
                .findFirst()
                .orElseThrow();
            webMenuBefore.name = "Thanksgiving Menu";

            put("/restaurants/1", 200, webRestaurant);

            // then
            WebRestaurant webRestaurantAfter = get("/restaurants/1", 200, WebRestaurant.class);
            assertThat(webRestaurantAfter.name).contains("Ruth Seafood Restaurant");
            assertThat(webRestaurantAfter.menus.stream().map(webMenu -> webMenu.name)).contains("Thanksgiving Menu");
        }

        @Test
        void can_update_a_list_of_restaurants() {
            // given
            List<WebRestaurant> webRestaurantList = get(
                "/restaurants",
                200,
                WEB_RESTAURANT_LIST_TYPE_REFERENCE
            );

            assertThat(webRestaurantList.stream().map(webRestaurant -> webRestaurant.name))
                .contains("Ruth Steakhouse", "Sam Steakhouse");

            // when
            final WebRestaurant webRestaurantRuth = webRestaurantList.stream().filter(webRestaurant -> webRestaurant.name.contains("Ruth Steakhouse"))
                .findAny()
                .orElseThrow();
            webRestaurantRuth.name = "Ruth Seafood Restaurant";

            final WebRestaurant webRestaurantSam = webRestaurantList.stream().filter(webRestaurant -> webRestaurant.name.contains("Sam Steakhouse"))
                .findAny()
                .orElseThrow();
            webRestaurantSam.name = "Sam Seafood Restaurant";

            put("/restaurants", 200, webRestaurantList);

            // then
            final List<WebRestaurant> webRestaurantListAfter = get(
                "/restaurants",
                200,
                WEB_RESTAURANT_LIST_TYPE_REFERENCE
            );

            assertThat(webRestaurantListAfter.stream().map(webRestaurant -> webRestaurant.name))
                .contains("Ruth Seafood Restaurant", "Sam Seafood Restaurant");
        }

        @Test
        void fails_to_update_a_restaurant_with_non_existing_id() {
            // given
            WebRestaurant webRestaurant = get("/restaurants/1", 200, WebRestaurant.class);
            webRestaurant.name = "Vaca seafood";

            // when/then
            put("/restaurants/100", 404, webRestaurant);
        }
    }

    @Nested
    @Transactional
    class Creation extends AbstractResourceTest {
        @Test
        void can_create_a_restaurant() {
            // given
            List<WebRestaurant> webRestaurantListBefore = get(
                "/restaurants",
                200,
                WEB_RESTAURANT_LIST_TYPE_REFERENCE
            );

            assertThat(webRestaurantListBefore.stream().map(webRestaurant -> webRestaurant.name))
                .doesNotContain("taste the difference");

            WebRestaurant webRestaurant = new WebRestaurant();
            webRestaurant.name = "taste the difference";
            webRestaurant.menus = Collections.emptyList();

            List<WebRestaurant> webRestaurantList = List.of(webRestaurant);

            // when
            post("/restaurants", 200, webRestaurantList);

            // then
            List<WebRestaurant> webRestaurantListAfter = get(
                "/restaurants",
                200,
                WEB_RESTAURANT_LIST_TYPE_REFERENCE
            );

            assertThat(webRestaurantListAfter.stream().map(webRestaurantAfter -> webRestaurantAfter.name))
                .contains("taste the difference");
        }

        @Test
        void can_create_a_list_of_restaurant() {
            // given
            List<WebRestaurant> webRestaurantListBefore = get(
                "/restaurants",
                200,
                WEB_RESTAURANT_LIST_TYPE_REFERENCE
            );

            assertThat(webRestaurantListBefore.stream().map(webRestaurant -> webRestaurant.name))
                .doesNotContain("taste the difference");

            // when
            WebRestaurant webRestaurant1= new WebRestaurant();
            webRestaurant1.name = "taste the difference";
            webRestaurant1.menus = Collections.emptyList();

            WebRestaurant webRestaurant2 = new WebRestaurant();
            webRestaurant2.name = "go surfing";
            webRestaurant2.menus = Collections.emptyList();

            List<WebRestaurant> webRestaurantList = List.of(webRestaurant1,webRestaurant2);

            post("/restaurants", 200, webRestaurantList);

            // then
            List<WebRestaurant> webRestaurantListAfter = get("/restaurants", 200, WEB_RESTAURANT_LIST_TYPE_REFERENCE);
            assertThat(webRestaurantListAfter.stream().map(webRestaurant -> webRestaurant.name))
                .contains("go surfing", "taste the difference");
        }

        @Test
        void can_create_menus_with_restaurant_id() {
            // given
            WebMenu webMenu1 = new WebMenu();
            webMenu1.name = "Thanksgiving Menu";
            webMenu1.sections = Collections.emptySet();

            WebMenu webMenu2 = new WebMenu();
            webMenu2.name = "New Year Menu";

            WebSection webSection = new WebSection();
            webSection.name = "Seafood Section";
            webSection.items = Collections.emptyList();

            webMenu2.sections = Set.of(webSection);

            List<WebMenu> webMenuList = List.of(webMenu1, webMenu2);

            // when
            post("/restaurants/1/menus", 200, webMenuList);

            // then
            WebRestaurant webRestaurant = get("/restaurants/1", 200, WebRestaurant.class);
            assertThat(webRestaurant.menus.stream().map(webMenu -> webMenu.name))
                .contains("Thanksgiving Menu", "New Year Menu");
        }

        @Test
        void fails_to_create_a_list_of_menus_with_non_existing_restaurant_id() {
            // given
            WebMenu webMenu = new WebMenu();
            webMenu.name = "taste the difference";
            webMenu.sections = Collections.emptySet();

            List<WebMenu> webMenuList = List.of(webMenu);

            // when
            post("/restaurants/100/menus", 404, webMenuList);
        }
    }

    @Nested
    @Transactional
    class Deletion extends AbstractResourceTest {
        @Test
        void can_delete_a_restaurant() {
            // given
            WebRestaurant webRestaurant = get("/restaurants/2", 200, WebRestaurant.class);

            // when
            delete("/restaurants/2", 200);

            // then
            List<WebRestaurant> webRestaurantList = get("/restaurants", 200, WEB_RESTAURANT_LIST_TYPE_REFERENCE);
            assertThat(webRestaurantList.stream()).doesNotContain(webRestaurant);
        }

        @Test
        void can_delete_a_list_of_restaurants() {
            // given
            List<WebRestaurant> webRestaurantList = get("/restaurants", 200, WEB_RESTAURANT_LIST_TYPE_REFERENCE);
            assertThat(webRestaurantList.stream().map(webRestaurant -> webRestaurant.name))
                .contains("Sam Steakhouse", "Ruth Steakhouse");

            // when
            delete("/restaurants?ids=1,2", 200);

            // then
            List<WebRestaurant> webRestaurantListAfter = get(
                "/restaurants",
                200,
                WEB_RESTAURANT_LIST_TYPE_REFERENCE
            );
            assertThat(webRestaurantListAfter.stream().map(webRestaurant -> webRestaurant.name))
                .doesNotContain("Sam Steakhouse", "Ruth Steakhouse");
        }

        @Test
        void fails_to_delete_a_restaurant_with_non_existing_id() {
            // given/when
            delete("/restaurants/100", 404);
        }

        @Test
        void fails_to_delete_a_list_of_restaurants_with_non_existing_id() {
            // given/when
            delete("/restaurants?ids=100,200", 404);
        }
    }

}