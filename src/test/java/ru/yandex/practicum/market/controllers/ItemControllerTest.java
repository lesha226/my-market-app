package ru.yandex.practicum.market.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.ItemsPageDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.services.ItemService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ItemController.class})
class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ItemService itemService;

    @Test
    void testGetItemsPage() throws Exception {
        ItemDto[][] items = {};
        ItemsPaging paging = new ItemsPaging(4, 3, true, true);
        ItemsPageDto dto = new ItemsPageDto(items, "5", ItemsSort.ALPHA, paging);
        when(itemService.getItemsPage("1", ItemsSort.PRICE, 3, 4)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items")
                                .param("search", "1")
                                .param("sort", "PRICE")
                                .param("pageNumber", "3")
                                .param("pageSize", "4"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.model().attributeExists("items"))
                .andExpect(MockMvcResultMatchers.model().attribute("search", "5"))
                .andExpect(MockMvcResultMatchers.model().attribute("sort", "ALPHA"))
                .andExpect(MockMvcResultMatchers.model().attributeExists("paging"));

        verify(itemService).getItemsPage("1", ItemsSort.PRICE, 3, 4);
    }

    @Test
    void testAddItemToCart() throws Exception {

        mockMvc.perform(post("/items")
                        .param("id", "1")
                        .param("action", "MINUS")
                        .param("search", "1")
                        .param("sort", "PRICE")
                        .param("pageNumber", "3")
                        .param("pageSize", "4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items?search=1&sort=PRICE&pageNumber=3&pageSize=4"));

        verify(itemService).addItemToCart(1L, ItemAction.MINUS);
    }

    @Test
    void testGetItem() throws Exception {
        ItemDto dto = new ItemDto(1L, "Title", "Desc", "/images/img123.jpg", 100L, 3);

        when(itemService.getItem(1L)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", dto));

        verify(itemService).getItem(1L);
    }

    @Test
    void testAddItemToCart_ReturnItem() throws Exception {
        ItemDto dto = new ItemDto(1L, "Title", "Desc", "/images/img123.jpg", 100L, 3);

        when(itemService.addItemToCartAndReturnItem(1L, ItemAction.MINUS)).thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.post("/items/1")
                        .param("action", "MINUS"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("item"))
                .andExpect(model().attribute("item", dto));

        verify(itemService).addItemToCartAndReturnItem(1L, ItemAction.MINUS);
    }

    @Test
    void testBuy() throws Exception {

        doReturn(1L).when(itemService).buy();

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1?newOrder=true"));

        verify(itemService).buy();

    }
}