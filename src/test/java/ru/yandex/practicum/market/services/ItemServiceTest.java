package ru.yandex.practicum.market.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.ItemsPageDto;
import ru.yandex.practicum.market.dto.ItemsRequest;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.entities.Item;
import ru.yandex.practicum.market.repositories.CartItemRepository;
import ru.yandex.practicum.market.repositories.ItemRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    ItemService service;

    @Autowired
    R2dbcEntityTemplate template;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    //Templates for test
    private final String title = "Temp title";
    private final String description = "Temp description";
    private final String imgPath = "Temp imgPath";
    private final long price = 123;
    private final int count = 456;
    private final ItemDto tempItem = new ItemDto(null, title, description, imgPath, price, count);
    private final Item item = new Item(null, title, description, imgPath, price, null);

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll().block();
        itemRepository.deleteAll().block();
    }

    @Test
    void testGetItemsPage() {
        ItemDto emptyItem = new ItemDto(-1L, "", "", "", 0L, 0);
        ItemsPaging paging = new ItemsPaging(5, 1, false,false);
        ItemsPageDto dto = new ItemsPageDto(List.of(List.of(tempItem, emptyItem, emptyItem)), "", ItemsSort.NO, paging);

        itemRepository.save(item)
                .flatMap(newItem -> cartItemRepository.insert(newItem.getId(), count))
                .block();

        ItemsPageDto result = service.getItemsPage("", ItemsSort.NO, 1, 5).block();
        System.out.println("result: " + result);

        assertEquals(dto, result);
    }

    @Test
    void testGetItem() {
        Long id = itemRepository.save(item)
                .flatMap(newItem -> cartItemRepository
                        .insert(newItem.getId(), count)
                        .thenReturn(newItem.getId()))
                .block();

        ItemDto result = service.getItem(id).block();

        assertEquals(tempItem, result);
    }
}