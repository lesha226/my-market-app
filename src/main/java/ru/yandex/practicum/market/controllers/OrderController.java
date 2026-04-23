package ru.yandex.practicum.market.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
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
    public Mono<Rendering> getOrders(Model model) {

        return service.getOrders()
                .map(orders ->
                        Rendering.view("orders")
                                .modelAttribute("orders", orders)
                                .build()
                );
    }

    @GetMapping("/{id}")
    public Mono<Rendering> getOrder(
                           @PathVariable("id") Long id,
                           @RequestParam(name = "newOrder", defaultValue = "false") boolean newOrder) {

        return service.getOrder(id)
                .map(orderDto ->
                    Rendering.view("order")
                            .modelAttribute("newOrder", newOrder)
                            .modelAttribute("order", orderDto)
                            .build()
                );
    }
    
}
