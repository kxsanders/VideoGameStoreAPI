package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrderDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("/orders")
public class OrdersController {

    private final ShoppingCartDao cartDao;
    private final UserDao userDao;
    private final ProfileDao profileDao;
    private final OrderDao orderDao;

    @Autowired
    public OrdersController(ShoppingCartDao cartDao,
                            UserDao userDao,
                            ProfileDao profileDao,
                            OrderDao orderDao) {
        this.cartDao = cartDao;
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.orderDao = orderDao;
    }

    @PostMapping("/orders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> checkout(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 1. Identify user
            String username = principal.getName();
            int userId = userDao.getIdByUsername(username);

            // 2. Get cart
            ShoppingCart cart = cartDao.getByUserId(userId);
            if (cart == null || cart.getItems().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            // 3. Get shipping info from profile
            Profile profile = profileDao.getByUserId(userId);

            // (simple flat shipping)
            BigDecimal shippingAmount = new BigDecimal("9.99");

            // 4. Create order
            int orderId = orderDao.createOrder(
                    userId,
                    profile.getAddress(),
                    profile.getCity(),
                    profile.getState(),
                    profile.getZip(),
                    shippingAmount
            );

            // 5. Create line items
            for (ShoppingCartItem item : cart.getItems().values()) {
                orderDao.addLineItem(
                        orderId,
                        item.getProductId(),
                        item.getProduct().getPrice(),
                        item.getQuantity(),
                        item.getDiscountPercent()
                );
            }

            // 6. Clear cart
            cartDao.clearCart(userId);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Checkout failed");
        }
    }
}
