package ru.yandex.practicum.market.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Item {

    @Id
    @Column("id")
    private Long id;

    @Column("title")
    private String title;

    @Column("description")
    private String description;

    @Column("img_path")
    private String imgPath;

    @Column("price")
    private long price;

    @Transient
    private CartItem cartItem;

}
