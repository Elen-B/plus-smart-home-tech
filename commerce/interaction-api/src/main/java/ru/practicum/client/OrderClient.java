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

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {
    @GetMapping
    List<OrderDto> getClientOrders(
            @RequestParam(name = "username")
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
            String userName);

    @PutMapping
    OrderDto createNewOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto returnProducts(@RequestBody ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto payOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto failPayOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/success")
    OrderDto successPayOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrderDto deliverOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto failDeliverOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/completed")
    OrderDto completeOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto calculateTotalPrice(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto calculateDeliveryPrice(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly")
    OrderDto assemblyOrder(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto failAssemblyOrder(@RequestBody @NotNull UUID orderId);

    @GetMapping("/only")
    OrderDto getOrder(@RequestBody @NotNull UUID orderId);
}
