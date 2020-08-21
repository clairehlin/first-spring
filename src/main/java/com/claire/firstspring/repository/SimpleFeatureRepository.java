package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleFeatureRepository implements FeatureRepository {

    private final JdbcTemplate jdbcTemplate;
    private final FeatureRowMapper featureRowMapper;
    private final IdGeneratingRepository idGeneratingRepository;

    public SimpleFeatureRepository(JdbcTemplate jdbcTemplate, FeatureRowMapper featureRowMapper, IdGeneratingRepository idGeneratingRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.featureRowMapper = featureRowMapper;
        this.idGeneratingRepository = idGeneratingRepository;
    }

    @Override
    public Set<Feature> itemFeatures(Integer itemId) {
        return Set.copyOf(
            jdbcTemplate.query(
                "SELECT feature.name AS name " +
                    "FROM feature INNER JOIN item_feature " +
                    "ON feature.id = item_feature.feature_id " +
                    "WHERE item_feature.item_id = ?",
                toArray(itemId),
                featureRowMapper
            )
        );
    }

    @Override
    public Optional<Integer> id(Feature feature) {
        return Optional.ofNullable(
            jdbcTemplate.queryForObject(
                "SELECT id FROM feature WHERE name = ?",
                toArray(feature.name()),
                Integer.class
            )
        );
    }

    @Override
    public Set<Feature> list() {
        return Set.copyOf(
            jdbcTemplate.query(
                "SELECT name FROM feature",
                featureRowMapper
            )
        );
    }

    @Override
    public void create(Feature feature) {
        int id = idGeneratingRepository.nextId(this);
        jdbcTemplate.update(
            "INSERT INTO feature (id, name) VALUES (?, ?)",
            id,
            feature.name()
        );
    }

    @Override
    public void delete(String featureName) {
        final int updated = jdbcTemplate.update(
            "DELETE FROM feature WHERE name = ?",
            featureName
        );

        if (updated < 1) {
            throw new NoSuchElementException("could not delete a feature with feature name " + featureName +
                ", perhaps it does not exist");
        }
    }

    @Override
    public void update(String currentName, String newName) {
        final int updated = jdbcTemplate.update(
            "UPDATE feature SET name = ? WHERE name = ?",
            newName,
            currentName
        );

        if (updated < 1) {
            throw new NoSuchElementException("could not update a feature with feature name " + currentName +
                ", perhaps it does not exist");
        }
    }

    @Override
    public String getMainTableName() {
        return "feature";
    }
}
