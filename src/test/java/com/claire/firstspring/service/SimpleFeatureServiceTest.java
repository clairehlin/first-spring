package com.claire.firstspring.service;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Feature;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan({"com.claire.firstspring.repository", "com.claire.firstspring.service"})
@Import({PersistenceConfig.class})
class SimpleFeatureServiceTest {
    @Autowired
    SimpleFeatureService simpleFeatureService;

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {
        @Test
        void can_list_features() {
            // given
            Feature keto = new Feature("Keto");
            Feature vegetarian = new Feature("Vegetarian");
            Feature lowFat = new Feature("Low Fat");

            // when/then
            assertThat(simpleFeatureService.list())
                .containsExactlyInAnyOrder(
                    keto,
                    vegetarian,
                    lowFat
                );
        }
    }

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    class Creation {
        @Test
        void can_create_a_new_feature() {
            // given
            Feature glutenFree = new Feature("Gluten free");

            // when
            simpleFeatureService.create(glutenFree);

            // then
            assertThat(simpleFeatureService.list()).contains(glutenFree);
        }

        @Test
        void fails_to_create_a_feature_with_empty_feature() {
            assertThatCode(() -> simpleFeatureService.create(null))
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
        void update_a_feature_name() {
            // given
            Feature newFeature = new Feature("Spicy");

            // when/then
            simpleFeatureService.updateFeature("Keto", "Spicy");

            // then
            assertThat(simpleFeatureService.list()).contains(newFeature);
        }

        @Test
        void fails_to_update_a_feature_with_empty_current_feature_name() {
            assertThatCode(() -> simpleFeatureService.updateFeature("", "Spicy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("current feature name cannot be blank");

            assertThatCode(() -> simpleFeatureService.updateFeature(null, "Spicy"))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("current feature name cannot be blank");

            assertThatCode(() -> simpleFeatureService.updateFeature(" ", "Spicy"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("current feature name cannot be blank");
        }

        @Test
        void fails_to_update_a_feature_with_empty_new_feature_name() {
            assertThatCode(() -> simpleFeatureService.updateFeature("Keto", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("new feature name cannot be blank");

            assertThatCode(() -> simpleFeatureService.updateFeature("Keto", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("new feature name cannot be blank");

            assertThatCode(() -> simpleFeatureService.updateFeature("Keto", " "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("new feature name cannot be blank");
        }
    }

    @Nested
    @JdbcTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Import({PersistenceConfig.class})
    @Transactional
    @Sql(statements = "INSERT INTO feature (id, name) VALUES (5, 'feature to delete');")
    class Deletion {
        @Test
        void can_delete_a_feature_with_feature_name() {
            // given
            Feature toDelete = new Feature("feature to delete");

            // when
            simpleFeatureService.delete("feature to delete");

            // then
            assertThat(simpleFeatureService.list()).doesNotContain(toDelete);
        }

        @Test
        void fails_to_delete_a_feature_with_empty_feature_name() {
            assertThatCode(() -> simpleFeatureService.delete(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("feature name cannot be blank");

            assertThatCode(() -> simpleFeatureService.delete(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("feature name cannot be blank");

            assertThatCode(() -> simpleFeatureService.delete(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("feature name cannot be blank");
        }
    }

}