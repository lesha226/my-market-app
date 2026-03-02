package ru.yandex.practicum.market.exceptions;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException() {
        super("Корзина пуста!");
    }
}
