package ru.yandex.practicum.market.repositories;

import org.jspecify.annotations.NonNull;
import org.springframework.data.repository.CrudRepository;
import ru.yandex.practicum.market.entities.Order;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Long> {

    @Override
    @NonNull
    List<Order> findAll();

}
