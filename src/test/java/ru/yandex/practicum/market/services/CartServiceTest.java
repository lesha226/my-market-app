package ru.yandex.practicum.market.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import ru.yandex.practicum.market.dto.*;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.entities.CartItem;
import ru.yandex.practicum.market.repositories.CartItemRepository;
import ru.yandex.practicum.market.repositories.ItemRepository;
import ru.yandex.practicum.market.repositories.OrderItemRepository;
import ru.yandex.practicum.market.repositories.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartServiceTest {

    @Autowired
    CartService service;

    @Autowired
    OrderService orderService;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll().block();
        orderItemRepository.deleteAll().block();
        orderRepository.deleteAll().block();
        itemRepository.deleteAll().block();

        execSql("insert into items(title, description, img_path, price) values('t1', 'd1', 'i1', 1)");
        execSql("insert into items(title, description, img_path, price) values('t2', 'd2', 'i2', 2)");
        execSql("insert into items(title, description, img_path, price) values('t3', 'd3', 'i3', 3)");
    }

    @Test
    void getCart() {
        execSql("""
                insert into cart_items(item_id, count)
                select id, power(10, price - 1)
                from items where title in ('t1', 't2')
                order by id""");
        CartItemDto item1 = new CartItemDto(null, "t1", "d1", "i1", 1, 1);
        CartItemDto item2 = new CartItemDto(null, "t2", "d2", "i2", 2, 10);
        CartDto dto = new CartDto(List.of(item1, item2), 21);

        CartDto result = service.getCart().block();
        assertEquals(dto, result);
    }

    @Test
    void addItemToCart() {

        Long id = queryForId("select id from items where title = 't1'");
        execSql("insert into cart_items(item_id, count) " +
                "select id, power(10, price - 1) from items where title in ('t1', 't2') order by id");
        CartItemDto item1 = new CartItemDto(null, "t1", "d1", "i1", 1, 1);
        CartItemDto item2 = new CartItemDto(null, "t2", "d2", "i2", 2, 10);
        CartDto dto;
        CartDto result;

        result = service.performActionAndGetCart(id, ItemAction.MINUS).block();
        dto = new CartDto(List.of(item2), 20);
        assertEquals(dto, result);

        result = service.performActionAndGetCart(id, ItemAction.PLUS).block();
        dto = new CartDto(List.of(item1, item2), 21);
        assertEquals(dto, result);

        result = service.performActionAndGetCart(id, ItemAction.DELETE).block();
        dto = new CartDto(List.of(item2), 20);
        assertEquals(dto, result);
    }

    @Test
    void testPerformAction() {
        Long cartItemId = queryForId("select id from items where title = 't1'");
        Integer count;

        service.performAction(cartItemId, ItemAction.PLUS).block();
        count = cartItemRepository.findById(cartItemId).map(CartItem::getCount).block();
        assertEquals(1, count);

        service.performAction(cartItemId, ItemAction.MINUS).block();
        count = cartItemRepository.findById(cartItemId).map(CartItem::getCount).block();
        assertNull(count);

        service.performAction(cartItemId, ItemAction.PLUS).block();
        service.performAction(cartItemId, ItemAction.PLUS).block();
        service.performAction(cartItemId, ItemAction.PLUS).block();
        count = cartItemRepository.findById(cartItemId).map(CartItem::getCount).block();
        assertEquals(3, count);

        service.performAction(cartItemId, ItemAction.MINUS).block();
        count = cartItemRepository.findById(cartItemId).map(CartItem::getCount).block();
        assertEquals(2, count);

        service.performAction(cartItemId, ItemAction.PLUS).block();
        count = cartItemRepository.findById(cartItemId).map(CartItem::getCount).block();
        assertEquals(3, count);

        service.performAction(cartItemId, ItemAction.DELETE).block();
        count = cartItemRepository.findById(cartItemId).map(CartItem::getCount).block();
        assertNull(count);

    }

    @Test
    void testBuy() {
        execSql("insert into cart_items(item_id, count) " +
                "select id, power(10, price - 1) from items where title in ('t1', 't2') order by id");
        OrderItemDto item1 = new OrderItemDto(null, "t1", 1, 1);
        OrderItemDto item2 = new OrderItemDto(null, "t2", 2, 10);
        OrderDto dto = new OrderDto(null, List.of(item1, item2), 21);

        Long id = service.buy().block();
        Integer cartSize = cartItemRepository.findAll().collectList().map(List::size).block();

        OrderDto result = orderService.getOrder(id).block();

        assertEquals(dto, result);
        assertEquals(0, cartSize);
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