package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
import org.apache.commons.lang3.Validate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleItemRepository implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ItemRowMapper itemRowMapper;
    private final IdGeneratingRepository idGeneratingRepository;
    private final FeatureRepository featureRepository;

    public SimpleItemRepository(
        JdbcTemplate jdbcTemplate,
        ItemRowMapper itemRowMapper,
        IdGeneratingRepository idGeneratingRepository,
        FeatureRepository featureRepository
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.itemRowMapper = itemRowMapper;
        this.idGeneratingRepository = idGeneratingRepository;
        this.featureRepository = featureRepository;
    }

    @Override
    public List<Item> sectionItems(Integer sectionId) {
        Validate.notNull(sectionId, "section id cannot be null");
        validateSectionExists(sectionId);

        return jdbcTemplate.query(
            "SELECT * FROM item WHERE section_id = ?",
            toArray(sectionId),
            itemRowMapper
        );
    }

    private void validateSectionExists(Integer sectionId) {
        final boolean sectionExists = Objects.requireNonNull(
            jdbcTemplate.queryForObject(
                "SELECT count(*) FROM section WHERE id = ?",
                toArray(sectionId),
                Integer.class
            )
        ) > 0;
        if (!sectionExists) {
            throw new NoSuchElementException(String.format("section with id %s does not exist", sectionId));
        }
    }

    @Override
    public Item create(Integer sectionId, Item item) {
        Validate.notNull(item, "item cannot be null");
        final int itemId = idGeneratingRepository.nextId(this);
        String name = item.name();
        String description = item.description();
        double price = item.price();

        jdbcTemplate.update(
            "INSERT INTO item (id, name, description, price, section_id) VALUES (?, ?, ?, ?, ?)",
            itemId,
            name,
            description,
            price,
            sectionId
        );

        if (!item.features().isEmpty()) {
            associateFeatures(itemId, item.features());
        }

        return new SimpleItem(
            itemId,
            name,
            description,
            price,
            item.features()
        );
    }

    @Override
    public List<Item> list() {
        return jdbcTemplate.query(
            "SELECT * FROM item",
            itemRowMapper
        );
    }

    @Override
    public Item getItem(Integer itemId) {
        Validate.notNull(itemId, "item id cannot be null");
        validateItemExists(itemId);
        return jdbcTemplate.queryForObject(
            "SELECT * FROM item WHERE id = ?",
            itemRowMapper,
            itemId
        );
    }

    private void validateItemExists(Integer itemId) {
        final boolean itemExists = Objects.requireNonNull(
            jdbcTemplate.queryForObject(
                "SELECT count(*) FROM item WHERE id = ?",
                toArray(itemId),
                Integer.class
            )
        ) > 0;

        if (!itemExists) {
            throw new NoSuchElementException("item id does not exist");
        }
    }

    @Override
    public void deleteItem(Integer itemId) {
        Validate.notNull(itemId, "item id cannot be null");
        final int updated = jdbcTemplate.update(
            "DELETE FROM item WHERE id = ?",
            itemId
        );
        if (updated < 1) {
            throw new NoSuchElementException("could not delete an item with id " + itemId + ", perhaps it does not exist");
        }
    }

    @Override
    public void updateItemIgnoringFeatures(Item item) {
        Validate.notNull(item.id());
        final int updated = jdbcTemplate.update(
            "UPDATE item SET name = ?, description = ?, price = ? WHERE id =?",
            item.name(),
            item.description(),
            item.price(),
            item.id()
        );

        if (updated < 1) {
            throw new NoSuchElementException("could not update and item with id " + item.id() + ", perhaps item does not exist");
        }
    }

    @Override
    public void associateFeatures(Integer itemId, Set<Feature> features) {
        Validate.notNull(itemId);
        Validate.notEmpty(features, "features cannot be empty");
        validateFeaturesExists(features);

        for (Feature feature : features) {
            final Integer featureId = featureRepository.id(feature).orElseThrow();
            insertRowFor(itemId, featureId);
        }
    }

    private void validateFeaturesExists(Set<Feature> features) {
        for (Feature feature : features) {
            String featureName = feature.name();
            final boolean featuresExists = Objects.requireNonNull(
                jdbcTemplate.queryForObject(
                    "SELECT count(*) FROM feature WHERE name = ?",
                    toArray(featureName),
                    Integer.class
                )
            ) > 0;

            if (!featuresExists) {
                throw new NoSuchElementException(
                    String.format("feature with feature name %s does not exist", featureName)
                );
            }
        }
    }

    @Override
    public void disassociateFeatures(Integer itemId, Set<Feature> features) {
        Validate.notNull(itemId);
        Validate.notEmpty(features, "features cannot be empty");
        for (Feature feature : features) {
            final Integer featureId = featureRepository.id(feature).orElseThrow();
            removeRowFor(itemId, featureId);
        }
    }

    private void removeRowFor(Integer itemId, Integer featureId) {
        final int updated = jdbcTemplate.update(
            "DELETE FROM item_feature WHERE item_id = ? AND feature_id = ?",
            itemId,
            featureId
        );

        if (updated < 1) {
            throw new NoSuchElementException(
                String.format("cannot find matching records for parameters item id = %s, feature id = %s", itemId, featureId)
            );
        }
    }

    private void insertRowFor(Integer itemId, Integer featureId) {
        jdbcTemplate.update(
            "INSERT INTO item_feature (item_id, feature_id) VALUES (?, ?)",
            itemId,
            featureId
        );
    }


    @Override
    public String getMainTableName() {
        return "item";
    }
}
