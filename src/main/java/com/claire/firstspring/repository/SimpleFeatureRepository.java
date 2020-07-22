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
    private final String schemaName;
    private final IdGeneratingRepository idGeneratingRepository;

    public SimpleFeatureRepository(JdbcTemplate jdbcTemplate, FeatureRowMapper featureRowMapper, String schemaName, IdGeneratingRepository idGeneratingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.featureRowMapper = featureRowMapper;
        this.schemaName = schemaName;
        this.idGeneratingRepository = idGeneratingRepository;
    }

    @Override
    public Set<Feature> itemFeatures(Integer itemId) {
        return Set.copyOf(
            jdbcTemplate.query(
                String.format(
                    "SELECT %s.feature.name AS name " +
                    "FROM %s.feature INNER JOIN %s.item_feature " +
                    "ON %s.feature.id = %s.item_feature.feature_id " +
                    "WHERE %s.item_feature.item_id = ?",
                    schemaName,
                    schemaName,
                    schemaName,
                    schemaName,
                    schemaName,
                    schemaName
                ),
                toArray(itemId),
                featureRowMapper
            )
        );
    }

    @Override
    public Integer id(Feature feature) {
        return jdbcTemplate.queryForObject(
            String.format("SELECT id FROM %s.feature WHERE name = ?", schemaName),
            toArray(feature.name()),
            Integer.class
        );
    }

    @Override
    public Set<Feature> list() {
        return Set.copyOf(
            jdbcTemplate.query(
                String.format("SELECT name FROM %s.feature", schemaName),
                featureRowMapper
            )
        );
    }

    @Override
    public void create(Feature feature) {
        int id = idGeneratingRepository.nextId(this);
        jdbcTemplate.update(
            String.format("INSERT INTO %s.feature (id, name) VALUES (?, ?)", schemaName),
            id,
            feature.name()
        );
    }

    //TODO
    @Override
    public void delete(String featureName) {

    }

    //TODO
    @Override
    public void update(String currentName, String newName) {

    }

    @Override
    public String getMainTableName() {
        return String.format("%s.feature", schemaName);
    }
}
