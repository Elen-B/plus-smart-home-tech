package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.OrderClient;
import ru.practicum.client.WarehouseClient;
import ru.practicum.dto.DeliveryDto;
import ru.practicum.dto.DeliveryState;
import ru.practicum.dto.OrderDto;
import ru.practicum.dto.ShippedToDeliveryRequest;
import ru.practicum.exception.NoDeliveryFoundException;
import ru.practicum.mapper.AddressMapper;
import ru.practicum.mapper.DeliveryMapper;
import ru.practicum.model.Address;
import ru.practicum.model.Delivery;
import ru.practicum.repository.DeliveryRepository;
import ru.practicum.utils.DeliveryUtil;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final AddressMapper addressMapper;

    private final OrderClient orderClient;
    private final WarehouseClient warehouseClient;

    @Override
    @Transactional
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        Delivery delivery = deliveryRepository.save(getNewDelivery(deliveryDto));
        return deliveryMapper.map(delivery);
    }

    @Override
    @Transactional
    public void deliverySuccessful(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.DELIVERED);
        deliveryRepository.save(delivery);
        orderClient.deliverOrder(orderId);
    }

    @Override
    @Transactional
    public void deliveryPicked(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
        deliveryRepository.save(delivery);

        ShippedToDeliveryRequest request = getNewShippedToDeliveryRequest(delivery);
        warehouseClient.shippedToDelivery(request);
        orderClient.assemblyOrder(delivery.getOrderId());
    }

    @Override
    @Transactional
    public void deliveryFailed(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        delivery.setDeliveryState(DeliveryState.FAILED);
        deliveryRepository.save(delivery);
        orderClient.failDeliverOrder(orderId);
    }

    @Override
    public Double deliveryCost(OrderDto orderDto) {
        Delivery delivery = getDeliveryByOrderId(orderDto.getOrderId());
        double cost = DeliveryUtil.BASE_DELIVERY_PRICE;
        cost += DeliveryUtil.BASE_DELIVERY_PRICE * getCoefByFromAddress(delivery.getFromAddress());
        cost *= getCoefByFragile(orderDto.isFragile());
        cost += orderDto.getDeliveryWeight() * 0.3;
        cost += orderDto.getDeliveryVolume() * 0.2;
        cost *= getCoefByToAddress(delivery.getFromAddress(), delivery.getToAddress());
        return cost;
    }

    double getCoefByFromAddress(Address address) {
        String addressStr = address.toString();
        if (addressStr.contains("ADDRESS_1")) {
            return DeliveryUtil.ADDRESS_1_ADDRESS_COEF;
        } else if (addressStr.contains("ADDRESS_2")) {
            return DeliveryUtil.ADDRESS_2_ADDRESS_COEF;
        } else {
            return DeliveryUtil.BASE_ADDRESS_COEF;
        }
    }

    double getCoefByToAddress(Address from, Address to) {
        if (!from.getStreet().equals(to.getStreet())) {
            return DeliveryUtil.DIFF_STREET_ADDRESS_COEF;
        }

        return 1.0;
    }

    double getCoefByFragile(boolean isFragile) {
        if (isFragile) {
            return DeliveryUtil.FRAGILE_COEF;
        }

        return 1.0;
    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        return deliveryRepository.findByOrderId(orderId).orElseThrow(
                () -> new NoDeliveryFoundException("Не найдена доставка")
        );
    }

    private ShippedToDeliveryRequest getNewShippedToDeliveryRequest(Delivery delivery) {
        return new ShippedToDeliveryRequest(
                delivery.getOrderId(),
                delivery.getDeliveryId()
        );
    }

    private Delivery getNewDelivery(DeliveryDto deliveryDto) {
        OrderDto orderDto = orderClient.getOrder(deliveryDto.getOrderId());
        return Delivery.builder()
                .orderId(deliveryDto.getOrderId())
                .fromAddress(addressMapper.map(deliveryDto.getFromAddress()))
                .toAddress(addressMapper.map(deliveryDto.getToAddress()))
                .deliveryWeight(orderDto.getDeliveryWeight())
                .deliveryVolume(orderDto.getDeliveryVolume())
                .fragile(orderDto.isFragile())
                .deliveryState(DeliveryState.CREATED)
                .build();
    }
}
