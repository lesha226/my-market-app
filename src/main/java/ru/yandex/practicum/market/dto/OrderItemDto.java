package ru.yandex.practicum.market.dto;

import java.util.Objects;

public record OrderItemDto(
        Long id,
        String title,
        long price,
        int count
) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemDto that = (OrderItemDto) o;
        return count == that.count && price == that.price && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, price, count);
    }
}
