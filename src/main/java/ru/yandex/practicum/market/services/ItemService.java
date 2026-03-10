package ru.yandex.practicum.market.services;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.market.dto.*;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;
import ru.yandex.practicum.market.entities.Item;
import ru.yandex.practicum.market.repositories.ItemRepository;
import ru.yandex.practicum.market.services.mappers.ItemMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ItemService {

    private final ItemRepository repository;
    private final CartService cartService;
    private final ItemMapper mapper;

    public ItemService(ItemRepository repository, CartService cartService, ItemMapper mapper) {
        this.repository = repository;
        this.cartService = cartService;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ItemsPageDto getItemsPage(String search, ItemsSort sort, int pageNumber, int pageSize) {
        if (search == null || sort == null || pageNumber <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException();
        }

        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, mapToSort(sort));
        Page<Item> itemsPage = repository.findBySearchString(search.trim(), pageable);
        List<List<ItemDto>> itemsForDto = makeItemsForDto(itemsPage.getContent());
        ItemsPaging paging = new ItemsPaging(pageSize, pageNumber, itemsPage.hasPrevious(), itemsPage.hasNext());

        return new ItemsPageDto(itemsForDto, search, sort, paging);
    }

    private Sort mapToSort(ItemsSort sort) {
        if (sort == null) {
            return null;
        }

        return switch (sort) {
            case NO     -> Sort.by(Sort.Order.asc("id"));
            case ALPHA  -> Sort.by(Sort.Order.asc("title"), Sort.Order.asc("id"));
            case PRICE  -> Sort.by(Sort.Order.asc("price"), Sort.Order.asc("id"));
        };
    }

    @NonNull
    @Transactional(readOnly = true)
    public ItemDto getItem(Long id) {
        if (id == null) {
            throw new IllegalArgumentException();
        }

        Item item = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден!"));

        return mapper.toDto(item);
    }

    public void addItemToCart(Long id, ItemAction action) {
        if (id == null || action == null) {
            throw new IllegalArgumentException();
        }

        cartService.performAction(id, action);
    }

    public ItemDto addItemToCartAndReturnItem(Long id, ItemAction action) {
        if (id == null || action == null) {
            throw new IllegalArgumentException();
        }

        cartService.performAction(id, action);

        return getItem(id);
    }

    private List<List<ItemDto>> makeItemsForDto(List<Item> itemList) {
        List<List<ItemDto>> itemsForDto = new ArrayList<>();
        List<ItemDto> group = new ArrayList<>();
        itemsForDto.add(group);

        for (Item item: itemList) {
            if (group.size() == 3) {
                group = new ArrayList<>();
                itemsForDto.add(group);
            }

            group.add(mapper.toDto(item));
        }

        while (group.size() < 3) {
            group.add(new ItemDto(-1L, "", "", "", 0L, 0));
        }

        return itemsForDto;
    }
}
