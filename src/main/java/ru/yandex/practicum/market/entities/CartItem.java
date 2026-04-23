package ru.yandex.practicum.market.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("cart_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CartItem {

    @Id
    @Column("item_id")
    private Long itemId;

    @Column("count")
    private int count;

}
