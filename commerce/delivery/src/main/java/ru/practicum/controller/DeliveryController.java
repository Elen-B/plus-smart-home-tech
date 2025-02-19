package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.DeliveryClient;
import ru.practicum.dto.DeliveryDto;
import ru.practicum.dto.OrderDto;
import ru.practicum.service.DeliveryService;

import java.util.UUID;


@Validated
@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {
    private final DeliveryService deliveryService;

    @Override
    @PutMapping
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        return deliveryService.planDelivery(deliveryDto);
    }

    @Override
    @PostMapping("/successful")
    public void deliverySuccessful(UUID orderId) {
        deliveryService.deliverySuccessful(orderId);
    }

    @Override
    @PostMapping("/picked")
    public void deliveryPicked(UUID orderId) {
        deliveryService.deliveryPicked(orderId);
    }

    @Override
    @PostMapping("/failed")
    public void deliveryFailed(UUID orderId) {
        deliveryService.deliveryFailed(orderId);
    }

    @Override
    @PostMapping("/cost")
    public Double deliveryCost(OrderDto orderDto) {
        return deliveryService.deliveryCost(orderDto);
    }
}
