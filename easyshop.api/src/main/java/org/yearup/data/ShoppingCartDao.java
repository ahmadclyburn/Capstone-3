package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;


public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    ShoppingCart addProduct(int userId, int productId);
    ShoppingCart removeProduct(int userId, int productId);
    ShoppingCart clearCart(int userId);
    void updateQuantity(int userId, int productId, int quantity);
    void checkout(int userId);}

