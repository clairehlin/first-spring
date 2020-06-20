package com.claire.firstspring.repository;

import com.claire.firstspring.model.Feature;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.SimpleItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
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
        associatedFeatures(itemId, item.features());

        return new SimpleItem(
            itemId,
            name,
            description,
            price,
            item.features()
        );
    }

    private void associatedFeatures(Integer itemId, Set<Feature> features) {
        for (Feature feature : features) {
            final Integer featureId = featureRepository.id(feature);
            insertRowFor(itemId, featureId);
        }
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
