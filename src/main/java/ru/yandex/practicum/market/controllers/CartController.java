package ru.yandex.practicum.market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.ItemActionRequest;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.services.CartService;

@Controller
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/cart/items")
    public Mono<Rendering> getItems() {
        System.out.println("getItems");
        return service.getCart()
                .map(cartDto ->
                        Rendering.view("cart")
                                .modelAttribute("items", cartDto.items())
                                .modelAttribute("total", cartDto.total())
                                .build()
                );
    }

    @PostMapping("/cart/items")
    public Mono<Rendering> addItemToCart(Mono<ItemActionRequest> itemActionRequestMono) {

        return itemActionRequestMono
                .flatMap(itemActionRequest ->
                        service.performAction(itemActionRequest.id(), itemActionRequest.action()))
                .thenReturn("/cart/items")
                .map(url -> Rendering.redirectTo(url).build());
    }

    @PostMapping("/buy")
    public Mono<Rendering> buy() {

        System.out.println("buy");

        return service.buy()
                .log()
                .map(orderId ->
                        UriComponentsBuilder.fromPath("/orders/" + orderId)
                                .queryParam("newOrder", true)
                                .build()
                                .toUriString()
                )
                .map(url -> Rendering.redirectTo(url).build());
    }
}
