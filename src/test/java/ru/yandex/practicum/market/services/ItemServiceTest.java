package ru.yandex.practicum.market.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.ItemsPageDto;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
class ItemServiceTest {

    @Autowired
    ItemService service;

    @Autowired
    JdbcTemplate jdbcTemplate;

    //Templates for test
    private final String title = "Temp title";
    private final String description = "Temp description";
    private final String imgPath = "Temp imgPath";
    private final long price = 123;
    private final int count = 0;
    private final ItemDto tempItem = new ItemDto(null, title, description, imgPath, price, count);

    @Test
    void testGetItemsPage() {
        ItemDto emptyItem = new ItemDto(-1L, "", "", "", 0L, 0);
        ItemsPaging paging = new ItemsPaging(5, 1, false,false);
        ItemsPageDto dto = new ItemsPageDto(List.of(List.of(tempItem, emptyItem, emptyItem)), "", ItemsSort.NO, paging);

        jdbcTemplate.update("insert into items(title, description, img_path, price) values(?, ?, ?, ?)",
                title, description, imgPath, price);

        ItemsPageDto result = service.getItemsPage("", ItemsSort.NO, 1, 5);
        System.out.println(result);

        assertEquals(dto, result);
    }

    @Test
    void testGetItem() {
        jdbcTemplate.update("insert into items(title, description, img_path, price) values(?, ?, ?, ?)",
                title, description, imgPath, price);
        Long id = jdbcTemplate.queryForObject("select id from items order by id desc limit 1", Long.class);

        ItemDto result = service.getItem(id);

        assertEquals(tempItem, result);
    }
}