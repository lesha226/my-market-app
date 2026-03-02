package ru.yandex.practicum.market.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.market.dto.OrderDto;
import ru.yandex.practicum.market.dto.OrderItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    JdbcTemplate template;

    @Autowired
    OrderService service;

    @Test
    void getOrders() {
        template.update("insert into items(title, description, img_path, price) values('t1', 'd1', 'i1', 1)");
        template.update("insert into items(title, description, img_path, price) values('t2', 'd2', 'i2', 2)");
        template.update("insert into items(title, description, img_path, price) values('t3', 'd3', 'i3', 3)");
        template.update("insert into orders() values ()");
        Long id = template.queryForObject("select id from orders order by id desc limit 1", Long.class);
        template.update("insert into order_items(order_id, item_id, count) " +
                "select ?, id, power(10, price) from items where price < 3", id);
        OrderItemDto item1 = new OrderItemDto(null , "t1", 1, 10);
        OrderItemDto item2 = new OrderItemDto(null , "t2", 2, 100);
        List<OrderDto> dto = List.of(new OrderDto(id, List.of(item1, item2), 210));

        List<OrderDto> result = service.getOrders();

        assertEquals(dto, result);
    }

    @Test
    void getOrder() {
        template.update("insert into items(title, description, img_path, price) values('t1', 'd1', 'i1', 1)");
        template.update("insert into items(title, description, img_path, price) values('t2', 'd2', 'i2', 2)");
        template.update("insert into items(title, description, img_path, price) values('t3', 'd3', 'i3', 3)");
        template.update("insert into orders() values ()");
        Long id = template.queryForObject("select id from orders order by id desc limit 1", Long.class);
        template.update("insert into order_items(order_id, item_id, count) " +
                "select ?, id, power(10, price) from items where price < 3", id);
        OrderItemDto item1 = new OrderItemDto(null , "t1", 1, 10);
        OrderItemDto item2 = new OrderItemDto(null , "t2", 2, 100);
        OrderDto dto = new OrderDto(id, List.of(item1, item2), 210);

        OrderDto result = service.getOrder(id, false);

        assertNotNull(result);
        assertEquals(dto.items().size(), result.items().size());
        assertEquals(dto.totalSum(), result.totalSum());
        assertEquals(dto.items().get(0), result.items().get(0));
        assertEquals(dto, result);
    }
}