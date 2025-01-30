package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.OrderClient;
import ru.practicum.dto.CreateNewOrderRequest;
import ru.practicum.dto.OrderDto;
import ru.practicum.dto.ProductReturnRequest;
import ru.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderClient {
    private final OrderService orderService;

    @Override
    @GetMapping
    public List<OrderDto> getClientOrders(String userName) {
        return orderService.getClientOrders(userName);
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        return orderService.createNewOrder(request);
    }

    @Override
    @PostMapping("/return")
    public OrderDto returnProducts(ProductReturnRequest request) {
        return orderService.returnProducts(request);
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payOrder(UUID orderId) {
        return orderService.payOrder(orderId);
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto failPayOrder(UUID orderId) {
        return orderService.failPayOrder(orderId);
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto deliverOrder(UUID orderId) {
        return orderService.deliverOrder(orderId);
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto failDeliverOrder(UUID orderId) {
        return orderService.failDeliverOrder(orderId);
    }

    @Override
    @PostMapping("/completed")
    public OrderDto completeOrder(UUID orderId) {
        return orderService.completeOrder(orderId);
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalPrice(UUID orderId) {
        return orderService.calculateTotalPrice(orderId);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryPrice(UUID orderId) {
        return orderService.calculateDeliveryPrice(orderId);
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assemblyOrder(UUID orderId) {
        return orderService.assemblyOrder(orderId);
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto failAssemblyOrder(UUID orderId) {
        return orderService.failAssemblyOrder(orderId);
    }
}
