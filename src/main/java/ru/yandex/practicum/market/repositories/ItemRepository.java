package ru.yandex.practicum.market.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import ru.yandex.practicum.market.entities.Item;

public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {

}
