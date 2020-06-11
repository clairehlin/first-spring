package com.claire.firstspring.repository;

import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleSection;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class SectionRowMapper implements RowMapper<Section> {
    private final ItemRepository itemRepository;

    public SectionRowMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Section mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        return new SimpleSection(
            id,
            name,
            itemRepository.sectionItems(id)
        );
    }
}
