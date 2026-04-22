package ru.yandex.practicum.market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.*;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.exceptions.ItemNotFoundException;
import ru.yandex.practicum.market.services.CartService;
import ru.yandex.practicum.market.services.ItemService;

@Controller
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping({"/", "/items"})
    public Mono<Rendering> getItemsPage(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "sort", defaultValue = "NO") ItemsSort sort,
            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {
        return itemService.getItemsPage(search, sort, pageNumber, pageSize)
                .map(itemsPageDto ->
                        Rendering.view("items_")
                                .modelAttribute("items", itemsPageDto.items())
                                .modelAttribute("search", itemsPageDto.search())
                                .modelAttribute("sort", itemsPageDto.sort().name())
                                .modelAttribute("paging", itemsPageDto.paging())
                                .build());
    }

    @PostMapping("/items")
    public Mono<Rendering> addItemToCart(Mono<ItemActionRequest> itemActionRequestMono, Mono<ItemsRequest> itemsRequestMono) {
        return itemActionRequestMono
                .flatMap(itemService::addItemToCart)
                .then(itemsRequestMono)
                .map(params ->
                    UriComponentsBuilder.fromPath("/items")
                            .queryParam("search", params.getSearch())
                            .queryParam("sort", params.getSort())
                            .queryParam("pageNumber", params.getPageNumber())
                            .queryParam("pageSize", params.getPageSize())
                            .build()
                            .toUriString()
                )
                .map(url -> Rendering.redirectTo(url).build());
    }

    @GetMapping("/items/{id}")
    public Mono<Rendering> getItem(@PathVariable("id") Long id) {

        System.out.println("getItem id: " + id);

        Mono<ItemDto> itemMono = itemService.getItem(id)
                .switchIfEmpty(Mono.error(() -> new ItemNotFoundException(id)));

        return itemMono
                .map(itemDto ->
                    Rendering.view("item")
                            .modelAttribute("item", itemDto)
                            .build());
    }

    @PostMapping("/items/{id}")
    public Mono<Rendering> addItemToCartAndReturnItem(Mono<ItemActionRequest> itemActionRequest) {

        return itemActionRequest
                .doOnNext(params -> System.out.println("addItemToCartAndReturnItem params: " + params))
                .flatMap(params -> itemService.addItemToCartAndReturnItem(params)
                        .switchIfEmpty(Mono.error(() -> new ItemNotFoundException(params.id())))
                )
                .map(itemDto ->
                        Rendering
                                .view("item")
                                .modelAttribute("item", itemDto)
                                .build())
                .doOnNext(rendering -> System.out.println("Done."));
    }

}
