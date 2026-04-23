package ru.yandex.practicum.market.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.entities.CartItem;

public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {
    Flux<CartItem> findAllByOrderByItemId();

    @Query("INSERT INTO cart_items(item_id, count) VALUES (:itemId, :count)")
    Mono<Long> insert(@Param("itemId") Long itemId, @Param("count") int count);
}
