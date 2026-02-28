package ru.yandex.practicum.market.services.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.entities.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    CartItemDto toDto(Item item);

    List<CartItemDto> toDto(List<Item> items);
}
