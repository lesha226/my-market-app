package ru.yandex.practicum.market.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record ItemDto (
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
        ItemDto dto = (ItemDto) o;
        return count == dto.count && price == dto.price && Objects.equals(title, dto.title) && Objects.equals(imgPath, dto.imgPath) && Objects.equals(description, dto.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, imgPath, price, count);
    }
}
