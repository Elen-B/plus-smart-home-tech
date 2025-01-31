package ru.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.Pageable;
import ru.practicum.dto.ProductCategory;
import ru.practicum.dto.ProductDto;
import ru.practicum.dto.SetProductQuantityStateRequest;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store")
public interface ShoppingStoreClient {
    @GetMapping("/api/v1/shopping-store")
    List<ProductDto> getProducts(@RequestParam(name = "category") @NotNull ProductCategory category,
                                 Pageable pageable);

    @GetMapping("/api/v1/shopping-store/{productId}")
    ProductDto getProduct(@PathVariable UUID productId);

    @PutMapping("/api/v1/shopping-store")
    ProductDto createNewProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/api/v1/shopping-store")
    ProductDto updateProduct(@RequestBody @Valid ProductDto productDto);

    @PostMapping("/api/v1/shopping-store/removeProductFromStore")
    boolean removeProductFromStore(@RequestBody UUID productId);

    @PostMapping("/api/v1/shopping-store/quantityState")
    boolean setProductQuantityState(SetProductQuantityStateRequest request);

    @GetMapping("/api/v1/shopping-store/onlyIds")
    List<ProductDto> getProductByIds(@RequestParam Collection<UUID> ids);
}
