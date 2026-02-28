package ru.yandex.practicum.market.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Set;

@Entity
@Table(name="orders")
@Data
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(/*mappedBy = "order", */cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @NonNull
    private Set<OrderItem> items;

    @Column(name = "total_sum", nullable = false)
    private long totalSum;

}
