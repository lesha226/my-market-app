package ru.yandex.practicum.market.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.entities.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "cartItem.count", target = "count")
    ItemDto toDto(Item item);
}
