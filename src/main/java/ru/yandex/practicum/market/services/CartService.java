package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {


    private final ItemDto item1 = new ItemDto(1L, "Title 1", "Description 1", "", 100L, 1);
    private final List<ItemDto> items = List.of(item1);
    private final CartDto cartDto = new CartDto(items, 100L);

    public CartDto getCart() {
        // TODO : use repository
        return cartDto;
    }

    public CartDto addItemToCart(Long id, ItemAction action) {
        // TODO : use repository
        return cartDto;
    }
}
