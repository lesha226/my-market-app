package ru.yandex.practicum.market.repositories;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.market.entities.CartItem;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @NonNull
    List<CartItem> findAllByOrderByItemId();
}
