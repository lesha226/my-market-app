package ru.yandex.practicum.market.repositories;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.market.entities.OrderItem;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long>  {

    Flux<OrderItem> findAllByOrderIdOrderByIdAsc(Long orderId);
}
