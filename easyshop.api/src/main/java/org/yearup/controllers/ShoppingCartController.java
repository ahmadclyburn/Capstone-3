package org.yearup.controllers;

import org.apache.ibatis.type.SimpleTypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;
import java.sql.SQLException;

// convert this class to a REST controller
// only logged in users should have access to these actions
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;


    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    // each method in this controller requires a Principal object as a parameter
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... you broke it.");
        }
    }

    // add a POST method to add a product to the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be added
    @PostMapping("products/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ShoppingCart addProduct(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal) throws SQLException {
        int userId = 0;
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            userId = user.getId();

            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
            boolean productExists = cart.getItems().values().stream()
                    .anyMatch(cartItem -> cartItem.getProductId() == productId);
            if(productExists){
                ShoppingCartItem existingItem = cart.getItems().get(productId);
                int newQuantity = existingItem.getQuantity() + item.getQuantity();
                shoppingCartDao.updateQuantity(userId, productId, newQuantity);
            }
            else{
                shoppingCartDao.addProduct(userId, productId, item.getQuantity());
            }  return shoppingCartDao.getByUserId(userId);
        } catch (SQLException e) {
            System.err.println("error adding product");
            throw new RuntimeException(e);
        }
}

    // add a PUT method to update an existing product in the cart - the url should be
    // https://localhost:8080/cart/products/15 (15 is the productId to be updated)
    // the BODY should be a ShoppingCartItem - quantity is the only value that will be updated

@PutMapping("products/{productId}")
public void updateProduct(@PathVariable int productId, @RequestBody  ShoppingCartItem item, Principal principal){
    try {
        String username = principal.getName();
        User user = userDao.getByUserName(username);
        int userId = user.getId();
        shoppingCartDao.updateQuantity(userId, productId, item.getQuantity());
    } catch (Exception e) {
        throw new RuntimeException(e);
    }

}

    // add a DELETE method to clear all products from the current users cart
    // https://localhost:8080/cart
    @DeleteMapping("product/{productId}")
    public void deleteProduct(@PathVariable int productId, Principal principal){
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            int userId = user.getId();
            shoppingCartDao.removeProduct(userId, productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
