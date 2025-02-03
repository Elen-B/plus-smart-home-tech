package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.ShoppingCartClient;
import ru.practicum.dto.BookedProductsDto;
import ru.practicum.dto.ChangeProductQuantityRequest;
import ru.practicum.dto.ShoppingCartDto;
import ru.practicum.service.ShoppingCartService;
import ru.practicum.utils.ValidationUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {
    private final ShoppingCartService shoppingCartService;

    @Override
    @GetMapping
    public ShoppingCartDto getShoppingCart(
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE) String userName) {
        return shoppingCartService.getShoppingCart(userName);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProductsToShoppingCart(
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE) String userName,
            Map<UUID, @NotNull Long> products) {
        return shoppingCartService.addProductsToShoppingCart(userName, products);
    }

    @Override
    @DeleteMapping
    public void deleteShoppingCart(@NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE) String userName) {
        shoppingCartService.deleteShoppingCart(userName);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto removeFromShoppingCart(@NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
                                                      String userName,
                                                  List<UUID> products) {
        return shoppingCartService.removeFromShoppingCart(userName, products);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto changeProductQuantity(@NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE)
                                                     String userName,
                                                 @Valid ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(userName, request);
    }

    @Override
    @PostMapping("/booking")
    public BookedProductsDto bookingProductsFromShoppingCart(
            @NotBlank(message = ValidationUtil.VALIDATION_USERNAME_MESSAGE) String userName) {
        return shoppingCartService.bookingProductsFromShoppingCart(userName);
    }
}
