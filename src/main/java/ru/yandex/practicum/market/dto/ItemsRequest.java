package ru.yandex.practicum.market.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.BindParam;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;

@Data
@AllArgsConstructor
public class ItemsRequest {

    private String search = "";
    private ItemsSort sort = ItemsSort.NO;
    private Integer pageNumber = 1;
    private Integer pageSize = 5;

}
