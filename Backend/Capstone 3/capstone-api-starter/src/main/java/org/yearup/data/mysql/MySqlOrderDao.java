package org.yearup.data.mysql;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.yearup.data.OrderDao;

import javax.sql.DataSource;
import javax.xml.transform.Result;
import java.math.BigDecimal;
import java.sql.*;

@Component
public class MySqlOrderDao extends MySqlDaoBase implements OrderDao {

    public MySqlOrderDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public int createOrder(int userId, String address, String city, String state, String zip,
                           BigDecimal shippingAmount) {

        String sql = """
            INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount)
            VALUES (?, NOW(), ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, userId);
            ps.setString(2, address);
            ps.setString(3, city);
            ps.setString(4, state);
            ps.setString(5, zip);
            ps.setBigDecimal(6, shippingAmount);

            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if(keys.next()) {
                return keys.getInt(1);
            }
        }
        catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
        throw new RuntimeException("Failed to create order.");
    }

    @Override
    public void addLineItem(int orderId, int productId, BigDecimal salesPrice, int quantity,
                            BigDecimal discount) {

        String sql = """
            INSERT INTO order_line_items
            (order_id, product_id, sales_price, quantity, discount)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection()) {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setBigDecimal(3, salesPrice);
            ps.setInt(4, quantity);
            ps.setBigDecimal(5, discount);

            ps.executeUpdate();
        }
        catch (SQLException exception) {
        throw new RuntimeException(exception);
        }
    }
}
