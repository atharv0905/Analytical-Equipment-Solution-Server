/**
 * File: CartController.java
 * Author: Atharv Mirgal
 * Description: This controller manages cart-related operations for the application, such as adding, updating,
 *              deleting items, and retrieving cart items. It interacts with the CartService to perform business
 *              logic and returns appropriate responses for each API request. Error handling is included to manage
 *              exceptions and log errors.
 * Created on: 11/10/2024
 * Last Modified: 13/10/2024
 */

package com.analyticalsolution.analyticalsolution.controller;

import com.analyticalsolution.analyticalsolution.entity.CartItem;
import com.analyticalsolution.analyticalsolution.responses.CartDetailsResponse;
import com.analyticalsolution.analyticalsolution.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // Add item to cart
    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestBody CartItem cartItem){
        try{
            cartService.addItemToCart(cartItem);
            return new ResponseEntity<>("Item added to cart", HttpStatus.OK);
        }catch (Exception e){
            log.error("Error occurred while adding item to cart: " + e.getMessage());
            return new ResponseEntity<>("Error occurred while adding item to cart", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Update item quantity
    @PutMapping("/update-quantity")
    public ResponseEntity<?> updateItemQuantity(@RequestBody CartItem cartItem){
        try{
            cartService.updateItemQuantity(cartItem);
            return new ResponseEntity<>("Item quantity updated", HttpStatus.OK);
        }catch (Exception e){
            log.error("Error occurred while updating item quantity: " + e.getMessage());
            return new ResponseEntity<>("Error occurred while updating item quantity", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Delete item
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteItem(@RequestBody CartItem cartItem){
        try{
            cartService.deleteItemFromCart(cartItem.getItem_id());
            return new ResponseEntity<>("Item deleted", HttpStatus.OK);
        }catch (Exception e){
            log.error("Error occurred while deleting item: " + e.getMessage());
            return new ResponseEntity<>("Error occurred while deleting item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Fetch cart items
    @GetMapping("/items")
    public ResponseEntity<?> fetchAllCartItems(){
        try{
            List<CartDetailsResponse> allItems = cartService.getAllItems();
            if(allItems == null){
                allItems = new ArrayList<>();
            }
            return new ResponseEntity<>(allItems, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error occurred while deleting item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
