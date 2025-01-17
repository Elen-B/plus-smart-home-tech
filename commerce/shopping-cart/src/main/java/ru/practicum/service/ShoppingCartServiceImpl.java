package ru.practicum.service;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.ChangeProductQuantityRequest;
import ru.practicum.dto.ShoppingCartDto;
import ru.practicum.exception.NoProductsInShoppingCartException;
import ru.practicum.exception.NotAuthorizedUserException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.ShoppingCartMapper;
import ru.practicum.model.ShoppingCart;
import ru.practicum.repository.ShoppingCartRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public ShoppingCartDto getShoppingCart(String userName) {
        checkUser(userName);
        return shoppingCartMapper.map(getShoppingCartByUsername(userName));
    }

    @Override
    @Transactional
    public ShoppingCartDto addProductsToShoppingCart(String userName, Map<UUID, @NotNull Long> products) {
        checkUser(userName);
        ShoppingCart cart = shoppingCartRepository.findByUserName(userName).orElse(
                getNewShoppingCart(userName)
        );
        Map<UUID, Long> cartProducts = new HashMap<>();
        if (!cart.getProducts().isEmpty()) {
            cartProducts.putAll(cart.getProducts());
        }
        cartProducts.putAll(products);
        cart.setProducts(cartProducts);
        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        return shoppingCartMapper.map(savedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto removeFromShoppingCart(String userName, List<UUID> products) {
        checkUser(userName);
        ShoppingCart cart = getShoppingCartByUsername(userName);
        products.forEach(productId -> cart.getProducts().remove(productId));
        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        return shoppingCartMapper.map(savedCart);
    }

    @Override
    @Transactional
    public ShoppingCartDto changeProductQuantity(String userName, ChangeProductQuantityRequest request) {
        checkUser(userName);
        ShoppingCart cart = getShoppingCartByUsername(userName);
        if (!cart.getProducts().containsKey(request.getProductId())) {
            throw new NoProductsInShoppingCartException("Нет искомых товаров в корзине");
        }
        cart.getProducts().put(request.getProductId(), request.getNewQuantity());
        ShoppingCart savedCart = shoppingCartRepository.save(cart);
        return shoppingCartMapper.map(savedCart);
    }

    @Override
    @Transactional
    public void deleteShoppingCart(String userName) {
        checkUser(userName);
        shoppingCartRepository.deleteByUserName(userName);
    }

    private void checkUser(String userName) {
        if (userName.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым");
        }
    }

    private ShoppingCart getShoppingCartByUsername(String userName) {
        return shoppingCartRepository.findByUserName(userName).orElseThrow(
                () -> new NotFoundException("Корзина покупателя не найдена")
        );
    }

    private ShoppingCart getNewShoppingCart(String userName) {
        ShoppingCart cart = new ShoppingCart();
        cart.setUserName(userName);
        return cart;
    }
}
