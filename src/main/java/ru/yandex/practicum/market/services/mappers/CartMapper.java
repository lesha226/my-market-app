package ru.yandex.practicum.market.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.entities.CartItem;
import ru.yandex.practicum.market.entities.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.title", target = "title")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.imgPath", target = "imgPath")
    @Mapping(source = "item.price", target = "price")
    CartItemDto toDto(CartItem cartItem);

    List<CartItemDto> toDto(List<CartItem> cartItems);
}
