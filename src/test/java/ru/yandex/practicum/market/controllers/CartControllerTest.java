package ru.yandex.practicum.market.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.services.CartService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CartService service;

    @Test
    void getItems() throws Exception {
        CartItemDto item = new CartItemDto(1L, "Title", "Desc", "/images/img123.jpg", 100L, 3);
        List<CartItemDto> items = List.of(item);
        CartDto dto = new CartDto(items, 100L);

        doReturn(dto).when(service).getCart();

        mockMvc.perform(MockMvcRequestBuilders.get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("items", items));

        verify(service).getCart();
    }

    @Test
    void addItemToCart() throws Exception {
        CartItemDto item = new CartItemDto(1L, "Title", "Desc", "/images/img123.jpg", 100L, 3);
        List<CartItemDto> items = List.of(item);
        CartDto dto = new CartDto(items, 100L);

        doReturn(dto).when(service).performActionAndGetCart(1L, ItemAction.MINUS);

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/items")
                        .param("id", "1")
                        .param("action", "MINUS"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attribute("items", items));

        verify(service).performActionAndGetCart(1L, ItemAction.MINUS);
    }
}