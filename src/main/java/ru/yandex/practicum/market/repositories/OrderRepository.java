package ru.yandex.practicum.market.repositories;

import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.market.entities.Order;

import java.util.List;

public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    Flux<Order> findAllByOrderByIdAsc();
}
