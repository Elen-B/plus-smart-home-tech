package ru.practicum.facade;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.DeliveryClient;
import ru.practicum.client.PaymentClient;
import ru.practicum.client.ShoppingCartClient;
import ru.practicum.client.WarehouseClient;
import ru.practicum.dto.*;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.model.Order;
import ru.practicum.service.OrderService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFacadeImpl implements OrderFacade {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    private final ShoppingCartClient shoppingCartClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;

    @Override
    public List<OrderDto> getClientOrders(String userName) {
        log.info("get orders for user {}", userName);
        return orderService.getClientOrders(userName)
                .stream()
                .map(orderMapper::map)
                .toList();
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        Order order = orderService.createNewOrder(getNewOrderFromRequest(request));
        log.info("new order from request: {}", order);

        UUID deliveryId = getNewDeliveryId(order.getOrderId(), request.getDeliveryAddress());
        return orderMapper.map(orderService.setDelivery(order.getOrderId(), deliveryId));
    }

    @Override
    public OrderDto returnProducts(ProductReturnRequest request) {
        warehouseClient.acceptReturn(request.getProducts());

        return orderMapper.map(orderService.returnProducts(request.getOrderId()));
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        Order order = orderService.getOrderById(orderId);
        double productCost = paymentClient.getProductCost(orderMapper.map(order));
        double deliveryCost = deliveryClient.deliveryCost(orderMapper.map(order));
        order.setDeliveryPrice(deliveryCost);
        order.setProductPrice(productCost);
        log.info("order after setting productPrice: {}", order);
        double totalCost = paymentClient.getTotalCost(orderMapper.map(order));
        order.setTotalPrice(totalCost);
        PaymentDto paymentDto = paymentClient.createPayment(orderMapper.map(order));
        order.setPaymentId(paymentDto.getPaymentId());

        Order savedOrder = orderService.savePaymentInfo(order);
        log.info("payOrder: order after creating payment {}", savedOrder);
        return orderMapper.map(savedOrder);
    }

    @Override
    public OrderDto successPayOrder(UUID orderId) {
        return orderMapper.map(orderService.successPayOrder(orderId));
    }

    @Override
    public OrderDto failPayOrder(UUID orderId) {
        return orderMapper.map(orderService.failPayOrder(orderId));
    }

    @Override
    public OrderDto deliverOrder(UUID orderId) {
        return orderMapper.map(orderService.deliverOrder(orderId));
    }

    @Override
    public OrderDto failDeliverOrder(UUID orderId) {
        return orderMapper.map(orderService.failDeliverOrder(orderId));
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        return orderMapper.map(orderService.completeOrder(orderId));
    }

    @Override
    public OrderDto calculateTotalPrice(UUID orderId) {
        Order order = orderService.getOrderById(orderId);
        double totalCost = paymentClient.getTotalCost(orderMapper.map(order));

        return orderMapper.map(orderService.setTotalPrice(orderId, totalCost));
    }

    @Override
    public OrderDto calculateDeliveryPrice(UUID orderId) {
        Order order = orderService.getOrderById(orderId);
        double deliveryCost = deliveryClient.deliveryCost(orderMapper.map(order));

        return orderMapper.map(orderService.setDeliveryPrice(orderId, deliveryCost));
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        warehouseClient.assemblyProductsForOrder(getNewAssemblyProductsForOrderRequest(orderId));

        return orderMapper.map(orderService.assemblyOrder(orderId));
    }

    @Override
    public OrderDto failAssemblyOrder(UUID orderId) {
        return orderMapper.map(orderService.failAssemblyOrder(orderId));
    }

    @Override
    public OrderDto getOrderById(UUID orderId) {
        return orderMapper.map(orderService.getOrderById(orderId));
    }

    private AssemblyProductsForOrderRequest getNewAssemblyProductsForOrderRequest(UUID orderId) {
        Order order = orderService.getOrderById(orderId);
        AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest();
        request.setOrderId(orderId);
        request.setProducts(order.getProducts());

        return request;
    }

    private Order getNewOrderFromRequest(CreateNewOrderRequest request) {
        BookedProductsDto bookedProductsDto = shoppingCartClient.bookingProductsFromShoppingCart(request.getUserName());

        return Order.builder()
                .userName(request.getUserName())
                .cartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .deliveryWeight(bookedProductsDto.getDeliveryWeight())
                .deliveryVolume(bookedProductsDto.getDeliveryVolume())
                .fragile(bookedProductsDto.getFragile())
                .state(OrderState.NEW)
                .build();
    }

    private UUID getNewDeliveryId(UUID orderId, AddressDto deliveryAddress) {
        DeliveryDto deliveryDto = new DeliveryDto();
        deliveryDto.setFromAddress(warehouseClient.getWarehouseAddress());
        deliveryDto.setToAddress(deliveryAddress);
        deliveryDto.setOrderId(orderId);
        deliveryDto.setDeliveryState(DeliveryState.CREATED);
        log.info("new DeliveryDto: {}", deliveryDto);

        return deliveryClient.planDelivery(deliveryDto).getDeliveryId();
    }
}
