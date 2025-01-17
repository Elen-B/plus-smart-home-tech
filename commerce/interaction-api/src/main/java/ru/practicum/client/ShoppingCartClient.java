package ru.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.BookedProductsDto;
import ru.practicum.dto.ChangeProductQuantityRequest;
import ru.practicum.dto.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart")
public interface ShoppingCartClient {

    @GetMapping
    ShoppingCartDto getShoppingCart(@RequestParam(name = "username") String userName);

    @PutMapping
    ShoppingCartDto addProductsToShoppingCart(@RequestParam(name = "username") String userName,
                                              @RequestBody Map<UUID, @NotNull Long> products);

    @DeleteMapping
    void deleteShoppingCart(@RequestParam(name = "username") String userName);

    @PostMapping("/remove")
    ShoppingCartDto removeFromShoppingCart(@RequestParam(name = "username") String userName,
                                           @RequestBody List<UUID> products);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeProductQuantity(@RequestParam(name = "username") String userName,
                                          @RequestBody @Valid ChangeProductQuantityRequest request);

    @PostMapping("/booking")
    BookedProductsDto bookingProductsFromShoppingCart(@RequestParam(name = "username") String userName);
}
