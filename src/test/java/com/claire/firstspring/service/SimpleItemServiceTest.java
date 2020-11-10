package com.claire.firstspring.service;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static java.util.Collections.emptySet;
import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@ComponentScan({"com.claire.firstspring.repository", "com.claire.firstspring.service"})
@Import({PersistenceConfig.class})
class SimpleItemServiceTest {
    @Autowired
    SimpleItemService simpleItemService;

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {
        @Test
        void can_get_all_items() {
            // when
            final List<Item> items = simpleItemService.list();

            // then
            assertThat(items).extracting(Item::name)
                .containsExactlyInAnyOrder(
                    "thai papaya salad",
                    "mango prawn salad",
                    "Italian pasta with pesto and mushrooms",
                    "low calories salad"
                );
        }

        @Test
        void can_get_an_item_with_item_id() {
            // when
            Item item = simpleItemService.getItem(2);

            // then
            assertThat(item.name()).contains("mango prawn salad");
        }

        @Test
        void fails_to_get_an_item_with_empty_item_id() {
            assertThatCode(() -> simpleItemService.getItem(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_get_a_non_existing_item() {
            // when/then
            assertThatCode(() -> simpleItemService.getItem(222))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("item id does not exist");
        }

    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Creation {
        // can add new items to a section
        @Test
        void can_add_new_items_to_a_section() {
            // given
            assertThat(simpleItemService.list()).extracting(Item::name).doesNotContain("kale soup");

            Feature keto = new Feature("Keto");
            Feature vegetarian = new Feature("Vegetarian");
            Set<Feature> features = Set.of(keto, vegetarian);

            SimpleItem newItem = new SimpleItem(
                null,
                "kale soup",
                "kale in creamy soup",
                12.99,
                features
            );

            // when
            simpleItemService.addNewItemToSection(2, newItem);

            // then
            assertThat(simpleItemService.list())
                .usingElementComparatorOnFields("name", "description", "price", "features")
                .contains(newItem);
        }

        @Test
        void fails_to_add_a_new_item_to_a_section_with_non_existing_feature_name() {
            // given
            Feature glutenFree = new Feature("Gluten Free");
            Feature keto = new Feature("Keto");

            Item newItem = new SimpleItem(
                null,
                "kale soup",
                "creamy kale soup",
                12.99,
                Set.of(glutenFree, keto)
            );

            // when/then
            assertThatCode(() -> simpleItemService.addNewItemToSection(2, newItem))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("feature with feature name Gluten Free does not exist");
        }

        @Test
        void fails_to_add_new_item_with_empty_section_id() {
            // given
            Feature keto = new Feature("Keto");
            Feature vegetarian = new Feature("Vegetarian");
            Set<Feature> features = Set.of(keto, vegetarian);

            Item newItem = new SimpleItem(
                null,
                "kale soup",
                "kale in creamy soup",
                12.99,
                features
            );

            // when/then
            assertThatCode(() -> simpleItemService.addNewItemToSection(null, newItem))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("section id cannot be null");
        }

        @Test
        void fails_to_add_empty_items_with_a_section_id() {
            assertThatCode(() -> simpleItemService.addNewItemToSection(2, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("item cannot be null");
        }

        @Test
        void fails_to_add_an_item_to_a_non_existing_section() {
            // given
            Item item = new SimpleItem(
                null,
                "cheese volcano",
                "cheese sticks in bread",
                12.99,
                emptySet()
            );
            // when/then
            assertThatCode(() -> simpleItemService.addNewItemToSection(222, item))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void fails_to_add_an_item_with_id_to_a_section() {
            // given
            Item item = new SimpleItem(
                10,
                "cheese volcano",
                "cheese sticks in bread",
                12.99,
                emptySet()
            );

            // when/then
            assertThatCode(() -> simpleItemService.addNewItemToSection(2, item))
                .hasMessageContaining("item id must be null");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Deletion {
        @ParameterizedTest
        @ValueSource(
            strings = {
                "item without features: 2",
                "item with features: 4"
            }
        )
        void can_delete_an_item(String testCase) {
            // given
            int itemId = parseInt(substringAfter(testCase, ": "));
            assertThat(simpleItemService.list()).extracting(Item::id).contains(itemId);

            // when
            simpleItemService.deleteItem(itemId);

            // then
            assertThat(simpleItemService.list()).extracting(Item::id).doesNotContain(itemId);
        }


        @Test
        void fails_to_delete_an_item_with_empty_item_id() {
            assertThatCode(() -> simpleItemService.deleteItem(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_delete_a_non_existing_item() {
            assertThatCode(() -> simpleItemService.deleteItem(100))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("item id does not exist");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Updating {
        @Test
        void can_update_an_item_with_some_features_changed() {
            // given
            Feature keto = new Feature("Keto");
            Feature veg = new Feature("Vegetarian");
            Set<Feature> features = Set.of(keto, veg);

            Item updatedItem = new SimpleItem(
                4,
                "texas bbq beef",
                "texas style bbq beef",
                38.99,
                features
            );

            // when
            simpleItemService.updateItem(updatedItem);

            // then
            assertThat(simpleItemService.getItem(4)).isEqualTo(updatedItem);
        }

        @Test
        void can_update_an_item_with_no_feature_changed() {
            // given
            Item item = simpleItemService.getItem(4);

            Item updatedItem = new SimpleItem(
                4,
                "texas bbq beef",
                "texas style bbq beef",
                38.99,
                item.features()
            );

            // when
            simpleItemService.updateItem(updatedItem);

            // then
            assertThat(simpleItemService.getItem(4)).isEqualTo(updatedItem);
        }

        @Test
        void can_update_an_item_with_all_features_changed() {
            // given
            Feature veg = new Feature("Vegetarian");
            Set<Feature> features = Set.of(veg);

            Item updatedItem = new SimpleItem(
                4,
                "texas bbq beef",
                "texas style bbq beef",
                38.99,
                features
            );

            // when
            simpleItemService.updateItem(updatedItem);

            // then
            assertThat(simpleItemService.getItem(4)).isEqualTo(updatedItem);
        }

        @Test
        void fails_to_update_an_item_without_id() {
            // given
            Item item = new SimpleItem(
                null,
                "beef soup",
                "beef in veg soup",
                12.99,
                emptySet()
            );

            // when/then
            assertThatCode(() -> simpleItemService.updateItem(item))
            .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void fails_to_update_a_non_existing_item() {
            // given
            Item item = new SimpleItem(
                100,
                "beef soup",
                "beef in veg soup",
                13.99,
                emptySet()
            );

            // when/then
            assertThatCode(() -> simpleItemService.updateItem(item))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageContaining("item id does not exist");
        }
    }
}   