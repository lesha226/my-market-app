package ru.yandex.practicum.market.services.mappers;

import org.mapstruct.Mapper;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.entities.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toDto(Item item);
}
