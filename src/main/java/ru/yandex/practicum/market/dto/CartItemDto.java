package ru.yandex.practicum.market.dto;

import java.util.Objects;

public record CartItemDto(
        Long id,
        String title,
        String description,
        String imgPath,
        long price,
        int count
){
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItemDto that = (CartItemDto) o;
        return count == that.count && price == that.price && Objects.equals(title, that.title) && Objects.equals(imgPath, that.imgPath) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, imgPath, price, count);
    }
}
