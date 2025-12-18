package org.yearup.data;

import java.math.BigDecimal;

public interface OrderDao {

    int createOrder(int userId, String address, String city, String state, String zip, BigDecimal shippingAmount);

    void addLineItem(int orderId, int productId, BigDecimal salesPrice, int quantity, BigDecimal discount);
}

