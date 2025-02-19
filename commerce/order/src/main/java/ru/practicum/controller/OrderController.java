package ru.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.OrderClient;
import ru.practicum.dto.CreateNewOrderRequest;
import ru.practicum.dto.OrderDto;
import ru.practicum.dto.ProductReturnRequest;
import ru.practicum.facade.OrderFacade;

import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController implements OrderClient {
    private final OrderFacade orderFacade;

    @Override
    @GetMapping
    public List<OrderDto> getClientOrders(String userName) {
        return orderFacade.getClientOrders(userName);
    }

    @Override
    @PutMapping
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        return orderFacade.createNewOrder(request);
    }

    @Override
    @PostMapping("/return")
    public OrderDto returnProducts(ProductReturnRequest request) {
        return orderFacade.returnProducts(request);
    }

    @Override
    @PostMapping("/payment")
    public OrderDto payOrder(UUID orderId) {
        return orderFacade.payOrder(orderId);
    }

    @Override
    @PostMapping("/payment/failed")
    public OrderDto failPayOrder(UUID orderId) {
        return orderFacade.failPayOrder(orderId);
    }

    @Override
    @PostMapping("/payment/success")
    public OrderDto successPayOrder(UUID orderId) {
        return orderFacade.successPayOrder(orderId);
    }

    @Override
    @PostMapping("/delivery")
    public OrderDto deliverOrder(UUID orderId) {
        return orderFacade.deliverOrder(orderId);
    }

    @Override
    @PostMapping("/delivery/failed")
    public OrderDto failDeliverOrder(UUID orderId) {
        return orderFacade.failDeliverOrder(orderId);
    }

    @Override
    @PostMapping("/completed")
    public OrderDto completeOrder(UUID orderId) {
        return orderFacade.completeOrder(orderId);
    }

    @Override
    @PostMapping("/calculate/total")
    public OrderDto calculateTotalPrice(UUID orderId) {
        return orderFacade.calculateTotalPrice(orderId);
    }

    @Override
    @PostMapping("/calculate/delivery")
    public OrderDto calculateDeliveryPrice(UUID orderId) {
        return orderFacade.calculateDeliveryPrice(orderId);
    }

    @Override
    @PostMapping("/assembly")
    public OrderDto assemblyOrder(UUID orderId) {
        return orderFacade.assemblyOrder(orderId);
    }

    @Override
    @PostMapping("/assembly/failed")
    public OrderDto failAssemblyOrder(UUID orderId) {
        return orderFacade.failAssemblyOrder(orderId);
    }

    @Override
    @GetMapping("/only")
    public OrderDto getOrder(@RequestBody @NotNull UUID orderId) {
        return orderFacade.getOrderById(orderId);
    }
}
