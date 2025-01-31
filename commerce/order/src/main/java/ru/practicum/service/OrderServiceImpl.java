package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.DeliveryClient;
import ru.practicum.client.PaymentClient;
import ru.practicum.client.ShoppingCartClient;
import ru.practicum.client.WarehouseClient;
import ru.practicum.dto.*;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.OrderMapper;
import ru.practicum.model.Order;
import ru.practicum.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    private final ShoppingCartClient shoppingCartClient;
    private final PaymentClient paymentClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;

    @Override
    public List<OrderDto> getClientOrders(String userName) {
        log.info("get orders for user {}", userName);
        return orderRepository.findAllByUserName(userName)
                .stream()
                .map(orderMapper::map)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        Order order = getNewOrderFromRequest(request);
        log.info("new order from request: {}", order);

        Order savedOrder = orderRepository.save(order);
        savedOrder = planDelivery(savedOrder.getOrderId(), request.getDeliveryAddress());
        return orderMapper.map(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto returnProducts(ProductReturnRequest request) {
        warehouseClient.acceptReturn(request.getProducts());

        return orderMapper.map(updateOrderState(request.getOrderId(), OrderState.PRODUCT_RETURNED));
    }

    @Override
    @Transactional
    public OrderDto payOrder(UUID orderId) {
        Order order = findOrderById(orderId);
        double productCost = paymentClient.getProductCost(orderMapper.map(order));
        double deliveryCost = deliveryClient.deliveryCost(orderMapper.map(order));
        order.setDeliveryPrice(deliveryCost);
        order.setProductPrice(productCost);
        log.info("order after setting productPrice: {}", order);
        double totalCost = paymentClient.getTotalCost(orderMapper.map(order));
        order.setTotalPrice(totalCost);
        PaymentDto paymentDto = paymentClient.createPayment(orderMapper.map(order));
        order.setPaymentId(paymentDto.getPaymentId());
        order.setState(OrderState.ON_PAYMENT);
        Order savedOrder = orderRepository.save(order);
        log.info("payOrder: order after creating payment {}", savedOrder);
        return orderMapper.map(savedOrder);
    }

    @Override
    public OrderDto successPayOrder(UUID orderId) {
        return orderMapper.map(updateOrderState(orderId, OrderState.PAID));
    }

    @Override
    @Transactional
    public OrderDto failPayOrder(UUID orderId) {
        return orderMapper.map(updateOrderState(orderId, OrderState.PAYMENT_FAILED));
    }

    @Override
    public OrderDto deliverOrder(UUID orderId) {
        return orderMapper.map(updateOrderState(orderId, OrderState.DELIVERED));
    }

    @Override
    @Transactional
    public OrderDto failDeliverOrder(UUID orderId) {
        return orderMapper.map(updateOrderState(orderId, OrderState.DELIVERY_FAILED));
    }

    @Override
    @Transactional
    public OrderDto completeOrder(UUID orderId) {
        return orderMapper.map(updateOrderState(orderId, OrderState.COMPLETED));
    }

    @Override
    public OrderDto calculateTotalPrice(UUID orderId) {
        Order order = findOrderById(orderId);
        double totalCost = paymentClient.getTotalCost(orderMapper.map(order));
        order.setTotalPrice(totalCost);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.map(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto calculateDeliveryPrice(UUID orderId) {
        Order order = findOrderById(orderId);
        double deliveryCost = deliveryClient.deliveryCost(orderMapper.map(order));
        order.setDeliveryPrice(deliveryCost);
        Order savedOrder = orderRepository.save(order);

        return orderMapper.map(savedOrder);
    }

    @Override
    @Transactional
    public OrderDto assemblyOrder(UUID orderId) {
        warehouseClient.assemblyProductsForOrder(getNewAssemblyProductsForOrderRequest(orderId));

        return orderMapper.map(updateOrderState(orderId, OrderState.ASSEMBLED));
    }

    @Override
    @Transactional
    public OrderDto failAssemblyOrder(UUID orderId) {
        return orderMapper.map(updateOrderState(orderId, OrderState.ASSEMBLY_FAILED));
    }

    @Override
    public OrderDto getOrderById(UUID orderId) {
        log.info("==> get order by id = {}", orderId);
        return orderMapper.map(findOrderById(orderId));
    }

    private Order updateOrderState(UUID orderId, OrderState newState) {
        Order order = findOrderById(orderId);
        order.setState(newState);
        return orderRepository.save(order);
    }

    private Order getOrderByUserName(String userName) {
        return orderRepository.findByUserName(userName).orElseThrow(
                () -> new NotFoundException("Заказ покупателя не найден")
        );
    }

    private Order findOrderById(UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("Заказ покупателя не найден")
        );
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

    private Order planDelivery(UUID orderId, AddressDto deliveryAddress) {
        Order order = findOrderById(orderId);
        order.setDeliveryId(getNewDeliveryId(orderId, deliveryAddress));

        return orderRepository.save(order);
    }

    private AssemblyProductsForOrderRequest getNewAssemblyProductsForOrderRequest(UUID orderId) {
        Order order = findOrderById(orderId);
        AssemblyProductsForOrderRequest request = new AssemblyProductsForOrderRequest();
        request.setOrderId(orderId);
        request.setProducts(order.getProducts());

        return request;
    }
}
