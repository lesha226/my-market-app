package ru.yandex.practicum.market.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.OrderDto;
import ru.yandex.practicum.market.dto.OrderItemDto;
import ru.yandex.practicum.market.services.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebFluxTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    OrderService service;

    @Test
    void testGetOrders() throws Exception {
        OrderItemDto item = new OrderItemDto(1L, "Title of itemDto1", 10L, 456);
        List<OrderItemDto> items = List.of(item);
        List<OrderDto> dto = List.of(new OrderDto(1L, items, 789L));

        doReturn(Mono.just(dto)).when(service).getOrders();

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Title of itemDto1");
                    assert html.contains("456");
                    assert html.contains("4560");
                    assert html.contains("789");
                });

        verify(service).getOrders();
    }

    @Test
    void testGetOrder() throws Exception {
        OrderItemDto item = new OrderItemDto(1L, "Title of itemDto1", 123L, 456);
        List<OrderItemDto> items = List.of(item);
        OrderDto dto = new OrderDto(1L, items, 789L);

        doReturn(Mono.just(dto)).when(service).getOrder(1L);

        webTestClient.get()
                .uri("/orders/1?newOrder=true")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Title of itemDto1");
                    assert html.contains("123");
                    assert html.contains("456");
                    assert html.contains("789");
                });

        verify(service).getOrder(1L);
    }
}