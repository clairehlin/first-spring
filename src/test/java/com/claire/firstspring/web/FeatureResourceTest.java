package com.claire.firstspring.web;

import com.claire.firstspring.web.model.FeatureUpdate;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.claire.firstspring.web.model.FeatureUpdate.featureUpdate;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

class FeatureResourceTest {

    public static final TypeReference<Set<String>> STRING_SET_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Nested
    @Transactional
    class Listing extends AbstractResourceTest {
        @Test
        void can_list_features() throws Exception {
            // when
            final Set<String> features = get("/features", 200, STRING_SET_TYPE_REFERENCE);

            // then
            assertThat(features)
                .containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");
        }

        @Test
        @Sql(statements = {
            "DELETE FROM item_feature",
            "DELETE FROM feature"
        })
        void can_list_features_when_there_is_none() throws Exception {
            // when
            final Set<String> features = get("/features", 200, STRING_SET_TYPE_REFERENCE);

            // then
            assertThat(features).isEmpty();
        }

        @Test
        @Sql(statements = {
            "DELETE FROM item_feature",
            "DELETE FROM feature",
            "INSERT INTO feature (id, name) VALUES (1, 'spicy')"
        })
        void can_list_features_when_there_is_one() throws Exception {
            // when
            final Set<String> features = get("/features", 200, STRING_SET_TYPE_REFERENCE);

            // then
            assertThat(features).contains("spicy");
        }
    }

    @Nested
    @Transactional
    class Deleting extends AbstractResourceTest {
        @Test
        @Sql(statements = "INSERT INTO feature (id, name) VALUES (100, 'feature to delete');")
        void can_delete_a_feature_by_name() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).contains("feature to delete");

            // when
            delete("/features/" + "feature to delete", 200);

            // then
            final Set<String> featuresAfter = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresAfter)
                .doesNotContain("feature to delete");
        }

        @Test
        void cannot_delete_a_non_existing_feature() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore)
                .containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when
            delete("/features/Spicy", 404);
        }

        @Test
        void cannot_delete_an_empty_feature() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore)
                .containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when
            delete("/features/ ", 400);
        }
    }

    @Nested
    @Transactional
    class Creation extends AbstractResourceTest {
        @Test
        void can_create_a_feature_name() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when
            put("/features/" + "Gluten Free", 200);

            // then
            final Set<String> featuresAfter = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresAfter).contains("Gluten Free");
        }

        // TODO
        @Test
        void can_create_a_list_of_features() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

        }

        @Test
        void fails_to_create_an_empty_feature_name() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when/then
            put("/features/" + " ", 400);
        }
    }

    @Nested
    @Transactional
    class Updating extends AbstractResourceTest {
        @Test
        void can_update_a_feature_name() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when
            put("/features/" + "Low Fat/name/Gluten Free", 200);

            // then
            final Set<String> featuresAfter = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresAfter).contains("Gluten Free");
        }

        @Test
        void can_update_a_list_of_features() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when
            FeatureUpdate featureUpdate = featureUpdate("Keto", "Gluten Free");
            List<FeatureUpdate> featureUpdates = List.of(featureUpdate);

            put("/features", 200, featureUpdates);

            // then
            final Set<String> featuresAfter = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresAfter).containsExactlyInAnyOrder("Gluten Free", "Vegetarian", "Low Fat");
        }

        @Test
        void can_update_a_list_of_features_with_multiple_features() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when
            FeatureUpdate featureUpdate1 = featureUpdate("Keto", "Gluten Free");
            FeatureUpdate featureUpdate2 = featureUpdate("Vegetarian", "Spicy");
            List<FeatureUpdate> featureUpdates = List.of(featureUpdate1, featureUpdate2);

            put("/features", 200, featureUpdates);

            // then
            final Set<String> featuresAfter = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresAfter).containsExactlyInAnyOrder("Gluten Free", "Spicy", "Low Fat");
        }

        @Test
        void can_update_a_list_of_features_with_no_features() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when
            List<FeatureUpdate> featureUpdates = emptyList();

            put("/features", 200, featureUpdates);

            // then
            final Set<String> featuresAfter = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresAfter).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");
        }

        @Test
        void can_update_a_list_of_features_with_valid_names_excluding_invalid_new_name() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when
            FeatureUpdate featureUpdate1 = featureUpdate("Keto", "Gluten Free");
            FeatureUpdate featureUpdate2 = featureUpdate("Vegetarian", " ");
            List<FeatureUpdate> featureUpdates = List.of(featureUpdate1, featureUpdate2);

            put("/features", 400, featureUpdates);

            // then
            final Set<String> featuresAfter = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresAfter).containsExactlyInAnyOrder("Gluten Free", "Vegetarian", "Low Fat");
        }

        @Test
        void fails_to_update_non_existing_features() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            FeatureUpdate featureUpdate = featureUpdate("Keto2", "Gluten Free");
            List<FeatureUpdate> featureUpdates = List.of(featureUpdate);

            // when/then
            put("/features", 404, featureUpdates);
        }

        @Test
        void cannot_update_a_feature_with_empty_feature_name() throws Exception {
            // given
            final Set<String> featuresBefore = get("/features", 200, STRING_SET_TYPE_REFERENCE);
            assertThat(featuresBefore).containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");

            // when/then
            put("/features/" + "Low Fat/name/ ", 400);
        }
    }
}

