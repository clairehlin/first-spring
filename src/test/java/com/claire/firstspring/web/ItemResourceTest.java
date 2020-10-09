package com.claire.firstspring.web;

import com.claire.firstspring.web.model.WebItem;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.SetUtils.hashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class ItemResourceTest {

    public static final TypeReference<List<WebItem>> WEB_ITEM_LIST_TYPE_REFERENCE = new TypeReference<>() {
    };

    @Nested
    @Transactional
    class Listing extends AbstractResourceTest {

        @Test
        void can_get_an_item_with_item_id() {
            // when
            final WebItem webItem = get("/items/1", 200, WebItem.class);

            // then
            assertThat(webItem.name).contains("thai papaya salad");
            assertThat(webItem.description).contains("papaya salad in thai sauce");
            assertThat(webItem.features).contains("Keto");
        }

        @Test
        void can_get_a_list_of_items() {
            // when
            final List<WebItem> webItems = get("/items", 200, WEB_ITEM_LIST_TYPE_REFERENCE);

            // then
            assertThat(webItems.stream().map((WebItem webItem) -> webItem.name))
                .containsExactlyInAnyOrder(
                    "thai papaya salad",
                    "mango prawn salad",
                    "Italian pasta with pesto and mushrooms",
                    "low calories salad"
                );

            assertThat(webItems.stream().map((WebItem webItem) -> webItem.description))
                .contains(
                    "papaya salad in thai sauce",
                    "prawn salad with mango",
                    "pasta with pesto sauce",
                    "tomatoes and cucumbers"
                );

            assertThat(webItems.stream().map((WebItem webItem) -> webItem.price))
                .contains(23.99, 15.99);
        }

        @Test
        @Sql(statements = {
            "DELETE FROM item_feature",
            "DELETE FROM feature"
        })
        void can_get_a_list_Of_items_when_there_is_none() {
            // when/then
            final List<WebItem> webItems = get("/items", 200, WEB_ITEM_LIST_TYPE_REFERENCE);
        }

        @Test
        void fails_to_get_an_item_with_empty_item_id() {
            assertThatCode(() -> get("/items/ ", 400, WebItem.class))
                .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @Transactional
    class Updating extends AbstractResourceTest {

        @Test
        void can_update_an_item_with_item_id() {
            // given
            final WebItem webItem = get("/items/4", 200, WebItem.class);
            assertThat(webItem.name).contains("low calories salad");

            // when
            WebItem webItemNew = new WebItem();
            webItemNew.id = webItem.id;
            webItemNew.price = 23.99;
            webItemNew.name = "high protein beef soup";
            webItemNew.description = "high protein beef bone soup with vegetables";
            webItemNew.features = hashSet("Keto");

            put("/items/4", 200, webItemNew);

//             then
            final WebItem webItemAfter = get("/items/4", 200, WebItem.class);
            assertThat(webItemAfter.name).contains("high protein beef soup");
            assertThat(webItemAfter.description).contains("high protein beef bone soup with vegetables");
            assertThat(webItemAfter.price).isEqualTo(webItemNew.price);
        }

        @Test
        void can_update_a_list_of_items() {
            // given
            final Map<Integer, WebItem> webItemsBeforeUpdate = get("/items", 200, WEB_ITEM_LIST_TYPE_REFERENCE)
                .stream()
                .collect(
                    toMap(
                        wi -> wi.id,
                        Function.identity()
                    )
                );

            final Map<Integer, WebItem> webItemsToUpdate = get("/items", 200, WEB_ITEM_LIST_TYPE_REFERENCE)
                .stream()
                .collect(
                    toMap(
                        wi -> wi.id,
                        Function.identity()
                    )
                );

            webItemsToUpdate.get(1).name = webItemsToUpdate.get(1).name + 1;
            webItemsToUpdate.get(2).description = webItemsToUpdate.get(2).description + 2;
            webItemsToUpdate.get(3).price = webItemsToUpdate.get(3).price + 3;
            webItemsToUpdate.get(3).features = Set.of("Low Fat");

            // when
            final Collection<WebItem> values = webItemsToUpdate.values();
            put("/items", 200, values);

            //then
            final List<WebItem> webItemsAfterUpdate = get("/items", 200, WEB_ITEM_LIST_TYPE_REFERENCE);
            assertThat(webItemsAfterUpdate)
                .hasSize(4)
                .hasSize(values.size())
                .containsAll(values)
                .doesNotContain(
                    webItemsBeforeUpdate.get(1),
                    webItemsBeforeUpdate.get(2),
                    webItemsBeforeUpdate.get(3)
                )
                .contains(webItemsBeforeUpdate.get(4));
        }

        @Test
        void fails_to_update_non_existing_item() {
            // given
            WebItem webItem = get("/items/2", 200, WebItem.class);

            // when
            // use non existing ID
            webItem.id = webItem.id + 100;

            // then
            put("/items/" + webItem.id, 404, webItem);
        }

        @Test
        void fails_to_update_when_id_in_uri_is_different_from_body () {
            // given
            WebItem webItem = get("/items/2", 200, WebItem.class);

            // when
            // make id inconsistent in the body
            webItem.id = 3;

            // then
            put("/items/2", 400, webItem);
        }

        @Test
        void fails_to_update_a_list_of_non_existing_items() {
            // given
            final Map<Integer, WebItem> webItemsBeforeUpdate = get("/items", 200, WEB_ITEM_LIST_TYPE_REFERENCE)
                .stream()
                .collect(
                    toMap(
                        wi -> wi.id,
                        Function.identity()
                    )
                );

            List<WebItem> webItemList = get("/items", 200, WEB_ITEM_LIST_TYPE_REFERENCE);
            webItemList.get(0).id = webItemList.get(0).id + 100;
            webItemList.get(0).name = webItemList.get(0).name + 100;

            webItemList.get(3).id = webItemList.get(3).id + 300;
            webItemList.get(3).name = webItemList.get(3).name + 300;

            // when
            put("/items", 404, webItemList);


        }
    }
}

