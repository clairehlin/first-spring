package com.claire.firstspring.web;

import com.claire.firstspring.web.model.WebError;
import com.claire.firstspring.web.model.WebItem;
import com.claire.firstspring.web.model.WebMenu;
import com.claire.firstspring.web.model.WebSection;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MenuResourceTest {
    public static final TypeReference<List<WebMenu>> WEB_MENU_LIST_TYPE_REFERENCE = new TypeReference<List<WebMenu>>() {
    };
    // can update a menu
    // can update a list of menus
    // fails to update a menu with non-existing menu id
    // can create a section
    // can create a list of sections
    // fails to create a section with non-existing menu id
    // fails to create a list of section with non-existing menu id
    // can delete a menu
    // can delete a list of menus
    // fails to delete a menu with non-existing menu id
    // fails to delete a list of menus with non-existing menu id

    @Nested
    @Transactional
    class Listing extends AbstractResourceTest {
        @Test
        void can_get_a_menu() {
            // when
            final WebMenu webMenu = get("/menus/2", 200, WebMenu.class);

            // then
            assertThat(webMenu.name).contains("Holiday Menu");
        }

        @Test
        void can_get_a_list_of_menus() {
            // when
            final List<WebMenu> webMenuList = get("/menus?ids=1,2", 200, WEB_MENU_LIST_TYPE_REFERENCE);

            // then
            assertThat(webMenuList.stream().map(webMenu -> webMenu.name)).contains("Simple Menu", "Holiday Menu");
        }

        @Test
        void fails_to_get_a_menu_with_non_existing_menu_id() {
            // when/then
            final WebError webError = get("/menus/100", 404, WebError.class);
            assertThat(webError.errorMessage).contains("menu id 100 does not exist");
        }
    }

    @Nested
    @Transactional
    class Creation extends AbstractResourceTest {
        @Test
        void can_create_a_section() {
            // given
            final WebSection webSection = new WebSection();
            webSection.name = "Stir Fry Section";

            WebItem webItem1 = new WebItem();
            webItem1.name = "Stir Fry Chicken";
            webItem1.features = Set.of("Keto");

            webSection.items = List.of(webItem1);

            // when
            post("/menus/2/section", 200, webSection);

        }
    }
}