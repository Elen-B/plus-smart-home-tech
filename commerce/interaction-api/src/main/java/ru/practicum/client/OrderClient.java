package ru.practicum.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CreateNewOrderRequest;
import ru.practicum.dto.OrderDto;
import ru.practicum.dto.ProductReturnRequest;
import ru.practicum.utils.ValidationUtil;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "order")
public interface OrderClient {
    @GetMapping("/api/v1/order")
    List<OrderDto> getClientOrders(
            @RequestParam(name = "username")
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
            String userName);

    @PutMapping("/api/v1/order")
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping("/api/v1/order/return")
    OrderDto returnProducts(@RequestBody ProductReturnRequest request);

    @PostMapping("/api/v1/order/payment")
    OrderDto payOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/payment/failed")
    OrderDto failPayOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/delivery")
    OrderDto deliverOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/delivery/failed")
    OrderDto failDeliverOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/completed")
    OrderDto completeOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/calculate/total")
    OrderDto calculateTotalPrice(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/calculate/delivery")
    OrderDto calculateDeliveryPrice(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/assembly")
    OrderDto assemblyOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/api/v1/order/assembly/failed")
    OrderDto failAssemblyOrder(@RequestBody @NotNull UUID orderId);
}
