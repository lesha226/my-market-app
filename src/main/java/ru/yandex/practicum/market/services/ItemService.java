package ru.yandex.practicum.market.services;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.market.dto.*;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;

@Service
public class ItemService {

    private final ItemDto tempItem1 = new ItemDto(1, "Title 1", "Description 1", "", 100, 0);
    private final ItemDto tempItem2 = new ItemDto(2, "Title 2", "Description 2", "", 200, 0);
    private final ItemDto tempItem3 = new ItemDto(3, "Title 3", "Description 3", "", 300, 0);
    private final ItemDto[][] tempItems = {{tempItem1, tempItem2, tempItem3}};
    private final ItemsPaging tempPaging = new ItemsPaging(5, 1, false, false);
    private final Long newOrderId = 1L;

    public ItemsPageDto getItemsPage(String search, ItemsSort sort, int pageNumber, int pageSize) {
        // TODO : use repository
        return new ItemsPageDto(tempItems, search, sort, new ItemsPaging(pageSize, pageNumber, pageNumber > 1, true));
    }

    public void putItemInCart(Long id, ItemAction action) {
        // TODO : use repository
    }

    public ItemDto getItem(Long id) {
        // TODO: use repository
        return tempItem1;
    }

    public ItemDto addToCartAndReturnItem(Long id, ItemAction action) {
        // TODO: use repository
        return tempItem1;
    }

    public Long buy() {
        // TODO : use repository
        return newOrderId;
    }
}
