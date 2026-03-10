package ru.yandex.practicum.market.dto;

import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;

import java.util.List;

public record ItemsPageDto(
        List<List<ItemDto>> items,
        String search,
        ItemsSort sort,
        ItemsPaging paging
) {}
