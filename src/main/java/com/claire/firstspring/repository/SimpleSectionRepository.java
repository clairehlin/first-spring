package com.claire.firstspring.repository;

import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleSection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ArrayUtils.toArray;

@Repository
public class SimpleSectionRepository implements SectionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SectionRowMapper sectionRowMapper;
    private final String schemaName;
    private final IdGeneratingRepository idGeneratingRepository;
    private final ItemRepository itemRepository;


    public SimpleSectionRepository(JdbcTemplate jdbcTemplate, SectionRowMapper sectionRowMapper, String schemaName, IdGeneratingRepository idGeneratingRepository, ItemRepository itemRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.sectionRowMapper = sectionRowMapper;
        this.schemaName = schemaName;
        this.idGeneratingRepository = idGeneratingRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<Section> sections() {
        return jdbcTemplate.query(
            String.format("SELECT * FROM %s.sections LIMIT 101", schemaName),
            sectionRowMapper
        );
    }

    @Override
    public Optional<Section> section(Integer id) {
        Section section = jdbcTemplate.queryForObject(
            String.format("SELECT * FROM %s.section WHERE id = ?", schemaName),
            toArray(id),
            sectionRowMapper
        );
        return Optional.ofNullable(section);
    }

    @Override
    public List<Section> menuSections(Integer menuId) {
        return jdbcTemplate.query(
            String.format("SELECT * FROM %s.section WHERE menu_id = ?", schemaName),
            toArray(menuId),
            sectionRowMapper
        );

    }

    @Override
    public Section create(Integer menuId, Section section) {
        final int sectionId = idGeneratingRepository.nextId(this);
        String name = section.name();

        jdbcTemplate.update(
            String.format("INSERT INTO %s.section (id, name, menu_id) VALUES (?, ?, ?)", schemaName),
            sectionId,
            name,
            menuId
        );

        return new SimpleSection(
            sectionId,
            name,
            createdItems(sectionId, section.items())
        );
    }

    //TODO
    @Override
    public void deleteSection(Integer sectionId) {

    }

    //TODO
    @Override
    public void updateSectionName(Integer id, String name) {

    }

    private List<Item> createdItems(int sectionId, List<Item> items) {
        List<Item> createdItems = new ArrayList<>();

        for (Item item : items){
            final Item itemWithId = itemRepository.create(sectionId, item);
            createdItems.add(itemWithId);
        }
        return createdItems;
    }

    @Override
    public String getMainTableName() {
        return String.format("%s.section", schemaName);
    }
}
