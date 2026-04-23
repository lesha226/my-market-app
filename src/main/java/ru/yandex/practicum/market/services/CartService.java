package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.entities.CartItem;
import ru.yandex.practicum.market.entities.Item;
import ru.yandex.practicum.market.entities.Order;
import ru.yandex.practicum.market.entities.OrderItem;
import ru.yandex.practicum.market.exceptions.EmptyCartException;
import ru.yandex.practicum.market.repositories.*;
import ru.yandex.practicum.market.services.mappers.CartMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final ItemAndCartRepository itemAndCartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartMapper mapper;

    public CartService(CartItemRepository cartItemRepository, ItemRepository itemRepository, ItemAndCartRepository itemAndCartRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository, CartMapper mapper) {
        this.cartItemRepository = cartItemRepository;
        this.itemRepository = itemRepository;
        this.itemAndCartRepository = itemAndCartRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.mapper = mapper;
    }

    public Mono<CartDto> getCart() {
        Flux<Item> cartItemsFlux = itemAndCartRepository.findAllByPositiveCount();

        return cartItemsFlux
                .collectList()
                .map(items -> {
                    AtomicLong total = new AtomicLong();
                    List<CartItemDto> cartItemDtoList = items.stream()
                            .map(mapper::toDto)
                            .peek(itemDto -> total.addAndGet(itemDto.price() * itemDto.count()))
                            .toList();
                    return new CartDto(cartItemDtoList, total.get());
                });

    }

    public Mono<CartDto> performActionAndGetCart(Long id, ItemAction action) {
        if (id == null || action == null) {
            throw new IllegalArgumentException();
        }

        Mono<Void> actionMono = performAction(id, action);

        return actionMono
                .then(getCart());
    }

    public Mono<Void> performAction(Long itemId, ItemAction action) {

        Mono<Boolean> hasItemMono = itemRepository.existsById(itemId);

        return hasItemMono
                .filter(hasItem -> hasItem)
                .flatMap(hasItem ->
                        switch (action) {
                            case MINUS -> itemAndCartRepository.decreaseItemCountInCart(itemId);
                            case PLUS -> itemAndCartRepository.increaseItemCountInCart(itemId);
                            case DELETE -> cartItemRepository.deleteById(itemId);
                        });

    }

    @Transactional
    public Mono<Long> buy() {

        return orderRepository.save(new Order())
                .map(Order::getId)
                .flatMap(orderId -> {
                    return cartItemRepository
                            .findAllByOrderByItemId()
                            .switchIfEmpty(Mono.error(EmptyCartException::new))
                            .flatMap(cartItem -> {
                                OrderItem orderItem = new OrderItem(null, orderId, cartItem.getItemId(), cartItem.getCount());
                                return orderItemRepository.save(orderItem);
                            })
                            .then(cartItemRepository.deleteAll())
                            .thenReturn(orderId);
                });
    }
}
