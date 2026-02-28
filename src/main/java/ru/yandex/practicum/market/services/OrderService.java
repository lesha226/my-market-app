package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.OrderDto;
import ru.yandex.practicum.market.entities.Order;
import ru.yandex.practicum.market.exceptions.OrderNotFountException;
import ru.yandex.practicum.market.repositories.OrderRepository;
import ru.yandex.practicum.market.services.mappers.OrderMapper;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper mapper;

    public OrderService(OrderRepository orderRepository, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    public List<OrderDto> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return mapper.toDto(orders);
    }

    public OrderDto getOrder(Long id, boolean newOrder) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFountException(id));

        return mapper.toDto(order);
    }
}
