package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.OrderDto;
import ru.practicum.dto.PaymentDto;

import java.util.UUID;

@FeignClient(name = "payment")
public interface PaymentClient {

    @PostMapping("/api/v1/payment")
    PaymentDto createPayment(@RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/totalCost")
    Double getTotalCost(@RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/refund")
    void paymentSuccess(@RequestBody UUID orderId);

    @PostMapping("/api/v1/payment/productCost")
    Double getProductCost(@RequestBody OrderDto order);

    @PostMapping("/api/v1/payment/failed")
    void paymentFailed(@RequestBody UUID orderId);
}
