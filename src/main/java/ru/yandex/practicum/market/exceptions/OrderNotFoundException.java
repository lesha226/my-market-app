package ru.yandex.practicum.market.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class OrderNotFoundException extends EntityNotFoundException {

    public OrderNotFoundException(Long id) {
        super("Заказ не найден!");
    }
}
