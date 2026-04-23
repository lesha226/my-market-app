package ru.yandex.practicum.market.repositories.impl;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.entities.CartItem;
import ru.yandex.practicum.market.entities.Item;
import ru.yandex.practicum.market.repositories.ItemAndCartRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

@Repository
public class ItemAndCartRepositoryImpl implements ItemAndCartRepository {

    private final R2dbcEntityTemplate template;

    public ItemAndCartRepositoryImpl(R2dbcEntityTemplate template) {
        this.template = template;
    }

    private static final String BASE_SQL = """
            SELECT i.id, i.title, i.description, i.img_path, i.price, ic.count
            FROM items i
            LEFT JOIN cart_items ic on ic.item_id = i.id
            """;

    @Override
    public Flux<Item> findAllBySearchString(String search, ItemsSort sort, int offset, int limit) {

        StringBuilder sql = new StringBuilder(BASE_SQL);
        Map<String, Object> params = new HashMap<>();

        if (search != null && !search.isEmpty()) {
            sql.append(" WHERE i.title ILIKE '%' || :search || '%' or i.description ILIKE '%' || :search || '%'");
            params.put("search", search);
        }

        sql.append(" ORDER BY ").append(switch (sort) {
            case NO -> "i.id";
            case ALPHA -> "i.title";
            case PRICE -> "i.price";
        });

        sql.append(" limit :limit offset :offset");
        params.put("limit", limit);
        params.put("offset", offset);

        return template
                .getDatabaseClient()
                .sql(sql.toString())
                .bindValues(params)
                .map(mapper)
                .all();
    }

    @Override
    public Flux<Item> findAllByPositiveCount() {
        return template
                .getDatabaseClient()
                .sql(BASE_SQL + " where ic.count > 0 order by i.id")
                .map(mapper)
                .all();
    }

    @Override
    public Mono<Item> findById(Long id) {
        return template
                .getDatabaseClient()
                .sql(BASE_SQL + " where i.id = :id")
                .bind("id", id)
                .map(mapper)
                .one();
    }

    @Override
    public Mono<Void> increaseItemCountInCart(Long id) {
        String updateSql = """
                update cart_items
                set count = count + 1
                where item_id = :id
                  and count > 0
                """;

        String insertSql = """
                insert into cart_items(item_id, count)
                values (:id, 1)
                """;

        return template.getDatabaseClient()
                .sql(updateSql)
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .flatMap(rowCount -> {
                    if (rowCount == 1) {
                        return Mono.just(rowCount);
                    } else {
                        return template.getDatabaseClient()
                                .sql(insertSql)
                                .bind("id", id)
                                .fetch()
                                .rowsUpdated();
                    }
                })
                .then();
    }

    @Override
    public Mono<Void> decreaseItemCountInCart(Long id) {
        String updateSql = """
                update cart_items
                set count = count - 1
                where item_id = :id
                  and count > 1
                """;

        String deleteSql = """
                delete from cart_items
                where item_id = :id
                  and count <= 1
                """;

        return template.getDatabaseClient()
                .sql(updateSql)
                .bind("id", id)
                .fetch()
                .rowsUpdated()
                .flatMap(rowCount -> {
                    if (rowCount == 1) {
                        return Mono.just(rowCount);
                    } else {
                        return template.getDatabaseClient()
                                .sql(deleteSql)
                                .bind("id", id)
                                .fetch()
                                .rowsUpdated();
                    }
                })
                .then();
    }

    private final BiFunction<Row, RowMetadata, Item> mapper = (row, rowMetadata) -> {

        Long id = row.get("id", Long.class);
        Integer count = row.get("count", Integer.class);
        CartItem cartItem = null;

        if (count != null) {
            cartItem = new CartItem(id, count);
        }

        return new Item(
                id,
                row.get("title", String.class),
                row.get("description", String.class),
                row.get("img_path", String.class),
                row.get("price", Long.class),
                cartItem
        );
    };

}
