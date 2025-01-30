package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.client.WarehouseClient;
import ru.practicum.dto.*;
import ru.practicum.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/warehouse")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseClient {
    private final WarehouseService warehouseService;

    @Override
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        warehouseService.newProductInWarehouse(request);
    }

    @PostMapping("/check")
    @Override
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductQuantityEnoughForShoppingCart(shoppingCartDto);
    }

    @Override
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        warehouseService.addProductToWarehouse(request);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }

    @Override
    @PostMapping("/shipped")
    public void shippedToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request) {
        warehouseService.shippedToDelivery(request);
    }

    @Override
    @PostMapping("/return")
    public void acceptReturn(@RequestBody @Valid Map<UUID, Integer> products) {
        warehouseService.acceptReturn(products);
    }

    @Override
    @PostMapping("/assembly")
    public BookedProductsDto assemblyProductsForOrder(@RequestBody @Valid AssemblyProductsForOrderRequest request) {
        return warehouseService.assemblyProductsForOrder(request);
    }
}
