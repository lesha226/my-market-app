package ru.yandex.practicum.market.entities;

import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.Nullable;

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

    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY, optional = true)
    private CartItem cartItem;

    public int getCount() {
        if (cartItem == null) {
            return 0;
        }

        return cartItem.getCount();
    }

}
