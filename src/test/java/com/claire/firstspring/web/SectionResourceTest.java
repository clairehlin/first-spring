package com.claire.firstspring.web;

import com.claire.firstspring.web.model.WebError;
import com.claire.firstspring.web.model.WebItem;
import com.claire.firstspring.web.model.WebSection;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SectionResourceTest {
    public static final TypeReference<List<WebSection>> WEB_SECTION_LIST_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Nested
    @Transactional
    class Listing extends AbstractResourceTest {
        @Test
        void can_get_a_section() {
            // when
            final WebSection webSection = get("/sections/1", 200, WebSection.class);

            // then
            assertThat(webSection.name).contains("Salad Section");
            assertThat(webSection.items.stream().map(webItem -> webItem.name)).contains("thai papaya salad");
        }

        @Test
        void can_get_a_list_of_sections() {
            // when
            List<WebSection> sectionList = get("/sections", 200, WEB_SECTION_LIST_TYPE_REFERENCE);

            // then
            assertThat(sectionList.stream().map(webSection -> webSection.name))
                .contains("Salad Section", "Pasta Section", "Poultry Section");

            assertThat(sectionList.stream().map(webSection -> webSection.id)).doesNotContainNull();
        }

        @Test
        void fails_to_get_a_non_existing_section() {
            // when/then
            final WebError webError = get("/sections/100", 404, WebError.class);
            assertThat(webError.errorMessage).contains("section id 100 does not exist");
        }

    }

    @Nested
    @Transactional
    class Creation extends AbstractResourceTest {

        @Test
        void can_create_an_item() {
            // given
            final String newName = "sweet corn soup";
            WebSection webSectionBefore = get("/sections/2", 200, WebSection.class);
            assertThat(webSectionBefore.items.stream().filter(wi -> wi.name.equals(newName))).hasSize(0);

            WebItem webItem = WebItem.of(
                null,
                newName,
                "sweet corn and veg in soup",
                12.99,
                Set.of("Keto", "Vegetarian")
            );

            // when
            post("/sections/2/items", 200, List.of(webItem));

            // then
            WebSection webSection = get("/sections/2", 200, WebSection.class);
            assertThat(webSection.items.stream().filter(wi -> wi.name.equals(webItem.name))).hasSize(1);
        }

        @Test
        void can_create_a_new_list_of_items() {
            // given
            WebItem webItem1 = new WebItem();
            webItem1.name = "sweet corn soup";
            webItem1.description = "sweet corn and veg in soup";
            webItem1.price = 12.99;
            webItem1.features = Set.of("Keto", "Vegetarian");

            WebItem webItem2 = new WebItem();
            webItem2.name = "chicken salad";
            webItem2.description = "sesame chicken in salad green";
            webItem2.price = 16.99;
            webItem2.features = Set.of("Keto");

            // when
            post("/sections/2/items", 200, List.of(webItem1, webItem2));

            // then
            WebSection webSection = get("/sections/2", 200, WebSection.class);
            assertThat(
                webSection.items.stream().map(webItem -> webItem.name)
            ).contains("sweet corn soup", "chicken salad");
        }

        @Test
        void fails_to_create_an_item_with_non_existing_section_id() {
            // given
            WebItem newWebItem = new WebItem();
            newWebItem.name = "beef salad";
            newWebItem.description = "beef in green";
            newWebItem.price = 23.99;
            newWebItem.features = Set.of("Keto");

            // when/then
            post("/section/100", 404, List.of(newWebItem));
        }

        @Test
        void fails_to_create_a_list_of_items_with_non_existing_section_id() {
            // given
            WebItem webItem1 = new WebItem();
            webItem1.name = "beef salad";
            webItem1.description = "beef in green";
            webItem1.price = 23.99;
            webItem1.features = Set.of("Keto");

            WebItem webItem2 = new WebItem();
            webItem2.name = "chicken salad";
            webItem2.description = "chicken in green";
            webItem2.price = 20.99;
            webItem2.features = Set.of("Keto");

            // when
            post("/section/100", 404, List.of(webItem1, webItem2));
        }

        @Test
        void fails_to_create_an_item_with_empty_section_id() {
            // given
            WebItem webItem1 = new WebItem();
            webItem1.name = "beef salad";
            webItem1.description = "beef in green";
            webItem1.price = 23.99;
            webItem1.features = Set.of("Keto");

            // when
            post("/section/", 404, List.of(webItem1));
        }

        @Test
        void fails_to_create_a_list_of_items_with_empty_section_id() {
            // given
            WebItem webItem1 = new WebItem();
            webItem1.name = "beef salad";
            webItem1.description = "beef in green";
            webItem1.price = 23.99;
            webItem1.features = Set.of("Keto");

            WebItem webItem2 = new WebItem();
            webItem2.name = "salad";
            webItem2.description = "greek green";
            webItem2.price = 13.99;
            webItem2.features = Set.of("Vegetarian");

            // when
            post("/section/", 404, List.of(webItem1, webItem2));
            post("/section/ ", 404, List.of(webItem1, webItem2));
        }
    }

    @Nested
    @Transactional
    class Updating extends AbstractResourceTest {
        @Test
        void can_update_a_section() {
            // given
            final WebSection webSection = get("/sections/2", 200, WebSection.class);
            assertThat(webSection.name).contains("Pasta Section");

            // when
            webSection.name = "Pasta with Poultry Section";
            put("/sections/2", 200, webSection);

            // then
            WebSection webSectionAfter = get("/sections/2", 200, WebSection.class);
            assertThat(webSectionAfter.name).contains("Pasta with Poultry Section");
        }

        @Test
        void can_update_a_list_of_web_sections() {
            // given
            List<WebSection> webSectionList = get("/sections", 200, WEB_SECTION_LIST_TYPE_REFERENCE);
            assertThat(webSectionList.stream().map(webSection -> webSection.name))
                .containsExactlyInAnyOrder("Salad Section", "Pasta Section", "Poultry Section");

            // when
            WebSection webSection = section1(webSectionList);
            String originalName = webSection.name;
            webSection.name = "Salad Section with Meat";
            put("/sections", 200, webSectionList);

            // then
            List<WebSection> webSectionListUpdated = get("/sections", 200, WEB_SECTION_LIST_TYPE_REFERENCE);
            assertThat(webSectionListUpdated).isEqualTo(webSectionList);
            assertThat(section1(webSectionListUpdated).name)
                .isNotEqualTo(originalName);
        }

        private WebSection section1(List<WebSection> webSectionList) {
            return webSectionList.stream().filter(ws -> ws.id.equals(1)).findFirst().orElseThrow();
        }
    }

    @Nested
    @Transactional
    class Deletion extends AbstractResourceTest {
        @Test
        void can_delete_a_section() {
            // given
            final WebSection webSection = get("/sections/2", 200, WebSection.class);

            // when
            delete("/sections/2", 200);

            // then
            List<WebSection> webSectionListAfter = get("/sections", 200, WEB_SECTION_LIST_TYPE_REFERENCE);
            assertThat(webSectionListAfter).doesNotContain(webSection);
        }

        @Test
        void can_delete_a_list_of_sections() {
            // given
            List<WebSection> webSectionList = get("/sections", 200, WEB_SECTION_LIST_TYPE_REFERENCE);

            // when
            delete("/sections?ids=1,2", 200);

            // then
            List<WebSection> webSectionListAfter = get("/sections", 200, WEB_SECTION_LIST_TYPE_REFERENCE);
            assertThat(webSectionListAfter.stream().map(webSection -> webSection.id)).doesNotContain(1,2);
        }

        @Test
        void cannot_delete_a_non_existing_section() {
            // when/then
            delete("/sections/100", 404);
        }

        @Test
        void cannot_delete_a_list_of_non_existing_section() {
            // when/then
            delete("/sections?ids=100,200", 404);
        }
    }

}