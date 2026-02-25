package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.OrderDto;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final ItemDto item1 = new ItemDto(1L, "Title 1", "Description 1", "", 100L, 1);
    private final List<ItemDto> items = List.of(item1);
    private final OrderDto order = new OrderDto(1L, items, BigDecimal.valueOf(100L));

    public List<OrderDto> getOrders() {
        // TODO : use repository
        return List.of(order);
    }

    public OrderDto getOrder(Long id, boolean newOrder) {
        // TODO : use repository
        return order;
    }
}
