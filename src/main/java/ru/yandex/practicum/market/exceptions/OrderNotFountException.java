package ru.yandex.practicum.market.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class OrderNotFountException extends EntityNotFoundException {

    public OrderNotFountException(Long id) {
        super("Заказ не найден!");
    }
}
