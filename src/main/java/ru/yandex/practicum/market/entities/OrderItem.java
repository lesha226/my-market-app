package ru.yandex.practicum.market.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_items")
@Data
@IdClass(OrderItemId.class)
public class OrderItem {

    @Id
    private Long orderId;

    @Id
    private Long itemId;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @MapsId("orderId")
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;*/

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @Column(nullable = false)
    private int count;

}
