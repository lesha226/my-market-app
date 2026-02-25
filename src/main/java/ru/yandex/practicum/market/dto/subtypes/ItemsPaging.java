package ru.yandex.practicum.market.dto.subtypes;

public record ItemsPaging(
        int pageSize,
        int pageNumber,
        boolean hasPrevious,
        boolean hasNext
){}
