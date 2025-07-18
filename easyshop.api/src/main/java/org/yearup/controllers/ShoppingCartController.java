package org.yearup.controllers;

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
@RestController
@PreAuthorize("isAuthenticated")
@CrossOrigin
@RequestMapping("cart")
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
    @GetMapping
    public ShoppingCart getCart(Principal principal)
    {

        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getByUserId(userId);

    }
    }

    @PostMapping("products/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ShoppingCart addProduct(@PathVariable int productId, Principal principal) {
        try {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            return shoppingCartDao.addProduct(userId, productId);

        } catch (Exception e) {
            System.err.println("error adding product");
            throw new RuntimeException(e);
        }

}


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
    public ShoppingCart removeProduct(@PathVariable int productId, Principal principal){
        try {
            String username = principal.getName();
            User user = userDao.getByUserName(username);
            int userId = user.getId();
            return shoppingCartDao.removeProduct(userId, productId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("")
    @PreAuthorize("isAuthenticated()")
    public ShoppingCart clearCart( Principal principal)
    {
        try
        {
            String userName = principal.getName();
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            return shoppingCartDao.clearCart(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}

