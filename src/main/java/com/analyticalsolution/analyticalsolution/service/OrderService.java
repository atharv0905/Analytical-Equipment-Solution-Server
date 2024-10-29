/**
 * File: OrderService.java
 * Author: Atharv Mirgal
 * Description: This service class contains business logic related to orders, providing methods for
 *              processing cart checkouts and retrieving user order histories. It interacts with
 *              the `UserRepository` to manage user data and uses `JdbcTemplate` for database operations.
 *              The class is responsible for creating new orders, managing the checkout process, and
 *              compiling order history responses. It utilizes utility services for JSON parsing and
 *              logging functionalities for tracking operations and errors. The service ensures
 *              proper handling of user authentication and integrates with other services such as
 *              `CartService` to facilitate a seamless order management experience.
 * Created on: 15/10/2024
 * Last Modified: 29/10/2024
 */

package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.Sale;
import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.responses.InvoiceResponse;
import com.analyticalsolution.analyticalsolution.responses.OrderHistoryResponse;
import com.analyticalsolution.analyticalsolution.responses.ProductInvoiceResponse;
import com.analyticalsolution.analyticalsolution.utils.UtilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.*;

@Slf4j
@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UtilityService utilityService;

    @Autowired
    private CartService cartService;

//    @Value("${app.base-url}")
    private String BASE_URL = "http://localhost:3000/";

    // Cart checkout
    @Transactional
    public void checkout(Sale sale) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName().toString());
            if (existingUser == null) {
                log.error("User not found.");
                return;
            }

            String customerID = existingUser.getId();

            // Check if the cart exists for the user
            String sql = "SELECT cart_id FROM cart WHERE customer_id = ?";
            List<String> cartIds = jdbcTemplate.queryForList(sql, new Object[]{customerID}, String.class);

            if (!cartIds.isEmpty()) {
                // If cart exists, get the cart ID
                String cartId = cartIds.get(0);

                // Fetch existing item_ids from the cart
                String selectItemIdsSql = "SELECT item_ids FROM cart WHERE cart_id = ?";
                String itemIdsJson = jdbcTemplate.queryForObject(selectItemIdsSql, new Object[]{cartId}, String.class);

                // Convert JSON string to a list of item IDs
                List<String> itemIds = utilityService.parseJsonToList(itemIdsJson);

                // Generate a unique sale_id, transaction_id, and invoice_number
                String saleId = UUID.randomUUID().toString();
                String createOrderSql = "INSERT INTO orders (order_id, sale_id, product_id, quantity) VALUES (?, ?, ?, ?)";

                // Create a new Sale entry in the sales table
                String createSaleSql = "INSERT INTO sales (sale_id, customer_id, order_confirmation_status, " +
                        "order_status, shipping_address, contact_phone, transaction_id, payment_status, invoice_number, sale_mode) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                // Default values for order status and sales mode
                String defaultOrderConfirmationStatus = "PENDING";
                String defaultOrderStatus = "PENDING";
                String defaultSalesMode = "ONLINE";
                String defaultPaymentStatus = "PAID";

                // Use provided values or fallback to default if null
                String orderConfirmationStatus = sale.getOrder_confirmation_status() != null ? sale.getOrder_confirmation_status() : defaultOrderConfirmationStatus;
                String orderStatus = sale.getOrder_status() != null ? sale.getOrder_status() : defaultOrderStatus;
                String saleMode = sale.getSale_mode() != null ? sale.getSale_mode() : defaultSalesMode;
                String paymentStatus = sale.getPayment_status() != null ? sale.getPayment_status() : defaultPaymentStatus;

                // Insert into the sales table
                jdbcTemplate.update(createSaleSql,
                        saleId,
                        customerID,
                        orderConfirmationStatus,
                        orderStatus,
                        sale.getShipping_address(),
                        sale.getContact_phone(),
                        sale.getTransaction_id(),
                        paymentStatus,
                        sale.getInvoice_number(),
                        saleMode
                );

                for (String itemId : itemIds) {
                    // Retrieve product_id and quantity for each item_id from cartitems table
                    String selectProductSql = "SELECT product_id, quantity FROM cartitems WHERE item_id = ?";
                    Map<String, Object> cartItemData = jdbcTemplate.queryForMap(selectProductSql, itemId);

                    String productId = (String) cartItemData.get("product_id");
                    long quantity = (long) cartItemData.get("quantity");

                    // Generate a unique order_id
                    String orderId = UUID.randomUUID().toString();

                    // Create a new entry in the orders table
                    jdbcTemplate.update(createOrderSql, orderId, saleId, productId, quantity);

                    // Delete item from cart
                    cartService.deleteItemFromCart(itemId);
                }

                log.info("Order and Sale successfully created for user: " + existingUser.getUsername());
            } else {
                log.info("No cart found for user: " + existingUser.getUsername());
            }

        } catch (Exception e) {
            log.error("Error occurred while placing order", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    // Fetch previous user orders
    @Transactional
    public List<OrderHistoryResponse> getOrderHistoryByUser() {
        try {
            List<OrderHistoryResponse> orderHistoryList = new ArrayList<>();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName().toString());
            if (existingUser == null) {
                log.error("User not found.");
                return null;
            }

            String customerID = existingUser.getId();

            // Check if the sale's data exists for the user
            String selectSalesSql = "SELECT sale_id, order_ids FROM sales WHERE customer_id = ?";
            List<Map<String, Object>> salesData = jdbcTemplate.queryForList(selectSalesSql, new Object[]{customerID});

            if (!salesData.isEmpty()) {
                for (Map<String, Object> sale : salesData) {
                    String orderIdsJson = (String) sale.get("order_ids");

                    // Convert JSON string to a list of order IDs
                    List<String> orderIds = utilityService.parseJsonToList(orderIdsJson);

                    for (String orderId : orderIds) {
                        // Fetch the order details using the order ID
                        String selectOrderSql = "SELECT o.product_id, o.quantity, o.order_date, p.product_name, p.product_price, p.product_images " +
                                "FROM orders o " +
                                "JOIN products p ON o.product_id = p.product_id " +
                                "WHERE o.order_id = ?";

                        Map<String, Object> orderData = jdbcTemplate.queryForMap(selectOrderSql, new Object[]{orderId});

                        // Extract the first product image directly from the JSON array
                        String productImagesJson = (String) orderData.get("product_images");
                        List<String> images = utilityService.parseJsonToList(productImagesJson);
                        String productImage = images.isEmpty() ? null : images.get(0);
                        productImage = BASE_URL + productImage;

                        // Construct OrderHistoryResponse
                        OrderHistoryResponse orderHistoryResponse = new OrderHistoryResponse(
                                orderId,
                                (String) orderData.get("product_id"),
                                (String) orderData.get("product_name"), // Extract the product_name
                                orderData.get("product_price").toString(),
                                (Long) orderData.get("quantity"),
                                productImage,
                                orderData.get("order_date").toString()
                        );

                        // Add to the list
                        orderHistoryList.add(orderHistoryResponse);
                    }
                }
            } else {
                log.info("No sales data found for user: " + existingUser.getUsername());
            }

            return orderHistoryList;
        } catch (Exception e) {
            log.error("Error while fetching order history", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }
    }

    public InvoiceResponse generateInvoice(String saleID) {
        try {
            InvoiceResponse invoiceResponse = new InvoiceResponse();

            // Fetch the basic sale details (sale_id and order_date)
            String fetchSale = "SELECT order_date FROM sales WHERE sale_id = ?";
            Date orderDate = jdbcTemplate.queryForObject(fetchSale, new Object[]{saleID}, Date.class);

            invoiceResponse.setSale_id(saleID);
            invoiceResponse.setOrder_date(orderDate);

            // Fetch sale and product details
            String saleDetails = "SELECT " +
                    "    s.sale_id, " +
                    "    u.name AS customer_name, " +
                    "    u.email AS customer_email, " +
                    "    s.contact_phone AS customer_phone, " +
                    "    s.shipping_address AS shipping_address, " +
                    "    o.product_id, " +
                    "    p.product_name, " +
                    "    p.product_price, " +
                    "    o.quantity, " +
                    "    (p.product_price * o.quantity) AS order_total " +
                    "FROM sales s " +
                    "JOIN orders o ON s.sale_id = o.sale_id " +
                    "JOIN users u ON s.customer_id = u.id " +
                    "JOIN products p ON o.product_id = p.product_id " +
                    "WHERE s.sale_id = ?";

            // Mapping the fetched data to the InvoiceResponse and ProductInvoiceResponse
            List<ProductInvoiceResponse> products = jdbcTemplate.query(saleDetails, new Object[]{saleID}, (rs, rowNum) -> {
                ProductInvoiceResponse product = new ProductInvoiceResponse();
                product.setProduct_name(rs.getString("product_name"));
                product.setQuantity(rs.getLong("quantity"));
                product.setProduct_price(rs.getLong("product_price"));
                product.setTotal_price(rs.getLong("order_total"));
                return product;
            });

            List<InvoiceResponse> invoiceResponses = jdbcTemplate.query(saleDetails, new Object[]{saleID}, (rs, rowNum) -> {
                InvoiceResponse user = new InvoiceResponse();
                user.setCustomer_email(rs.getString("customer_email"));
                user.setCustomer_name(rs.getString("customer_name"));
                user.setCustomer_phone(rs.getLong("customer_phone"));
                user.setShipping_address(rs.getString("shipping_address"));
                return user;
            });

            invoiceResponse.setCustomer_name(invoiceResponses.get(0).getCustomer_name());
            invoiceResponse.setCustomer_email(invoiceResponses.get(0).getCustomer_email());
            invoiceResponse.setCustomer_phone(invoiceResponses.get(0).getCustomer_phone());
            invoiceResponse.setShipping_address(invoiceResponses.get(0).getShipping_address());

            String company_name = "Analytical Equipment Solutions";
            invoiceResponse.setCompany_name(company_name);

            String company_address = " Jamil Nagar, Bhandup (W), Mumbai - 400078";
            invoiceResponse.setCompany_address(company_address);

            String company_email = "official@analyticalequipmentsolutions.com";
            invoiceResponse.setCompany_email(company_email);

            String company_phone = "8268393857";
            invoiceResponse.setCompany_phone(company_phone);

            invoiceResponse.setProducts(products);

            // Calculate subtotal by summing all product total prices
            long subtotal = products.stream().mapToLong(ProductInvoiceResponse::getTotal_price).sum();

            // Calculate gst_cost as 18% of subtotal
            long gst_cost = Math.round(subtotal * 0.18);
            invoiceResponse.setGst_cost(gst_cost);

            // Set shipping cost
            long shipping_cost = 3000L;
            invoiceResponse.setShipping_cost(shipping_cost);

            // Calculate total cost
            long total_cost = subtotal + gst_cost + shipping_cost;
            invoiceResponse.setTotal_cost(total_cost);

            return invoiceResponse;
        } catch (Exception e) {
            log.error("Error fetching data for generating invoice: " + e.getMessage());
            return null;
        }
    }

}
