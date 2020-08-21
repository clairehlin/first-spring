package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
class SimpleItemRepositoryTest {
    @Autowired
    SimpleItemRepository simpleItemRepository;

    @Autowired
    SimpleFeatureRepository simpleFeatureRepository;

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Deletion {
        @Test
        @Sql(statements = "INSERT INTO item (id, name, description, price, section_id) VALUES (5, 'Item to delete', 'will be deleted', 15.99, 2);")
        void can_delete_an_existing_item() {
            // given
            final List<Item> currentItems = simpleItemRepository.list();
            assertThat(currentItems).extracting(Item::id).contains(5);

            // when
            simpleItemRepository.deleteItem(5);

            // then
            final List<Item> itemsAfterDeletion = simpleItemRepository.list();
            assertThat(itemsAfterDeletion).extracting(Item::id).doesNotContain(5);
        }

        @Test
        void cannot_delete_an_existing_item_with_attached_features() {
            // when
            // then
            assertThatCode(() -> simpleItemRepository.deleteItem(3))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void rejects_non_existing_item() {
            // when
            // then
            assertThatCode(() -> simpleItemRepository.deleteItem(444))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("could not delete an item with id 444, perhaps it does not exist");
        }

        @Test
        void fails_if_item_id_is_missing() {
            // when
            // then
            assertThatCode(() -> simpleItemRepository.deleteItem(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("item id cannot be null");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {
        @Test
        void can_list_all_items() {
            // given/when
            final List<Item> currentItems = simpleItemRepository.list();

            // then
            assertThat(currentItems).extracting(Item::id).containsExactlyInAnyOrder(1, 2, 3, 4);
        }

        @Test
        void can_get_an_item_with_item_id() {
            assertThat(simpleItemRepository.getItem(1).name()).isEqualTo("thai papaya salad");
        }

        @Test
        void fail_if_item_it_is_missing() {
            assertThatCode(() -> simpleItemRepository.getItem(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("item id cannot be null");
        }

        @Test
        void can_list_items_associated_with_existing_section() {
            // given/when
            final List<Item> currentItems = simpleItemRepository.sectionItems(1);

            // then
            assertThat(currentItems).extracting(Item::id).containsExactlyInAnyOrder(1, 2);
        }

        @Test
        void gives_empty_list_for_section_without_items() {
            // given/when
            final List<Item> currentItems = simpleItemRepository.sectionItems(3);

            // then
            assertThat(currentItems).isEmpty();
        }

        @Test
        void fails_if_section_id_is_missing() {
            // when
            // then
            assertThatCode(() -> simpleItemRepository.sectionItems(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("section id cannot be null");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Creation {

        @Test
        void can_create_an_item_with_section_id() {
            // given
            Feature keto = new Feature("Keto");
            Feature vegetarian = new Feature("Vegetarian");
            Set<Feature> features = Set.of(keto, vegetarian);
            final List<Item> listBeforeCreation = simpleItemRepository.list();
            SimpleItem newItem = new SimpleItem(
                null,
                "garlic bread",
                "bread with butter and garlic",
                13.22,
                features
            );

            // when
            Item createdItem = simpleItemRepository.create(3, newItem);

            // then
            final List<Item> listAfterCreation = simpleItemRepository.list();

            assertThat(createdItem.id()).isNotNull();

            assertThat(
                newItem.withId(createdItem.id())
            )
                .isEqualTo(createdItem);

            assertThat(listAfterCreation)
                .isNotEqualTo(listBeforeCreation)
                .contains(createdItem);
        }

        @Test
        void fails_creation_section_with_null_section_id() {
            // given
            Item newItem = new SimpleItem(
                null,
                "bbq chicken",
                "chicken bbq way",
                12.99,
                emptySet()
            );

            // when
            // then
            assertThatCode(() -> simpleItemRepository.create(null, newItem))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void rejects_creation_with_non_existing_section_id() {
            // given
            Item newItem = new SimpleItem(
                null,
                "bbq chicken",
                "chicken bbq way",
                12.99,
                emptySet()
            );

            // when
            // then
            assertThatCode(() -> simpleItemRepository.create(333, newItem))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        void fails_to_create_with_missing_item() {
            // when/then
            assertThatCode(() -> simpleItemRepository.create(1, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("item cannot be null");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Updating {
        @Test
        void can_update_item_without_features() {
            // given
            Item newItem = new SimpleItem(1, "kimchi", "korean kimchi", 10.99, emptySet());
            assertThat(simpleItemRepository.getItem(1).name())
                .isNotEqualTo("kimchi");

            // when
            simpleItemRepository.updateItemIgnoringFeatures(newItem);

            // then
            final Item item = simpleItemRepository.getItem(1);
            assertThat(item.name()).isEqualTo("kimchi");
            assertThat(item.description()).isEqualTo("korean kimchi");
        }

        @Test
        void can_update_an_item_with_provided_features() {
            // given
            Feature mango = new Feature("mango");
            Feature orange = new Feature("orange");
            Set<Feature> features = Set.of(mango, orange);

            Item currentItem = simpleItemRepository.getItem(1);
            assertThat(currentItem.name()).isNotEqualTo("kimchi");
            Item newItem = new SimpleItem(1, "kimchi", "korean kimchi", 10.99, features);

            // when
            simpleItemRepository.updateItemIgnoringFeatures(newItem);

            // then
            final Item updatedItem = simpleItemRepository.getItem(1);
            assertThat(updatedItem.name()).isEqualTo("kimchi");
        }

        @Test
        void fails_to_update_with_null_item_id() {
            // given
            Feature keto = new Feature("Keto");
            Feature vegetarian = new Feature("Vegetarian");
            Set<Feature> features = Set.of(keto, vegetarian);

            Item newItem = new SimpleItem(null, "kimchi", "korean kimchi", 10.99, features);

            // when
            // then
            assertThatCode(() -> simpleItemRepository.updateItemIgnoringFeatures(newItem))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_update_item_with_missing_item() {
            // given
            // when
            // then
            assertThatCode(() -> simpleItemRepository.updateItemIgnoringFeatures(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_update_with_non_existing_item_id() {
            // given
            Feature mango = new Feature("mango");
            Feature yogurt = new Feature("yogurt");
            Set<Feature> features = Set.of(mango, yogurt);

            // when/then
            Item newItem = new SimpleItem(222, "mango", "mango lassi", 5.99, features);
            assertThatCode(() -> simpleItemRepository.updateItemIgnoringFeatures(newItem))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Associating {
        @Test
        void rejects_to_associate_with_already_associated_items_and_features() {
            // given
            Feature keto = new Feature("Keto");
            Feature lowFat = new Feature("Low Fat");
            Set<Feature> features = Set.of(keto, lowFat);

            // when/then
            assertThatCode(() -> simpleItemRepository.associateFeatures(4, features))
                .isInstanceOf(DuplicateKeyException.class);
        }

        @Test
        void rejects_to_associate_with_empty_features() {
            // when/then
            assertThatCode(() -> simpleItemRepository.associateFeatures(1, emptySet()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("features cannot be empty");
        }

        @Test
        void rejects_to_associate_with_null_features() {
            // given
            // when/then
            assertThatCode(() -> simpleItemRepository.associateFeatures(1, null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void rejects_to_associate_with_one_associated_feature_and_one_non_associated_feature() {
            // given
            Feature keto = new Feature("Keto");
            Feature vegetarian = new Feature("Vegetarian");
            Set<Feature> features = Set.of(keto, vegetarian);

            // when/then
            assertThatCode(() -> simpleItemRepository.associateFeatures(4, features))
                .isInstanceOf(DuplicateKeyException.class)
                .hasMessageContaining("Unique index");
        }

        @Test
        void can_associate_item_id_with_existing_features() {
            // given
            //features unassociated with item
            // item without features
            int idOfItem = 2;
            Feature keto = new Feature("Keto");
            Feature vegetarian = new Feature("Vegetarian");
            Set<Feature> features = Set.of(keto, vegetarian);
            assertThat(simpleFeatureRepository.itemFeatures(idOfItem))
                .doesNotContain(keto);

            // when
            // associate items with features
            simpleItemRepository.associateFeatures(idOfItem, features);

            // then
            // confirm association succeeded
            assertThat(simpleFeatureRepository.itemFeatures(idOfItem))
                .contains(keto);
        }

        @Test
        void rejects_to_associate_null_item_id_with_features() {
            // given
            Feature keto = new Feature("Keto");
            Feature vegetarian = new Feature("Vegetarian");
            Set<Feature> features = Set.of(keto, vegetarian);

            // when/then
            assertThatCode(() -> simpleItemRepository.associateFeatures(null, features))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void rejects_to_associate_item_id_with_non_existing_features() {
            // given
            Feature feature = new Feature("sour");
            Set<Feature> features = Set.of(feature);

            // when/then
            assertThatCode(() -> simpleItemRepository.associateFeatures(1, features))
                .isInstanceOf(EmptyResultDataAccessException.class);
        }
    }


    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Disassociating {
        @Test
        void can_disassociate_features_with_item_id_and_features() {
            // given
            Feature keto = new Feature("Keto");
            Feature lowFat = new Feature("Low Fat");
            Set<Feature> features = Set.of(keto, lowFat);

            assertThat(simpleFeatureRepository.itemFeatures(4))
                .contains(keto, lowFat);

            // when
            simpleItemRepository.disassociateFeatures(4, features);

            // then
            assertThat(simpleFeatureRepository.itemFeatures(4))
                .doesNotContain(keto, lowFat);
        }

        @Test
        void rejects_to_disassociate_if_no_relationship_between_item_id_and_features() {
            // given
            Feature veg = new Feature("Vegetarian");
            Set<Feature> features = Set.of(veg);

            assertThat(simpleFeatureRepository.list())
                .contains(veg);
            assertThat(simpleFeatureRepository.itemFeatures(1))
                .doesNotContain(veg);

            // when/then
            assertThatCode(() -> simpleItemRepository.disassociateFeatures(1, features))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageStartingWith("cannot find matching records for parameters item");
        }

        @Test
        void rejects_to_disassociate_with_null_item_id() {
            // given
            Feature keto = new Feature("Keto");
            Set<Feature> features = Set.of(keto);

            // when/then
            assertThatCode(() -> simpleItemRepository.disassociateFeatures(null, features))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void rejects_to_disassociate_with_non_existing_features() {
            // given
            Feature sour = new Feature("sour");
            Set<Feature> features = Set.of(sour);

            // when/then
            assertThatCode(() -> simpleItemRepository.disassociateFeatures(1, features))
                .isInstanceOf(EmptyResultDataAccessException.class);
        }

        @Test
        void rejects_to_disassociate_with_non_existing_item_and_existing_features() {
            // given
            Feature keto = new Feature("Keto");
            Set<Feature> features = Set.of(keto);

            // when/then
            assertThatCode(() -> simpleItemRepository.disassociateFeatures(222, features))
                .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        void rejects_to_disassociate_with_existing_item_and_non_existing_features() {
            // given
            Feature keto = new Feature("Keto");
            Feature orange = new Feature("orange");
            Set<Feature> features = Set.of(keto, orange);

            // when/then
            assertThatCode(() -> simpleItemRepository.disassociateFeatures(4, features))
                .isInstanceOf(EmptyResultDataAccessException.class);
        }

        @Test
        void rejects_to_disassociate_with_empty_features() {
            // given/when/then
            assertThatCode(() -> simpleItemRepository.disassociateFeatures(4, emptySet()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("features cannot be empty");
        }

        @Test
        void rejects_to_disassociate_with_null_features() {
            // given/when/then
            assertThatCode(() -> simpleItemRepository.disassociateFeatures(4, null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void rejects_to_disassociate_with_one_already_disassociated_features_and_one_associated_features() {
            // given
            Feature vegetarian = new Feature("Vegetarian");
            Feature keto = new Feature("Keto");
            Set<Feature> features = Set.of(vegetarian, keto);

            // when/then
            assertThatCode(() -> simpleItemRepository.disassociateFeatures(4, features))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("cannot find matching record");
        }
    }
}