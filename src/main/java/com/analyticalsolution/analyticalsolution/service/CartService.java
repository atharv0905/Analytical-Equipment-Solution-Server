/**
 * File: CartService.java
 * Author: Atharv Mirgal
 * Description: This service manages cart-related operations for the e-commerce platform, handling
 *              adding, updating, and deleting items in the cart for authenticated users. It allows
 *              users to maintain a list of cart items, facilitating the creation of new carts or
 *              updating existing ones with item details. The service also supports fetching all
 *              cart items for a user and integrates with utility methods for JSON conversion.
 *              Utilizes Spring Security for user authentication and manages interactions with the
 *              database using JdbcTemplate for smooth cart operations. It logs errors to assist
 *              in identifying issues during cart processing.
 * Created on: 14/10/2024
 * Last Modified: 28/10/2024
 */

package com.analyticalsolution.analyticalsolution.service;

import com.analyticalsolution.analyticalsolution.entity.CartItem;
import com.analyticalsolution.analyticalsolution.entity.User;
import com.analyticalsolution.analyticalsolution.repository.UserRepository;
import com.analyticalsolution.analyticalsolution.responses.CartDetailsResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class CartService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UtilityService utilityService;

    private String BASE_URL = "http://localhost:3000/";

    // Add item to cart
    @Transactional
    public void addItemToCart(CartItem cartItem){
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName().toString());
            if (existingUser == null) {
                log.error("User not found.");
            }

            String customerID = existingUser.getId();

            // Check if the cart exists for the user
            String sql = "SELECT cart_id FROM cart WHERE customer_id = ?";
            List<String> cartIds = jdbcTemplate.queryForList(sql, new Object[]{customerID}, String.class);

            if(!cartIds.isEmpty()){
                // If cart exists, get the cart ID
                String cartId = cartIds.get(0);

                // Fetch existing item_ids from the cart
                String selectItemIdsSql = "SELECT item_ids FROM cart WHERE cart_id = ?";
                String existingItemIdsJson = jdbcTemplate.queryForObject(selectItemIdsSql, new Object[]{cartId}, String.class);

                // Convert JSON string to a list of item IDs
                List<String> existingItemIds = utilityService.parseJsonToList(existingItemIdsJson);

                // Create a new cart item
                String insertCartItem = "INSERT INTO cartitems (item_id, product_id, quantity) " +  "VALUES (?, ?, ?)";
                cartItem.setItem_id(UUID.randomUUID().toString());
                cartItem.setQuantity(1L);
                jdbcTemplate.update(insertCartItem, cartItem.getItem_id(), cartItem.getProduct_id(), cartItem.getQuantity());

                // Add the new item to the list
                existingItemIds.add(cartItem.getItem_id());

                // Update the cart with the new item list
                String updateSql = "UPDATE cart SET item_ids = ? WHERE cart_id = ?";
                String updatedItemIdsJson = utilityService.convertListToJson(existingItemIds);
                jdbcTemplate.update(updateSql, updatedItemIdsJson, cartId);

                log.info("Item added to the existing cart.");
            }else{
                // Create a new cart item
                String insertCartItem = "INSERT INTO cartitems (item_id, product_id, quantity) " +  "VALUES (?, ?, ?)";
                cartItem.setItem_id(UUID.randomUUID().toString());
                cartItem.setQuantity(1L);
                jdbcTemplate.update(insertCartItem, cartItem.getItem_id(), cartItem.getProduct_id(), cartItem.getQuantity());

                // If the cart does not exist, create a new cart and add the item
                String createNewCart = "INSERT into cart (cart_id, customer_id, item_ids) " + "VALUES(?, ?, ?)";

                String newCartId = UUID.randomUUID().toString();
                List<String> itemIds = new ArrayList<>();
                itemIds.add(cartItem.getItem_id());
                String itemIdsJson = utilityService.convertListToJson(itemIds);

                // Insert the new cart into the database
                jdbcTemplate.update(createNewCart, newCartId, customerID, itemIdsJson);

                log.info("New cart created and item added.");
            }
        } catch (Exception e) {
            log.error("Error occurred while adding item to cart: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    // Update item quantity
    @Transactional
    public void updateItemQuantity(CartItem cartItem) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName());

            if (existingUser == null) {
                log.error("User not found.");
                return;
            }

            String customerID = existingUser.getId();

            // Get the cart ID for the user
            String sql = "SELECT cart_id FROM cart WHERE customer_id = ?";
            List<String> cartIds = jdbcTemplate.queryForList(sql, new Object[]{customerID}, String.class);

            if (!cartIds.isEmpty()) {
                String cartId = cartIds.get(0);

                // Retrieve the existing item_ids from the cart
                String selectItemIdsSql = "SELECT item_ids FROM cart WHERE cart_id = ?";
                String itemIdsJson = jdbcTemplate.queryForObject(selectItemIdsSql, new Object[]{cartId}, String.class);

                List<String> itemIds = utilityService.parseJsonToList(itemIdsJson);

                // Check whether the CartItem exists in this cart
                if (itemIds.contains(cartItem.getItem_id())) {
                    // If the item exists, update its quantity
                    if(cartItem.getQuantity() <= 0){
                        deleteItemFromCart(cartItem.getItem_id());
                    }
                    String updateQuantitySql = "UPDATE cartitems SET quantity = ? WHERE item_id = ? AND product_id = ?";
                    jdbcTemplate.update(updateQuantitySql, cartItem.getQuantity(), cartItem.getItem_id(), cartItem.getProduct_id());

                    log.info("Item quantity updated successfully for item_id: " + cartItem.getItem_id());
                } else {
                    log.error("Item not found in the cart for item_id: " + cartItem.getItem_id());
                }
            } else {
                log.error("No cart found for user: " + customerID);
            }
        } catch (Exception e) {
            log.error("Error occurred while updating item quantity: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    // Delete item from cart
    @Transactional
    public void deleteItemFromCart(String itemId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName());

            if (existingUser == null) {
                log.error("User not found.");
                return;
            }

            String customerID = existingUser.getId();

            // Get the cart ID for the user
            String sql = "SELECT cart_id FROM cart WHERE customer_id = ?";
            List<String> cartIds = jdbcTemplate.queryForList(sql, new Object[]{customerID}, String.class);

            if (!cartIds.isEmpty()) {
                String cartId = cartIds.get(0);

                // Retrieve the existing item_ids from the cart
                String selectItemIdsSql = "SELECT item_ids FROM cart WHERE cart_id = ?";
                String itemIdsJson = jdbcTemplate.queryForObject(selectItemIdsSql, new Object[]{cartId}, String.class);

                List<String> itemIds = utilityService.parseJsonToList(itemIdsJson);

                // Check whether the item exists in the cart
                if (itemIds.contains(itemId)) {
                    // Remove the item from the list
                    itemIds.remove(itemId);

                    // Update the cart with the new list of item IDs
                    String updatedItemIdsJson = utilityService.convertListToJson(itemIds);
                    String updateSql = "UPDATE cart SET item_ids = ? WHERE cart_id = ?";
                    jdbcTemplate.update(updateSql, updatedItemIdsJson, cartId);

                    // Optionally, remove the item from the cartitems table
                    String deleteCartItemSql = "DELETE FROM cartitems WHERE item_id = ?";
                    jdbcTemplate.update(deleteCartItemSql, itemId);

                    log.info("Item deleted successfully from cart for item_id: " + itemId);
                } else {
                    log.error("Item not found in the cart for item_id: " + itemId);
                }
            } else {
                log.error("No cart found for user: " + customerID);
            }
        } catch (Exception e) {
            log.error("Error occurred while deleting item from cart: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    // Fetch all cart items for the authenticated user
    @Transactional
    public List<CartDetailsResponse> getAllItems() {
        List<CartDetailsResponse> cartItems = new ArrayList<>();
        try {
            // Get the authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User existingUser = userRepository.findUserByUsername(authentication.getName());

            if (existingUser == null) {
                log.error("User not found.");
                return null;
            }

            String customerID = existingUser.getId();

            // Get the cart ID for the user
            String sql = "SELECT cart_id FROM cart WHERE customer_id = ?";
            List<String> cartIds = jdbcTemplate.queryForList(sql, new Object[]{customerID}, String.class);

            if (!cartIds.isEmpty()) {
                // If the cart exists, get the cart ID
                String cartId = cartIds.get(0);

                // Fetch existing item_ids from the cart
                String selectItemIdsSql = "SELECT item_ids FROM cart WHERE cart_id = ?";
                String itemIdsJson = jdbcTemplate.queryForObject(selectItemIdsSql, new Object[]{cartId}, String.class);

                // Convert JSON string to a list of item IDs
                List<String> itemIds = utilityService.parseJsonToList(itemIdsJson);

                // If item IDs exist, fetch all cart items
                if (!itemIds.isEmpty()) {
                    String selectItemsSql = "SELECT ci.item_id, ci.product_id, ci.quantity, p.product_name, p.product_images, p.product_price " +
                            "FROM cartitems ci " +
                            "JOIN products p ON ci.product_id = p.product_id " +
                            "WHERE ci.item_id IN (" + String.join(",", itemIds.stream().map(id -> "'" + id + "'").toArray(String[]::new)) + ")";

                    // Fetch the cart items and map them to the CartDetailsResponse
                    List<Map<String, Object>> rows = jdbcTemplate.queryForList(selectItemsSql);

                    for (Map<String, Object> row : rows) {
                        CartDetailsResponse cartItem = new CartDetailsResponse();
                        cartItem.setItem_id((String) row.get("item_id"));
                        cartItem.setProduct_id((String) row.get("product_id"));
                        cartItem.setProduct_name((String) row.get("product_name"));

                        // Extracting a single image or handling multiple images based on your structure
                        String productImagesJson = (String) row.get("product_images");
                        List<String> productImages = utilityService.parseJsonToList(productImagesJson);
                        cartItem.setProduct_image(productImages.isEmpty() ? "" : productImages.get(0));
                        cartItem.setProduct_image(BASE_URL + cartItem.getProduct_image());
                        cartItem.setPrice((Long) row.get("product_price"));
                        cartItem.setQuantity((Long) row.get("quantity"));

                        cartItems.add(cartItem);
                    }
                }
            } else {
                log.info("No cart found for user: " + customerID);
                return null;
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching items from cart: " + e.getMessage());
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return null;
        }

        return cartItems;
    }
}
