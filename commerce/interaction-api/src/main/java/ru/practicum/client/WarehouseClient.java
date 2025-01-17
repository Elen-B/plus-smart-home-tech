package ru.practicum.client;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.dto.*;

@FeignClient(name = "warehouse")
public interface WarehouseClient {

    @PutMapping
    void newProductInWarehouse(@RequestBody @Valid NewProductInWarehouseRequest request);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantityEnoughForShoppingCart(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void addProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest request);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}
