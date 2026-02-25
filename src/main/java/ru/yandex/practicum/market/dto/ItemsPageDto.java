package ru.yandex.practicum.market.dto;

import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.dto.subtypes.ItemsPaging;

public record ItemsPageDto(
        ItemDto[][] items,
        String search,
        ItemsSort sort,
        ItemsPaging paging
) {}
