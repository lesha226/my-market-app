package ru.yandex.practicum.market.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        Long id,
        List<ItemDto> items,
        BigDecimal totalSum
) {}
