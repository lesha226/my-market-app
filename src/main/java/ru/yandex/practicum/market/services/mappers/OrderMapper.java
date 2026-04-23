package ru.yandex.practicum.market.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.OrderDto;
import ru.yandex.practicum.market.dto.OrderItemDto;
import ru.yandex.practicum.market.entities.Order;
import ru.yandex.practicum.market.entities.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    /*@Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.title", target = "title")
    @Mapping(source = "item.price", target = "price")
    OrderItemDto toDto(OrderItem orderItem);

    List<OrderItemDto> toDto(List<OrderItem> items);*/
}
