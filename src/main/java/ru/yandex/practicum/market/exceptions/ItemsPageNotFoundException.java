package ru.yandex.practicum.market.exceptions;

public class ItemsPageNotFoundException extends NotFoundException {
    public ItemsPageNotFoundException() {
        super("Страница товаров не найдена!");
    }
}
