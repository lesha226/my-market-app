package ru.yandex.practicum.market.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public record OrderDto(
        Long id,
        List<OrderItemDto> items,
        long totalSum
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto dto = (OrderDto) o;
        return totalSum == dto.totalSum && Objects.equals(items, dto.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, totalSum);
    }
}
