package ru.yandex.practicum.market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.yandex.practicum.market.dto.OrderDto;
import ru.yandex.practicum.market.services.OrderService;

import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping
    public String getOrders(Model model) {
        
        List<OrderDto> orders = service.getOrders();

        model.addAttribute("orders", orders);
        
        return "orders";
    }
    
    @GetMapping("/{id}")
    public String getOrder(Model model, RedirectAttributes redirectAttributes,
                           @PathVariable("id") Long id,
                           @RequestParam(name = "newOrder", defaultValue = "false") boolean newOrder) {

        OrderDto order = service.getOrder(id, newOrder);

        //redirectAttributes.addAttribute("newOrder", newOrder);
        model.addAttribute("newOrder", newOrder);
        model.addAttribute("order", order);

        return "order";
    }
    
}
