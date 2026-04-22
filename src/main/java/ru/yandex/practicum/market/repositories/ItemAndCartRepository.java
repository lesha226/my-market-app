package ru.yandex.practicum.market.repositories;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.entities.Item;

public interface ItemAndCartRepository {

    Flux<Item> findAllBySearchString(String search, ItemsSort sort, int offset, int limit);

    Flux<Item> findAllByPositiveCount();

    Mono<Item> findById(Long id);

    Mono<Void> increaseItemCountInCart(Long id);

    Mono<Void> decreaseItemCountInCart(Long id);
}
