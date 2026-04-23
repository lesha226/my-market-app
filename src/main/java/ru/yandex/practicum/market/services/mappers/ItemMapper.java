package ru.yandex.practicum.market.services.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.entities.CartItem;
import ru.yandex.practicum.market.entities.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(source = "cartItem.count", target = "count")
    ItemDto toDto(Item item);

    default ItemDto toDto(Item item, CartItem cartItem) {

        if ( item == null ) {
            return null;
        }

        int count = 0;
        Long id = null;
        String title = null;
        String description = null;
        String imgPath = null;
        long price = 0L;

        id = item.getId();
        title = item.getTitle();
        description = item.getDescription();
        imgPath = item.getImgPath();
        price = item.getPrice();

        if (cartItem != null) {
            count = cartItem.getCount();
        }

        return new ItemDto( id, title, description, imgPath, price, count );
    };
}
