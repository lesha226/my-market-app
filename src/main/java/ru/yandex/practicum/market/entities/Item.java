package ru.yandex.practicum.market.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="ITEMS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    private String description;

    @Column(name = "img_path")
    private String imgPath;

    private long price;

    @Column(columnDefinition = "int default 0")
    private int count = 0;
}
