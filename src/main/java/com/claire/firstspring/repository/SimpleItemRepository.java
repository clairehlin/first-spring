package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleItemRepository implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ItemRowMapper itemRowMapper;
    private final String schemaName;
    private final IdGeneratingRepository idGeneratingRepository;
    private final FeatureRepository featureRepository;

    public SimpleItemRepository(JdbcTemplate jdbcTemplate, ItemRowMapper itemRowMapper, String schemaName, IdGeneratingRepository idGeneratingRepository, FeatureRepository featureRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.itemRowMapper = itemRowMapper;
        this.schemaName = schemaName;
        this.idGeneratingRepository = idGeneratingRepository;
        this.featureRepository = featureRepository;
    }

    @Override
    public List<Item> sectionItems(Integer sectionId) {
        return jdbcTemplate.query(
            String.format("SELECT * FROM %s.item WHERE section_id = ?", schemaName),
            toArray(sectionId),
            itemRowMapper
        );
    }

    @Override
    public Item create(Integer sectionId, Item item) {
        final int itemId = idGeneratingRepository.nextId(this);
        String name = item.name();
        String description = item.description();
        double price = item.price();

        jdbcTemplate.update(
            String.format("INSERT INTO %s.item (id, name, description, price, section_id) VALUES (?, ?, ?, ?, ?)", schemaName),
            itemId,
            name,
            description,
            price,
            sectionId
        );
        associateFeatures(itemId, item.features());

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
            String.format("SELECT * FROM %s.item", schemaName),
            itemRowMapper
        );
    }

    @Override
    public Item getItem(Integer itemId) {
        return jdbcTemplate.queryForObject(
            String.format("SELECT * FROM %s.item WHERE id = ?", schemaName),
            itemRowMapper,
            itemId
        );
    }

    @Override
    public void deleteItem(Integer itemId) {
        final int updated = jdbcTemplate.update(
            String.format("DELETE FROM %s.item WHERE id = ?", schemaName),
            itemId
        );
        if (updated < 1) {
            throw new NoSuchElementException("could not delete an item with id " + itemId + ", perhaps it does not exist");
        }
    }

    @Override
    public void updateItemIgnoringFeatures(Item item) {
        final int updated = jdbcTemplate.update(
            String.format("UPDATE %s.item SET name = ?, description = ?, price = ? WHERE id =?", schemaName),
            item.name(),
            item.description(),
            item.price(),
            item.id()
        );

        if (updated < 1) {
            throw new NoSuchElementException("could not update and item with id " + item.id() + ", perhaps it does not exist");
        }
    }

    @Override
    public void associateFeatures(Integer itemId, Set<Feature> features) {
        for (Feature feature : features) {
            final Integer featureId = featureRepository.id(feature).orElseThrow();
            insertRowFor(itemId, featureId);
        }
    }

    @Override
    public void disassociateFeatures(Integer itemId, Set<Feature> features) {
        for (Feature feature : features) {
            final Integer featureId = featureRepository.id(feature).orElseThrow();
            removeRowFor(itemId, featureId);
        }
    }

    private void removeRowFor(Integer itemId, Integer featureId) {
        jdbcTemplate.update(
            String.format("DELETE FROM %s.item_feature WHERE item_id = ? AND feature_id = ?", schemaName),
            itemId,
            featureId
        );
    }

    private void insertRowFor(Integer itemId, Integer featureId) {
        jdbcTemplate.update(
            String.format("INSERT INTO %s.item_feature (item_id, feature_id) VALUES (?, ?)", schemaName),
            itemId,
            featureId
        );
    }


    @Override
    public String getMainTableName() {
        return String.format("%s.item", schemaName);
    }
}
