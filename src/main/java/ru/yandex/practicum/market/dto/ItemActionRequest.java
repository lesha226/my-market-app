package ru.yandex.practicum.market.dto;

import org.springframework.web.bind.annotation.BindParam;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;

public record ItemActionRequest(Long id, ItemAction action) {

    public ItemActionRequest(
            @BindParam("id") Long id,
            @BindParam("action") String action) {

        this(id, ItemAction.valueOf(action.toUpperCase()));
    }
}
