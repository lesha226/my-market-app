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
import ru.yandex.practicum.market.dto.ItemActionRequest;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.ItemsPageDto;
import ru.yandex.practicum.market.dto.ItemsRequest;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.services.ItemService;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest({ItemController.class})
class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockitoBean
    ItemService itemService;

    @Test
    void testGetItemsPage() throws Exception {
        ItemDto itemDto1 = new ItemDto(1L, "Title of itemDto1", "Description of itemDto1", "Img of itemDto1", 123, 456);
        ItemDto itemDtoNull = new ItemDto(-1L, "", "", "", 0, 0);
        List<List<ItemDto>> items = List.of(List.of(itemDto1, itemDtoNull, itemDtoNull));
        ItemsPaging paging = new ItemsPaging(4, 3, true, true);
        ItemsPageDto dto = new ItemsPageDto(items, "5", ItemsSort.ALPHA, paging);
        when(itemService.getItemsPage("1", ItemsSort.PRICE, 3, 4)).thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/items?search=1&sort=PRICE&pageNumber=3&pageSize=4")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    System.out.println("html: " + html);
                    assert html.contains("Title of itemDto1");
                    assert html.contains("Description of itemDto1");
                    assert html.contains("Img of itemDto1");
                    assert html.contains("123");
                    assert html.contains("456");
                });

        verify(itemService).getItemsPage("1", ItemsSort.PRICE, 3, 4);
    }

    @ParameterizedTest
    @EnumSource(ItemAction.class)
    void testAddItemToCart(ItemAction itemAction) throws Exception {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id", "1");
        formData.add("action", itemAction.toString());
        formData.add("search", "1");
        formData.add("sort", "PRICE");
        formData.add("pageNumber", "3");
        formData.add("pageSize", "4");
        ItemActionRequest itemActionRequest = new ItemActionRequest(1L, itemAction);

        when(itemService.addItemToCart(itemActionRequest)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/items")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/items?search=1&sort=PRICE&pageNumber=3&pageSize=4");

        verify(itemService).addItemToCart(itemActionRequest);
    }

    @Test
    void testGetItem() throws Exception {
        ItemDto dto = new ItemDto(1L, "Title of itemDto1", "Description of itemDto1", "Img of itemDto1", 123, 456);

        when(itemService.getItem(1L)).thenReturn(Mono.just(dto));

        webTestClient.get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Title of itemDto1");
                    assert html.contains("Description of itemDto1");
                    assert html.contains("Img of itemDto1");
                    assert html.contains("123");
                    assert html.contains("456");
                });

        verify(itemService).getItem(1L);
    }

    @ParameterizedTest
    @EnumSource
    void testAddItemToCart_ReturnItem(ItemAction itemAction) throws Exception {
        ItemDto dto = new ItemDto(1L, "Title of itemDto1", "Description of itemDto1", "Img of itemDto1", 123, 456);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("action", itemAction.toString());
        ItemActionRequest itemActionRequest = new ItemActionRequest(1L, itemAction);

        when(itemService.addItemToCartAndReturnItem(itemActionRequest)).thenReturn(Mono.just(dto));

        webTestClient.post()
                .uri("/items/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
                .expectBody(String.class)
                .value(html -> {
                    assert html.contains("Title of itemDto1");
                    assert html.contains("Description of itemDto1");
                    assert html.contains("Img of itemDto1");
                    assert html.contains("123");
                    assert html.contains("456");
                });

        verify(itemService).addItemToCartAndReturnItem(itemActionRequest);
    }
}