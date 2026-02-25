package ru.yandex.practicum.market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.market.dto.CartDto;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.services.CartService;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    @GetMapping("/items")
    public String getItems(Model model) {
        CartDto cartDto = service.getCart();

        model.addAttribute("items", cartDto.items());
        model.addAttribute("total", cartDto.total());

        return "cart";
    }

    @PostMapping("/items")
    public String addToCart(Model model,
                            @RequestParam(name = "id") Long id,
                            @RequestParam(name = "action") ItemAction action) {

        service.addToCart(id, action);
        CartDto cartDto = service.getCart();

        model.addAttribute("items", cartDto.items());
        model.addAttribute("total", cartDto.total());

        return "cart";
    }
}
