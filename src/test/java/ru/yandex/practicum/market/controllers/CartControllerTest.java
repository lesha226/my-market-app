package ru.yandex.practicum.market.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.services.CartService;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(CartController.class)
class CartControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    CartService service;

    @Test
    void getItems() throws Exception {
        CartItemDto item = new CartItemDto(1L, "Title of itemDto1", "Description of itemDto1", "/images/img123.jpg", 123L, 456);
        List<CartItemDto> items = List.of(item);
        CartDto dto = new CartDto(items, 789L);

        doReturn(Mono.just(dto)).when(service).getCart();

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Title of itemDto1");
                    assert html.contains("Description of itemDto1");
                    assert html.contains("/images/img123.jpg");
                    assert html.contains("123");
                    assert html.contains("456");
                    assert html.contains("789");
                });

        verify(service).getCart();
    }

    @ParameterizedTest
    @EnumSource(ItemAction.class)
    void addItemToCart(ItemAction itemAction) throws Exception {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id", "1");
        formData.add("action", itemAction.toString());

        doReturn(Mono.empty()).when(service).performAction(1L, itemAction);

        webTestClient.post()
                .uri("/cart/items")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        verify(service).performAction(1L, itemAction);
    }

    @Test
    void testBuy() throws Exception {

        doReturn(Mono.just(1L)).when(service).buy();

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/orders/1?newOrder=true");

        verify(service).buy();

    }
}