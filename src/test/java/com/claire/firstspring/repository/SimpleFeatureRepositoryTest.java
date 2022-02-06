package com.claire.firstspring.repository;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Feature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan("com.claire.firstspring.repository")
@Import({PersistenceConfig.class})
@Transactional
class SimpleFeatureRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    SimpleFeatureRepository simpleFeatureRepository;

    @Test
    void can_read_features_from_repo() {
        final Set<Feature> features = simpleFeatureRepository.list();
        assertThat(features)
            .hasSize(3)
            .isNotEmpty()
            .extracting(Feature::name)
            .containsExactlyInAnyOrder("Keto", "Vegetarian", "Low Fat");
    }

    @Test
    void can_get_features_from_item_id() {
        final Set<Feature> features = simpleFeatureRepository.itemFeatures(1);
        assertThat(features)
            .hasSize(1)
            .extracting(feature -> feature.name())
            .containsExactly("Keto");
    }

    @Test
    void can_create_a_feature(){
        //given
        Feature newFeature = new Feature("Spicy Food");

        //when
        simpleFeatureRepository.create(newFeature);

        //then
        final Set<Feature> features =simpleFeatureRepository.list();
        assertThat(features)
            .hasSize(4)
            .extracting(Feature::name)
            .containsExactlyInAnyOrder("Keto", "Vegetarian", "Spicy Food", "Low Fat");
    }

    @Test
    @Sql(statements = "INSERT INTO feature (id, name) VALUES (100, 'feature to delete');")
    void can_delete_a_feature(){
        simpleFeatureRepository.delete("feature to delete");
        final Set<Feature> features = simpleFeatureRepository.list();
        assertThat(features).hasSize(3);
    }
}