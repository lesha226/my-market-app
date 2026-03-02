package ru.yandex.practicum.market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.practicum.market.dto.*;
import ru.yandex.practicum.market.dto.subtypes.ItemAction;
import ru.yandex.practicum.market.dto.subtypes.ItemsSort;
import ru.yandex.practicum.market.services.CartService;
import ru.yandex.practicum.market.services.ItemService;

@Controller
public class ItemController {

    private final ItemService itemService;
    private final CartService cartService;

    public ItemController(ItemService itemService, CartService cartService) {
        this.itemService = itemService;
        this.cartService = cartService;
    }

    @GetMapping({"/", "/items"})
    public String getItemsPage(Model model,
                               @RequestParam(name = "search", defaultValue = "") String search,
                               @RequestParam(name = "sort", defaultValue = "NO") ItemsSort sort,
                               @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
                               @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {

        ItemsPageDto page = itemService.getItemsPage(search, sort, pageNumber, pageSize);

        model.addAttribute("items", page.items());
        model.addAttribute("search", page.search());
        model.addAttribute("sort", page.sort().name());
        model.addAttribute("paging", page.paging());

        return "items";
    }

    @PostMapping("/items")
    public String addItemToCart(RedirectAttributes redirectAttributes,
                            @RequestParam(name = "id") Long id,
                            @RequestParam(name = "action") ItemAction action,
                            @RequestParam(name = "search", defaultValue = "") String search,
                            @RequestParam(name = "sort", defaultValue = "NO") String sort,
                            @RequestParam(name = "pageNumber", defaultValue = "1") int pageNumber,
                            @RequestParam(name = "pageSize", defaultValue = "5") int pageSize) {

        itemService.addItemToCart(id, action);

        redirectAttributes.addAttribute("search", search);
        redirectAttributes.addAttribute("sort", sort);
        redirectAttributes.addAttribute("pageNumber", pageNumber);
        redirectAttributes.addAttribute("pageSize", pageSize);

        return "redirect:/items";
    }

    @GetMapping("/items/{id}")
    public String getItem(Model model, @PathVariable("id") Long id) {
        ItemDto item = itemService.getItem(id);

        model.addAttribute("item", item);

        return "item";
    }

    @PostMapping("/items/{id}")
    public String addItemToCart(Model model,
                            @PathVariable Long id,
                            @RequestParam(name = "action") ItemAction action) {

        ItemDto item = itemService.addItemToCartAndReturnItem(id, action);

        model.addAttribute("item", item);

        return "item";
    }

    @PostMapping("/buy")
    public String buy(RedirectAttributes redirectAttributes) {

        Long newOrderId = cartService.buy();

        redirectAttributes.addAttribute("newOrder", true);

        return "redirect:/orders/" + newOrderId;
    }

}
