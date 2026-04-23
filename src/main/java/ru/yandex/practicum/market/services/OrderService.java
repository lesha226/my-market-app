package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.OrderDto;
import ru.yandex.practicum.market.dto.OrderItemDto;
import ru.yandex.practicum.market.entities.Item;
import ru.yandex.practicum.market.entities.Order;
import ru.yandex.practicum.market.entities.OrderItem;
import ru.yandex.practicum.market.exceptions.OrderNotFoundException;
import ru.yandex.practicum.market.repositories.ItemRepository;
import ru.yandex.practicum.market.repositories.OrderItemRepository;
import ru.yandex.practicum.market.repositories.OrderRepository;
import ru.yandex.practicum.market.services.mappers.OrderMapper;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper mapper;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, ItemRepository itemRepository, OrderMapper mapper) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    public Mono<List<OrderDto>> getOrders() {
        Flux<Order> ordersFlux = orderRepository.findAllByOrderByIdAsc();

        return ordersFlux.flatMap(order -> getOrder(order.getId()))
                .collectList();
    }

    public Mono<OrderDto> getOrder(Long id) {

        Mono<Order> orderMono = orderRepository.findById(id)
                .switchIfEmpty(Mono.error(() -> new OrderNotFoundException(id)));

        Mono<List<OrderItemDto>> OrderItemsMono = orderItemRepository.findAllByOrderIdOrderByIdAsc(id)
                .flatMap(orderItem ->
                                itemRepository.findById(orderItem.getItemId())
                                        .map(item -> toDto(orderItem, item))
                        )
                .collectList();

        return Mono.zip(orderMono, OrderItemsMono)
                .map(tuple -> {
                    Order order = tuple.getT1();
                    List<OrderItemDto> items = tuple.getT2();
                    long totalSum = items.stream()
                            .mapToLong(orderItemDto -> orderItemDto.price() * orderItemDto.count())
                            .sum();

                    return new OrderDto(order.getId(), items, totalSum);
                });
    }

    private OrderItemDto toDto(OrderItem orderItem, Item item) {
        return new OrderItemDto(orderItem.getId(), item.getTitle(), item.getPrice(), orderItem.getCount());
    }
}
