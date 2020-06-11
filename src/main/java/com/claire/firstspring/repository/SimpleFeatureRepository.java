package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleFeatureRepository implements FeatureRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FeatureRowMapper featureRowMapper;
    private final String schema;

    public SimpleFeatureRepository(JdbcTemplate jdbcTemplate, FeatureRowMapper featureRowMapper, String schema) {
        this.jdbcTemplate = jdbcTemplate;
        this.featureRowMapper = featureRowMapper;
        this.schema = schema;
    }

    @Override
    public Set<Feature> itemFeatures(Integer itemId) {
        return Set.copyOf(
            jdbcTemplate.query(
                String.format("SELECT %s.feature.name AS name " +
                    "FROM %s.feature INNER JOIN %s.item_feature " +
                    "ON %s.feature.id = %s.item_feature.feature_id " +
                    "WHERE %s.item_feature.item_id = ?",
                    schema,
                    schema,
                    schema,
                    schema,
                    schema,
                    schema),
                toArray(itemId),
                featureRowMapper
            )
        );
    }

    @Override
    public Integer id(Feature feature) {
        return jdbcTemplate.queryForObject(
            String.format("SELECT id FROM %s.feature WHERE name = ?", schema),
            toArray(feature.name()),
            Integer.class
        );
    }
}
