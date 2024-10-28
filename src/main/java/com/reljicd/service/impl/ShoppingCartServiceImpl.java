package com.reljicd.service.impl;

import com.reljicd.exception.NotEnoughProductsInStockException;
import com.reljicd.model.Product;
import com.reljicd.repository.ProductRepository;
import com.reljicd.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Shopping Cart is implemented with a Map, and as a session bean.
 *
 * @author Dusan
 */
@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ProductRepository productRepository;

    private Map<Product, Integer> products = new HashMap<>();

    @Autowired
    public ShoppingCartServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void addProduct(Product product) {
        products.merge(product, 1, Integer::sum);
    }

    @Override
    public void removeProduct(Product product) {
        products.computeIfPresent(product, (p, quantity) -> quantity > 1 ? quantity - 1 : null);
    }

    @Override
    public Map<Product, Integer> getProductsInCart() {
        return Collections.unmodifiableMap(products);
    }

    @Override
    public void checkout() throws NotEnoughProductsInStockException {
        for (Map.Entry<Product, Integer> entry : products.entrySet()) {
            // Find product by ID using findById() (replaces deprecated findOne())
            Optional<Product> productOptional = productRepository.findById(entry.getKey().getId());

            if (productOptional.isEmpty()) {
                throw new NotEnoughProductsInStockException(entry.getKey());
            }

            Product product = productOptional.get();

            if (product.getQuantity() < entry.getValue()) {
                throw new NotEnoughProductsInStockException(product);
            }

            product.setQuantity(product.getQuantity() - entry.getValue());
        }

        // Save each product individually (since save() no longer supports collections)
        products.keySet().forEach(productRepository::save);

        productRepository.flush();
        products.clear();
    }

    @Override
    public BigDecimal getTotal() {
        return products.entrySet().stream()
                .map(entry -> entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }
}
