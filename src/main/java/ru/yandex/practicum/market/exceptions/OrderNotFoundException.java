package ru.yandex.practicum.market.exceptions;

public class OrderNotFoundException extends NotFoundException {

    public OrderNotFoundException(Long id) {
        super("Заказ не найден!");
    }
}
