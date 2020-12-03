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

class MenuResourceTest {
    public static final TypeReference<List<WebMenu>> WEB_MENU_LIST_TYPE_REFERENCE = new TypeReference<List<WebMenu>>() {
    };
    // can update a menu
    // can update a list of menus
    // fails to update a menu with non-existing menu id
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
            WebMenu webMenuBefore = get("/menus/2", 200, WebMenu.class);
            assertThat(webMenuBefore.sections.stream().map(webSectionBefore -> webSectionBefore.name))
                .doesNotContain("Stir Fry Section");

            WebItem webItem = WebItem.of(
                null,
                "Stir Fry Chicken",
                "stir fried chicken with veg",
                12.99,
                Set.of("Keto"));

            webSection.items = List.of(webItem);

            // when
            post("/menus/2/section", 200, webSection);

            // then
            WebMenu webMenuAfter = get("/menus/2", 200, WebMenu.class);
            assertThat(webMenuAfter.sections.stream().map(webSectionAfter -> webSectionAfter.name))
                .contains("Stir Fry Section");
        }

        @Test
        void can_create_a_list_of_sections() {
            // given
            final WebSection webSection1 = new WebSection();
            webSection1.name = "Soup Section";

            WebItem webItem1 = WebItem.of(
                null,
                "kale soup",
                "kale mixed vegetable soup",
                13.99,
                Set.of("Vegetarian")
            );

            WebItem webItem2 = WebItem.of(
                null,
                "chicken soup",
                "chicken carrots soup",
                18.99,
                Set.of("Vegetarian")
            );

            webSection1.items = List.of(webItem1, webItem2);


            final WebSection webSection2 = new WebSection();
            webSection2.name = "Grill Section";

            WebItem webItem3 = WebItem.of(
                null,
                "grilled shrimp",
                "shrimp grilled in lemon butter sauce",
                23.99,
                Set.of("Keto")
            );

            webSection2.items = List.of(webItem3);

            WebMenu webMenuBefore = get("/menus/1", 200, WebMenu.class);
            assertThat(webMenuBefore.sections.stream().map(webSection -> webSection.name))
                .doesNotContain("Grill Section", "Soup Section");

            // when
            post("/menus/1/sections", 200, List.of(webSection1, webSection2));

            // thenn
            WebMenu webMenuAfter = get("/menus/1", 200, WebMenu.class);
            assertThat(webMenuAfter.sections.stream().map(webSection -> webSection.name))
                .contains("Soup Section", "Grill Section");
        }

        @Test
        void fails_to_create_a_section_with_non_existing_menu_id() {
            // given
            final WebSection webSection1 = new WebSection();
            webSection1.name = "Soup Section";

            WebItem webItem1 = WebItem.of(
                null,
                "kale soup",
                "kale mixed vegetable soup",
                13.99,
                Set.of("Vegetarian")
            );

            WebItem webItem2 = WebItem.of(
                null,
                "chicken soup",
                "chicken carrots soup",
                18.99,
                Set.of("Vegetarian")
            );

            webSection1.items = List.of(webItem1, webItem2);

            WebMenu webMenuBefore = get("/menus/1", 200, WebMenu.class);
            assertThat(webMenuBefore.sections.stream().map(webSection -> webSection.name))
                .doesNotContain("Soup Section");

            // when
            post("/sections/100/section", 404, WebSection.class);
        }

        @Test
        void fails_to_create_a_list_of_sections_with_non_existing_menu_id() {
            // given
            final WebSection webSection1 = new WebSection();
            webSection1.name = "Soup Section";

            WebItem webItem1 = WebItem.of(
                null,
                "kale soup",
                "kale mixed vegetable soup",
                13.99,
                Set.of("Vegetarian")
            );

            WebItem webItem2 = WebItem.of(
                null,
                "chicken soup",
                "chicken carrots soup",
                18.99,
                Set.of("Vegetarian")
            );

            webSection1.items = List.of(webItem1, webItem2);


            final WebSection webSection2 = new WebSection();
            webSection2.name = "Grill Section";

            WebItem webItem3 = WebItem.of(
                null,
                "grilled shrimp",
                "shrimp grilled in lemon butter sauce",
                23.99,
                Set.of("Keto")
            );

            webSection2.items = List.of(webItem3);

            WebMenu webMenuBefore = get("/menus/1", 200, WebMenu.class);
            assertThat(webMenuBefore.sections.stream().map(webSection -> webSection.name))
                .doesNotContain("Grill Section", "Soup Section");

            // when
            post("/menus/100/sections", 404, List.of(webSection1, webSection2));
        }
    }

    @Nested
    @Transactional
    class Updating extends AbstractResourceTest {
        @Test
        void can_update_a_menu_name_and_add_a_new_section() {
            // given
            WebMenu webMenu = get("/menus/1", 200, WebMenu.class);
            assertThat(webMenu.name).doesNotContain("Thanksgiving Menu");
            assertThat(webMenu.sections.size()).isEqualTo(2);

            webMenu.name = "Thanksgiving Menu";

            final WebSection webSection = new WebSection();
            webSection.name = "Soup Section";

            WebItem webItem1 = WebItem.of(
                null,
                "kale soup",
                "kale mixed vegetable soup",
                13.99,
                Set.of("Vegetarian")
            );

            WebItem webItem2 = WebItem.of(
                null,
                "chicken soup",
                "chicken carrots soup",
                18.99,
                Set.of("Vegetarian")
            );

            webSection.items = List.of(webItem1, webItem2);

            Set<WebSection> webSections = webMenu.sections;
            webSections.add(webSection);

            // when
            put("/menus/1", 200, webMenu);

            // then
            WebMenu webMenuAfter = get("/menus/1", 200, WebMenu.class);
            assertThat(webMenuAfter.name).contains("Thanksgiving Menu");
            assertThat(webMenuAfter.sections.size()).isEqualTo(3);
        }

        @Test
        void can_update_a_menu_and_remove_a_section_in_a_menu() {
            // given
            WebMenu webMenu = get("/menus/1", 200, WebMenu.class);
            assertThat(webMenu.sections.size()).isEqualTo(2);
            assertThat(webMenu.sections.stream().map(webSection -> webSection.name)).contains("Pasta Section");

            WebSection webSection = webMenu.sections.stream()
                .filter(webSectionPasta -> webSectionPasta.name.contains("Pasta Section"))
                .findFirst()
                .orElseThrow();

            webMenu.sections.remove(webSection);

            // when
            put("/menus/1", 200, webMenu);

            // then
            WebMenu webMenuAfter = get("/menus/1", 200, WebMenu.class);
            assertThat(webMenuAfter.sections.size()).isEqualTo(1);
            assertThat(webMenuAfter.sections.stream().map(webSectionAfter -> webSectionAfter.name))
                .doesNotContain("Pasta Section");

        }

        @Test
        void can_update_a_menu_with_updated_section_name() {
            // given
            WebMenu webMenu = get("/menus/2", 200, WebMenu.class);
            final WebSection poultry_section = webMenu.sections.stream()
                .filter(webSection -> webSection.name.contains("Poultry Section"))
                .findFirst()
                .orElseThrow();

            poultry_section.name = "Chicken Section";

            // when
            put("/menus/2", 200, webMenu);

            // then
            WebMenu webMenuAfter = get("/menus/2", 200, WebMenu.class);
            assertThat(webMenuAfter.sections.stream().map(webSection -> webSection.name))
                .contains("Chicken Section");


        }

        @Test
        void can_update_a_list_of_menus() {
            // given
            List<WebMenu> webMenuList = get("/menus?ids=1,2", 200, WEB_MENU_LIST_TYPE_REFERENCE);
            assertThat(webMenuList.stream().map(webMenu -> webMenu.name))
                .doesNotContain("Thanksgiving Menu");

            WebMenu webMenuFiltered = webMenuList.stream().filter(webMenu -> webMenu.id.equals(1)).findFirst().orElseThrow();
            webMenuFiltered.name = "Thanksgiving Menu";
            final Set<WebSection> sections = webMenuFiltered.sections;
            final WebSection salad_section = sections
                .stream()
                .filter(webSection -> webSection.name.contains("Salad Section"))
                .findAny()
                .orElseThrow();
            salad_section.name = "Thanksgiving Salad Section";

            WebMenu webMenuFiltered2 = webMenuList.stream().filter(webMenu -> webMenu.id.equals(2)).findFirst().orElseThrow();
            webMenuFiltered2.name = "Christmas Menu";


            // when
            put("/menus", 200, webMenuList);

            // then
            List<WebMenu> webMenuListAfter = get("/menus?ids=1,2", 200, WEB_MENU_LIST_TYPE_REFERENCE);
            assertThat(webMenuListAfter.stream().map(webMenu -> webMenu.name))
                .contains("Thanksgiving Menu", "Christmas Menu");
            final WebMenu webMenuThanksgiving = webMenuListAfter
                .stream()
                .filter(webMenu -> webMenu.name.contains("Thanksgiving Menu"))
                .findAny()
                .orElseThrow();
            assertThat(webMenuThanksgiving.sections.stream().map(webSection -> webSection.name))
                .contains("Thanksgiving Salad Section");
            assertThat(webMenuThanksgiving.sections).contains(salad_section);
        }

        @Test
        void fails_to_update_a_menu_with_non_existing_menu_id() {
            // given
            final WebMenu webMenu = get("/menus/1", 200, WebMenu.class);
            webMenu.name = "Thanksgiving Menu";

            // when
            put("/menus/100", 404, webMenu);
        }
    }

    @Nested
    @Transactional
    class Deletion extends AbstractResourceTest {

        @Test
        void can_delete_a_menu() {
            // given
            WebMenu webMenu = get("/menus/2", 200, WebMenu.class);
            assertThat(webMenu.name).contains("Holiday Menu");

            // when
            delete("/menus/2", 200);

            // then
            final List<WebMenu> webMenuList = get("/menus?ids=", 200, WEB_MENU_LIST_TYPE_REFERENCE);
            assertThat(webMenuList.stream().map(webMenuAfter -> webMenuAfter.name)).doesNotContain("Holiday Menu");
        }

        @Test
        void can_delete_a_list_of_menus() {
            // given
            final List<WebMenu> webMenuList = get("/menus?ids=1,2", 200, WEB_MENU_LIST_TYPE_REFERENCE);
            assertThat(webMenuList.stream().map(webMenu -> webMenu.name)).contains("Holiday Menu", "Simple Menu");

            // when
            delete("/menus?ids=1,2", 200);

            // then
            final List<WebMenu> webMenuListAfter = get("/menus?ids=", 200, WEB_MENU_LIST_TYPE_REFERENCE);
            assertThat(webMenuListAfter
                .stream()
                .map(webMenu -> webMenu.name))
                .doesNotContain("Holiday Menu", "Simple Menus");
        }

        @Test
        void fails_to_delete_a_menu_with_non_existing_id() {
            // given/when
            delete("/menus/100", 404);
        }

        @Test
        void fails_to_delete_a_list_of_non_existing_menu_id() {
            // given/when
            delete("/menus?ids=100,200", 404);
        }
    }
}