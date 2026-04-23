package ru.yandex.practicum.market.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.entities.CartItem;
import ru.yandex.practicum.market.entities.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "cartItem.count", target = "count")
    CartItemDto toDto(Item item);
}
