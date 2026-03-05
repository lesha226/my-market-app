package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.CartItemDto;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.entities.CartItem;
import ru.yandex.practicum.market.entities.Item;
import ru.yandex.practicum.market.entities.Order;
import ru.yandex.practicum.market.entities.OrderItem;
import ru.yandex.practicum.market.exceptions.EmptyCartException;
import ru.yandex.practicum.market.exceptions.ItemNotFoundException;
import ru.yandex.practicum.market.repositories.CartItemRepository;
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

    private final CartItemRepository cartItemRepository;
    private final ItemRepository repository;
    private final OrderRepository orderRepository;
    private final CartMapper mapper;

    public CartService(CartItemRepository cartItemRepository, ItemRepository repository, OrderRepository orderRepository, CartMapper mapper) {
        this.cartItemRepository = cartItemRepository;
        this.repository = repository;
        this.orderRepository = orderRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public CartDto getCart() {
        List<CartItem> cartItems = cartItemRepository.findAllByOrderByItemId();

        AtomicLong total = new AtomicLong();
        List<CartItemDto> cartItemDtoList = cartItems.stream()
                .map(mapper::toDto)
                .peek(itemDto -> total.addAndGet(itemDto.price() * itemDto.count()))
                .toList();
        return new CartDto(cartItemDtoList, total.get());
    }

    public CartDto performActionAndGetCart(Long id, ItemAction action) {
        if (id == null || action == null) {
            throw new IllegalArgumentException();
        }

        performAction(id, action);

        return getCart();
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void performAction(Long itemId, ItemAction action) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(itemId));

        CartItem cartItem = item.getCartItem();
        int count = (cartItem == null) ? 0 : cartItem.getCount();
        count = switch (action) {
            case MINUS -> Math.max(count - 1, 0);
            case PLUS ->  count + 1;
            case DELETE -> 0;
        };

        if (count > 0) {

            if (cartItem == null) {
                cartItem = new CartItem(null, item, count);
                cartItemRepository.save(cartItem);
                item.setCartItem(cartItem);
            } else {
                cartItem.setCount(count);
            }

        } else {

            if (cartItem != null) {
                item.setCartItem(null);
                cartItemRepository.delete(cartItem);
            }

        }

    }

    @Transactional
    public Long buy() {
        List<CartItem> cartItems = cartItemRepository.findAllByOrderByItemId();

        if (cartItems == null || cartItems.isEmpty()) {
            throw new EmptyCartException();
        }

        Order order = new Order();
        List<OrderItem> orderItems = new ArrayList<>();
        //long totalSum = 0;
        for (CartItem cartItem: cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(cartItem.getItem());
            orderItem.setCount(cartItem.getCount());
            orderItems.add(orderItem);

            //totalSum += cartItem.getItem().getPrice() * cartItem.getCount();
            cartItemRepository.delete(cartItem);
            //cartItem.getItem().setCartItem(null);
        }
        order.setItems(orderItems);

        orderRepository.save(order);

        return order.getId();
    }
}
