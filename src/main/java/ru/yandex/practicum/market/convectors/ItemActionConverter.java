package ru.yandex.practicum.market.convectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;

@Component
public class ItemActionConverter implements Converter<String, ItemAction> {

    @Override
    public ItemAction convert(String source) {
        return ItemAction.valueOf(source.toUpperCase());
    }

}
