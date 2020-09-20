package com.claire.firstspring.service;

import com.claire.firstspring.config.PersistenceConfig;
import com.claire.firstspring.model.Item;
import com.claire.firstspring.model.Section;
import com.claire.firstspring.model.SimpleItem;
import com.claire.firstspring.model.SimpleSection;
import com.claire.firstspring.repository.SimpleItemRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.util.Lists.emptyList;

@JdbcTest
@ComponentScan({"com.claire.firstspring.repository", "com.claire.firstspring.service"})
@Import({PersistenceConfig.class})
class SimpleSectionServiceTest {
    @Autowired
    SimpleSectionService simpleSectionService;

    @Autowired
    SimpleItemRepository simpleItemRepository;

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Listing {
        @Test
        void can_list_all_sections() {
            // given
            final List<Section> sections = simpleSectionService.list();

            // when/then
            assertThat(sections).extracting(Section::name).containsExactlyInAnyOrder(
                "Salad Section",
                "Pasta Section",
                "Poultry Section"
            );
        }

        @Test
        void can_get_a_section_with_section_id() {
            // given
            final Section section = simpleSectionService.getSection(1);

            // when/then
            assertThat(section.name()).contains("Salad Section");
        }

        @Test
        void fails_to_get_a_section_with_empty_section_id() {
            assertThatCode(() -> simpleSectionService.getSection(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_get_a_non_existing_section() {
            assertThatCode(() -> simpleSectionService.getSection(100))
                .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Creation {
        @Test
        void can_add_a_section() {
            // given
            Item beef = new SimpleItem(
                null,
                "stir fried beef",
                "stir fried beef with vegetables",
                12.99,
                emptySet()
            );

            Item veg = new SimpleItem(
                null,
                "stir fried vegetables",
                "stir fried vegetables in garlic sauce",
                12.99,
                emptySet()
            );

            Section section = new SimpleSection(null,
                "stir fried section",
                asList(beef, veg)
            );

            // when
            simpleSectionService.addSection(1, section);

            // then
            assertThat(simpleSectionService.list().stream().map(Section::name)).contains("stir fried section");
        }

        @Test
        void can_create_a_section_with_empty_item_list_in_section() {
            // given
            Section section = new SimpleSection(null, "beef section", emptyList());

            // when
            simpleSectionService.addSection(1, section);

            // then
            assertThat(simpleSectionService.list().stream().map(Section::name)).contains("beef section");
        }

        @Test
        void fails_to_create_a_section_with_non_empty_section_id() {
            // given
            Section section = new SimpleSection(100, "beef section", emptyList());

            // when/then
            assertThatCode(() -> simpleSectionService.addSection(1, section))
                .hasMessageContaining("section id must be null for a new section");
        }

        @Test
        void fails_to_create_a_section_with_null_menu_id() {
            // given
            Section section = new SimpleSection(null, "beef section", emptyList());

            // when/then
            assertThatCode(() -> simpleSectionService.addSection(null, section))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("menu id cannot be null");
        }

        @Test
        void fails_to_create_a_section_with_empty_section() {
            assertThatCode(() -> simpleSectionService.addSection(1, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("section cannot be null");
        }

        @Test
        void fails_to_create_a_section_with_non_existing_menu_id() {
            // given
            Section section = new SimpleSection(null, "beef section", emptyList());

            // when/then
            assertThatCode(() -> simpleSectionService.addSection(100, section))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("menu id 100 does not exist");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Updating {
        @Test
        void can_update_a_section() {
            // given
            Section newSection = new SimpleSection(1, "beef section", emptyList());

            // when
            simpleSectionService.updateSection(newSection);

            // then
            assertThat(simpleSectionService.list().stream().map(Section::name)).contains("beef section");
        }

        @Test
        void fails_to_update_a_section_with_empty_section_id() {
            // given
            Section newSection = new SimpleSection(null, "beef section", emptyList());

            // when/then
            assertThatCode(() -> simpleSectionService.updateSection(newSection))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("section id null does not exist");
        }

        @Test
        void fails_to_update_a_section_with_non_existing_section_id() {
            // given
            Section section = new SimpleSection(222, "beef section", emptyList());

            // when/then
            assertThatCode(() -> simpleSectionService.updateSection(section))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("section id 222 does not exist");
        }
    }

    @Nested
    @JdbcTest
    @Import({PersistenceConfig.class})
    @Transactional
    class Deletion {
        @Test
        void can_delete_a_section() {
            // given
            final Section section = simpleSectionService.getSection(1);
            final List<Item> items = section.items();
            final String sectionName = section.name();

            // when
            simpleSectionService.deleteSection(1);

            // then
            assertThat(simpleSectionService.list().stream().map(Section::name)).doesNotContain(sectionName);
            assertThat(items).allSatisfy(item ->
                assertThatCode(
                    () -> simpleItemRepository.getItem(item.id())
                ).isInstanceOf(NoSuchElementException.class)
            );
        }

        @Test
        void fails_to_delete_a_section_with_empty_section_id() {
            assertThatCode(() -> simpleSectionService.deleteSection(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        void fails_to_delete_a_section_with_non_existing_section_id() {
            assertThatCode(() -> simpleSectionService.deleteSection(100))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("section id 100 does not exist");
        }
    }
}