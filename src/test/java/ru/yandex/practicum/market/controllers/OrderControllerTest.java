package ru.yandex.practicum.market.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.OrderDto;
import ru.yandex.practicum.market.services.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    OrderService service;

    @Test
    void testGetOrders() throws Exception {
        ItemDto item = new ItemDto(1L, "Title", "Desc", "/images/img123.jpg", 100L, 3);
        List<ItemDto> items = List.of(item);
        List<OrderDto> dto = List.of(new OrderDto(1L, items, 100L));

        doReturn(dto).when(service).getOrders();

        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attribute("orders", dto));

        verify(service).getOrders();
    }

    @Test
    void testGetOrder() throws Exception {
        ItemDto item = new ItemDto(1L, "Title", "Desc", "/images/img123.jpg", 100L, 3);
        List<ItemDto> items = List.of(item);
        OrderDto dto = new OrderDto(1L, items, 100L);

        doReturn(dto).when(service).getOrder(1L, true);

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1")
                        .param("newOrder", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("newOrder", true))
                .andExpect(model().attribute("order", dto));

        verify(service).getOrder(1L, true);
    }
}