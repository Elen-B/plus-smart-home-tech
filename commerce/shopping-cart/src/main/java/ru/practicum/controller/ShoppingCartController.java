package ru.practicum.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.ShoppingCartClient;
import ru.practicum.dto.BookedProductsDto;
import ru.practicum.dto.ChangeProductQuantityRequest;
import ru.practicum.dto.ShoppingCartDto;
import ru.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shopping-cart")
@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartClient {
    private final ShoppingCartService shoppingCartService;

    @Override
    public ShoppingCartDto getShoppingCart(String userName) {
        return shoppingCartService.getShoppingCart(userName);
    }

    @Override
    public ShoppingCartDto addProductsToShoppingCart(String userName, Map<UUID, @NotNull Long> products) {
        return shoppingCartService.addProductsToShoppingCart(userName, products);
    }

    @Override
    public void deleteShoppingCart(String userName) {
        shoppingCartService.deleteShoppingCart(userName);
    }

    @Override
    public ShoppingCartDto removeFromShoppingCart(String userName, List<UUID> products) {
        return shoppingCartService.removeFromShoppingCart(userName, products);
    }

    @Override
    public ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest request) {
        return shoppingCartService.changeProductQuantity(userName, request);
    }

    @Override
    public BookedProductsDto bookingProductsFromShoppingCart(String userName) {
        return null;
    }
}
