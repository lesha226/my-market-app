package ru.yandex.practicum.market.services;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
//@AutoConfigureTestDatabase
//@DataJpaTest
class CartServiceTest {

    @Autowired
    CartService service;

    @Autowired
    JdbcTemplate template;

    @Autowired
    EntityManager entityManager;

    @Test
    void getCart() {
        template.update("insert into items(title, description, img_path, price, count) values('t1', 'd1', 'i1', 1, 1)");
        template.update("insert into items(title, description, img_path, price, count) values('t2', 'd2', 'i2', 2, 10)");
        template.update("insert into items(title, description, img_path, price, count) values('t3', 'd3', 'i3', 3, 0)");
        CartItemDto item1 = new CartItemDto(null, "t1", "d1", "i1", 1, 1);
        CartItemDto item2 = new CartItemDto(null, "t2", "d2", "i2", 2, 10);
        CartDto dto = new CartDto(List.of(item1, item2), 21);

        CartDto result = service.getCart();
        assertEquals(dto, result);
    }

    @Test
    void addItemToCart() {
        template.update("insert into items(title, description, img_path, price, count) values('t1', 'd1', 'i1', 1, 1)");
        template.update("insert into items(title, description, img_path, price, count) values('t2', 'd2', 'i2', 2, 10)");
        template.update("insert into items(title, description, img_path, price, count) values('t3', 'd3', 'i3', 3, 0)");
        Long id = template.queryForObject("select id from items where title = 't1'", Long.class);
        CartItemDto item1 = new CartItemDto(null, "t1", "d1", "i1", 1, 1);
        CartItemDto item2 = new CartItemDto(null, "t2", "d2", "i2", 2, 10);
        CartDto dto;
        CartDto result;

        result = service.performActionAndGetCart(id, ItemAction.MINUS);
        dto = new CartDto(List.of(item2), 20);
        assertEquals(dto, result);

        result = service.performActionAndGetCart(id, ItemAction.PLUS);
        dto = new CartDto(List.of(item1, item2), 21);
        assertEquals(dto, result);

        result = service.performActionAndGetCart(id, ItemAction.DELETE);
        dto = new CartDto(List.of(item2), 20);
        assertEquals(dto, result);
    }

    @Test
    void testPerformAction() {
        template.update("insert into items(title, description, img_path, price, count) values('t1', 'd1', 'i1', 1, 10)");
        Long id = template.queryForObject("select id from items where title = 't1'", Long.class);
        Long count;

        service.performAction(id, ItemAction.MINUS);
        entityManager.flush();
        count = template.queryForObject("select count from items where title = 't1'", Long.class);
        assertEquals(9, count);

        service.performAction(id, ItemAction.MINUS);
        entityManager.flush();
        count = template.queryForObject("select count from items where title = 't1'", Long.class);
        assertEquals(8, count);

        service.performAction(id, ItemAction.PLUS);
        entityManager.flush();
        count = template.queryForObject("select count from items where title = 't1'", Long.class);
        assertEquals(9, count);

        service.performAction(id, ItemAction.DELETE);
        entityManager.flush();
        count = template.queryForObject("select count from items where title = 't1'", Long.class);
        assertEquals(0, count);

    }
}