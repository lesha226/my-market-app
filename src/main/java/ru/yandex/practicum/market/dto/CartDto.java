package ru.yandex.practicum.market.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartDto(List<CartItemDto> items, long total) {
}
