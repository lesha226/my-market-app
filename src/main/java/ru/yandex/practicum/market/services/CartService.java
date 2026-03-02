package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.entities.Item;
import ru.yandex.practicum.market.entities.Order;
import ru.yandex.practicum.market.entities.OrderItem;
import ru.yandex.practicum.market.exceptions.EmptyCartException;
import ru.yandex.practicum.market.exceptions.ItemNotFoundException;
import ru.yandex.practicum.market.repositories.ItemRepository;
import ru.yandex.practicum.market.repositories.OrderRepository;
import ru.yandex.practicum.market.services.mappers.CartMapper;
import ru.yandex.practicum.market.services.mappers.ItemMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional
public class CartService {

    private final ItemRepository repository;
    private final OrderRepository orderRepository;
    private final CartMapper mapper;

    public CartService(ItemRepository repository, OrderRepository orderRepository, CartMapper mapper) {
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public CartDto getCart() {
        List<Item> items = repository.findCartItems();

        AtomicLong total = new AtomicLong();
        List<CartItemDto> ItemDtoList= items.stream()
                .map(mapper::toDto)
                .peek(itemDto -> total.addAndGet(itemDto.price() * itemDto.count()))
                .toList();
        return new CartDto(ItemDtoList, total.get());
    }

    public CartDto performActionAndGetCart(Long id, ItemAction action) {
        if (id == null || action == null) {
            throw new IllegalArgumentException();
        }

        performAction(id, action);

        return getCart();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void performAction(Long id, ItemAction action) {
        Item cartItem = repository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException(id));

        int count = switch (action) {
            case MINUS -> Math.max(cartItem.getCount() - 1, 0);
            case PLUS ->  cartItem.getCount() + 1;
            case DELETE -> 0;
        };

        cartItem.setCount(count);
        //repository.save(cartItem);
    }

    @Transactional
    public Long buy() {
        List<Item> items = repository.findByCountGreaterThanOrderById(0);

        if (items == null || items.isEmpty()) {
            throw new EmptyCartException();
        }

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        long totalSum = 0;
        for (Item item: items) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setCount(item.getCount());
            orderItems.add(orderItem);

            totalSum += item.getPrice() * item.getCount();
            item.setCount(0);
        }
        order.setItems(orderItems);

        orderRepository.save(order);

        return order.getId();
    }
}
