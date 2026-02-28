package ru.yandex.practicum.market.exceptions;

import jakarta.persistence.EntityNotFoundException;

public class ItemNotFoundException extends EntityNotFoundException {
    public ItemNotFoundException(Long id) {
        super("Товар не найден!");
    }
}
