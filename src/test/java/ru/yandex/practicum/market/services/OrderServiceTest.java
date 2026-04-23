package ru.yandex.practicum.market.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.market.dto.OrderDto;
import ru.yandex.practicum.market.dto.OrderItemDto;
import ru.yandex.practicum.market.repositories.ItemRepository;
import ru.yandex.practicum.market.repositories.OrderItemRepository;
import ru.yandex.practicum.market.repositories.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    OrderService service;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll().block();
        orderRepository.deleteAll().block();
        itemRepository.deleteAll().block();

        execSql("insert into items(title, description, img_path, price) values('t1', 'd1', 'i1', 1)");
        execSql("insert into items(title, description, img_path, price) values('t2', 'd2', 'i2', 2)");
        execSql("insert into items(title, description, img_path, price) values('t3', 'd3', 'i3', 3)");
    }

    @Test
    void getOrders() {
        execSql("insert into orders() values ()");
        Long id = queryForId("select id from orders order by id desc limit 1");
        execSql(String.format("insert into order_items(order_id, item_id, count) " +
                "select %d, id, power(10, price) from items where price < 3", id));
        OrderItemDto item1 = new OrderItemDto(null , "t1", 1, 10);
        OrderItemDto item2 = new OrderItemDto(null , "t2", 2, 100);
        List<OrderDto> dto = List.of(new OrderDto(id, List.of(item1, item2), 210));

        List<OrderDto> result = service.getOrders().block();

        assertEquals(dto, result);
    }

    @Test
    void getOrder() {
        execSql("insert into orders() values ()");
        Long id = queryForId("select id from orders order by id desc limit 1");
        execSql(String.format("insert into order_items(order_id, item_id, count) " +
                "select %d, id, power(10, price) from items where price < 3", id));
        OrderItemDto item1 = new OrderItemDto(null , "t1", 1, 10);
        OrderItemDto item2 = new OrderItemDto(null , "t2", 2, 100);
        OrderDto dto = new OrderDto(id, List.of(item1, item2), 210);

        OrderDto result = service.getOrder(id).block();

        assertNotNull(result);
        assertEquals(dto.items().size(), result.items().size());
        assertEquals(dto.totalSum(), result.totalSum());
        assertEquals(dto.items().get(0), result.items().get(0));
        assertEquals(dto, result);
    }

    private void execSql(String sql) {
        template.getDatabaseClient().sql(sql).then().block();
    }

    private Long queryForId(String sql) {
        return template.getDatabaseClient().sql(sql)
                .map((row, rowMetadata) -> row.get("id", Long.class))
                .one()
                .block();
    }

}