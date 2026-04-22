package ru.yandex.practicum.market.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.ArrayList;
import java.util.List;

@Table("orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Order {

    @Id
    @Column("id")
    private Long id;

}
