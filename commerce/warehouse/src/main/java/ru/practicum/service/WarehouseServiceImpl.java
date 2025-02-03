package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.exception.NoSpecifiedProductInWarehouseException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.practicum.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.practicum.mapper.WarehouseMapper;
import ru.practicum.model.Booking;
import ru.practicum.model.WarehouseProduct;
import ru.practicum.repository.BookingRepository;
import ru.practicum.repository.WarehouseRepository;
import ru.practicum.utils.AddressUtil;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final BookingRepository bookingRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    @Transactional
    public void newProductInWarehouse(NewProductInWarehouseRequest request) {
        if (warehouseRepository.existsById(request.getProductId())) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Ошибка, товар с таким описанием уже зарегистрирован на складе");
        }
        warehouseRepository.save(warehouseMapper.map(request));
    }

    @Override
    @Transactional
    public void addProductToWarehouse(AddProductToWarehouseRequest request) {
        WarehouseProduct product = getWarehouseProduct(request.getProductId());
        long newQuantity = product.getQuantity() + request.getQuantity();
        product.setQuantity(newQuantity);
        warehouseRepository.save(product);
    }

    @Override
    @Transactional
    public BookedProductsDto checkProductQuantityEnoughForShoppingCart(ShoppingCartDto shoppingCartDto) {
        log.info("Новый запрос на бронирование");
        Map<UUID, Integer> cartProducts = shoppingCartDto.getProducts();
        Map<UUID, WarehouseProduct> products = getWarehouseProducts(cartProducts.keySet());

        double weight = 0;
        double volume = 0;
        boolean fragile = false;
        for (Map.Entry<UUID, Integer> cartProduct : cartProducts.entrySet()) {
            WarehouseProduct product = products.get(cartProduct.getKey());
            if (cartProduct.getValue() > product.getQuantity()) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Ошибка, товар из корзины не находится в требуемом количестве на складе");
            }
            weight += product.getWeight() * cartProduct.getValue();
            volume += product.getHeight() * product.getWeight() * product.getDepth() * cartProduct.getValue();
            fragile = fragile || product.isFragile();
        }

        return new BookedProductsDto(
                weight,
                volume,
                fragile
        );
    }

    @Override
    public AddressDto getWarehouseAddress() {
        String defValue = AddressUtil.getAddress();
        return new AddressDto(
                defValue,
                defValue,
                defValue,
                defValue,
                defValue
        );
    }

    @Override
    @Transactional
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        Booking booking = getBookingById(request.getOrderId());
        booking.setDeliveryId(request.getDeliveryID());
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void acceptReturn(Map<UUID, Integer> products) {
        Map<UUID, WarehouseProduct> warehouseProducts = getWarehouseProducts(products.keySet());
        for (Map.Entry<UUID, Integer> product : products.entrySet()) {
            WarehouseProduct warehouseProduct = warehouseProducts.get(product.getKey());
            warehouseProduct.setQuantity(warehouseProduct.getQuantity() + product.getValue());
        }
        saveWarehouseRemains(warehouseProducts.values());
    }

    @Override
    @Transactional
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        Map<UUID, Integer> orderProducts = request.getProducts();
        Map<UUID, WarehouseProduct> products = getWarehouseProducts(orderProducts.keySet());

        double weight = 0;
        double volume = 0;
        boolean fragile = false;
        for (Map.Entry<UUID, Integer> cartProduct : orderProducts.entrySet()) {
            WarehouseProduct product = products.get(cartProduct.getKey());
            long newQuantity = product.getQuantity() - cartProduct.getValue();
            if (newQuantity < 0) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Ошибка, товар из корзины не находится в требуемом количестве на складе");
            }
            product.setQuantity(newQuantity);
            weight += product.getWeight() * cartProduct.getValue();
            volume += product.getHeight() * product.getWeight() * product.getDepth() * cartProduct.getValue();
            fragile = fragile || product.isFragile();
        }
        addBooking(request);
        saveWarehouseRemains(products.values());

        return new BookedProductsDto(
                weight,
                volume,
                fragile
        );
    }

    private WarehouseProduct getWarehouseProduct(UUID productId) {
        return warehouseRepository.findById(productId).orElseThrow(
                () -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе")
        );
    }

    private Booking getBookingById(UUID orderId) {
        return bookingRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("Нет информации о бронировании товаров по заказу")
        );
    }

    Map<UUID, WarehouseProduct> getWarehouseProducts(Collection<UUID> ids) {
        Map<UUID, WarehouseProduct> products = warehouseRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(WarehouseProduct::getProductId, Function.identity()));
        if (products.size() != ids.size()) {
            throw new ProductInShoppingCartLowQuantityInWarehouse("Некоторых товаров нет на складе");
        }

        return products;
    }

    void addBooking(AssemblyProductsForOrderRequest request) {
        Booking booking = Booking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build();
        bookingRepository.save(booking);
    }

    void saveWarehouseRemains(Collection<WarehouseProduct> products) {
        warehouseRepository.saveAll(products);
    }
}
