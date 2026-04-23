package ru.yandex.practicum.market.exceptions;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(Long id) {
        super("Товар не найден!");
    }
}
