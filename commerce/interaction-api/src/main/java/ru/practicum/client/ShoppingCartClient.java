package ru.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.BookedProductsDto;
import ru.practicum.dto.ChangeProductQuantityRequest;
import ru.practicum.dto.ShoppingCartDto;
import ru.practicum.utils.ValidationUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping("/api/v1/shopping-cart")
    ShoppingCartDto getShoppingCart(
            @RequestParam(name = "username")
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
            String userName);

    @PutMapping("/api/v1/shopping-cart")
    ShoppingCartDto addProductsToShoppingCart(
            @RequestParam(name = "username")
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
            String userName,
            @RequestBody Map<UUID, @NotNull Long> products);

    @DeleteMapping("/api/v1/shopping-cart")
    void deleteShoppingCart(
            @RequestParam(name = "username")
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
            String userName);

    @PostMapping("/api/v1/shopping-cart/remove")
    ShoppingCartDto removeFromShoppingCart(
            @RequestParam(name = "username")
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
            String userName,
            @RequestBody List<UUID> products);

    @PostMapping("/api/v1/shopping-cart/change-quantity")
    ShoppingCartDto changeProductQuantity(
            @RequestParam(name = "username")
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
            String userName,
            @RequestBody @Valid ChangeProductQuantityRequest request);

    @PostMapping("/api/v1/shopping-cart/booking")
    BookedProductsDto bookingProductsFromShoppingCart(
            @RequestParam(name = "username")
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
            String userName);
}
