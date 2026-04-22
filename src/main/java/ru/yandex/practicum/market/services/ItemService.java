package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.*;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.entities.CartItem;
import ru.yandex.practicum.market.entities.Item;
import ru.yandex.practicum.market.exceptions.ItemNotFoundException;
import ru.yandex.practicum.market.repositories.CartItemRepository;
import ru.yandex.practicum.market.repositories.ItemRepository;
import ru.yandex.practicum.market.repositories.ItemAndCartRepository;
import ru.yandex.practicum.market.services.mappers.ItemMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemService {

    private final ItemRepository repository;
    private final ItemAndCartRepository itemAndCartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartService cartService;
    private final ItemMapper mapper;

    public ItemService(ItemRepository repository, ItemAndCartRepository itemAndCartRepository, CartItemRepository cartItemRepository, CartService cartService, ItemMapper mapper) {
        this.repository = repository;
        this.itemAndCartRepository = itemAndCartRepository;
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.mapper = mapper;
    }

    public Mono<ItemsPageDto> getItemsPage(String search, ItemsSort sort, int pageNumber, int pageSize) {

        /*String search = itemsRequest.getSearch() == null ? "": itemsRequest.getSearch();
        ItemsSort sort = itemsRequest.getSort() == null ? ItemsSort.NO: itemsRequest.getSort();
        int pageNumber = itemsRequest.getPageNumber() == null ? ;
        int pageSize = itemsRequest.getPageSize();*/

        if (search == null || sort == null || pageNumber <= 0 || pageSize <= 0) {
            return Mono.error(new IllegalArgumentException());
        }

        int offset = (pageNumber - 1) * pageSize;
        int limit = pageSize + 1;

        return itemAndCartRepository.findAllBySearchString(search, sort, offset, limit)
                .collectList()
                .map(items -> {
                    List<List<ItemDto>> itemsForDto = makeItemsForDto(items, pageSize);
                    boolean hasPrevious = pageNumber > 1;
                    boolean hasNext = items.size() > pageSize;
                    ItemsPaging paging = new ItemsPaging(pageSize, pageNumber, hasPrevious, hasNext);

                    return new ItemsPageDto(itemsForDto, search, sort, paging);
                });
    }

    public Mono<ItemDto> getItem(Long id) {
        if (id == null) {
            return Mono.error(new IllegalArgumentException());
        }

        Mono<Item> itemMono = repository.findById(id)
                .switchIfEmpty(Mono.error(() -> new ItemNotFoundException(id)));

        Mono<CartItem> cartItemMono = cartItemRepository.findById(id)
                .switchIfEmpty(Mono.fromCallable(() -> new CartItem(id, 0)));

        return Mono.zip(itemMono, cartItemMono)
                .map(tuple -> mapper.toDto(tuple.getT1(), tuple.getT2()));
    }

    public Mono<Void> addItemToCart(ItemActionRequest itemActionRequest) {
        if (itemActionRequest == null || itemActionRequest.id() == null || itemActionRequest.action() == null) {
            return Mono.error(new IllegalArgumentException());
        }

        return cartService.performAction(itemActionRequest.id(), itemActionRequest.action());
    }

    public Mono<ItemDto> addItemToCartAndReturnItem(ItemActionRequest itemActionRequest) {
        if (itemActionRequest == null || itemActionRequest.id() == null || itemActionRequest.action() == null) {
            return Mono.error(new IllegalArgumentException());
        }

        return cartService.performAction(itemActionRequest.id(), itemActionRequest.action())
                .then(getItem(itemActionRequest.id()));
    }

    private List<List<ItemDto>> makeItemsForDto(List<Item> itemList, int pageSize) {
        List<List<ItemDto>> itemsForDto = new ArrayList<>();
        List<ItemDto> group = new ArrayList<>();
        itemsForDto.add(group);

        for (int i = 0; i < Math.min(itemList.size(), pageSize); i++) {
            if (group.size() == 3) {
                group = new ArrayList<>();
                itemsForDto.add(group);
            }

            group.add(mapper.toDto(itemList.get(i)));
        }

        while (group.size() < 3) {
            group.add(new ItemDto(-1L, "", "", "", 0L, 0));
        }

        return itemsForDto;
    }
}
