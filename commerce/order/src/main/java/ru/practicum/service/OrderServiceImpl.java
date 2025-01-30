package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.client.ShoppingCartClient;
import ru.practicum.dto.CreateNewOrderRequest;
import ru.practicum.dto.OrderDto;
import ru.practicum.dto.OrderState;
import ru.practicum.dto.ProductReturnRequest;
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

    @Override
    public List<OrderDto> getClientOrders(String userName) {
        log.info("get orders for user {}", userName);
        return orderRepository.findAllByUserName(userName)
                .stream()
                .map(orderMapper::map)
                .toList();
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        Order order = getNewOrderFromRequest(request);
        log.info("new order from request: {}", order);

        Order savedOrder = orderRepository.save(order);
        return orderMapper.map(savedOrder);
    }

    @Override
    public OrderDto returnProducts(ProductReturnRequest request) {
        return null;
    }

    @Override
    public OrderDto payOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto failPayOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto deliverOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto failDeliverOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto completeOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateTotalPrice(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto calculateDeliveryPrice(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto assemblyOrder(UUID orderId) {
        return null;
    }

    @Override
    public OrderDto failAssemblyOrder(UUID orderId) {
        return null;
    }

    private Order getOrderByUsername(String userName) {
        return orderRepository.findByUserName(userName).orElseThrow(
                () -> new NotFoundException("Заказ покупателя не найден")
        );
    }

    private Order getNewOrderFromRequest(CreateNewOrderRequest request) {
        return Order.builder()
                .userName(request.getUserName())
                .cartId(request.getShoppingCart().getShoppingCartId())
                .products(request.getShoppingCart().getProducts())
                .state(OrderState.NEW)
                .build();
    }
}
