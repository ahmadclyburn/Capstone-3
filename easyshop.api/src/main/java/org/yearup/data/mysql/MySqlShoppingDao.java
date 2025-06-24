package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.sql.SQLException;

@Component
public class MySqlShoppingDao extends MySqlDaoBase implements ShoppingCartDao
{
    public MySqlShoppingDao(DataSource dataSource)
    {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {

        return null;
    }

    @Override
    public void addProduct(int userId, int productId, int quantity) throws SQLException {

    }

    @Override
    public void removeProduct(int userId, int productId) {

    }

    @Override
    public void clearCart(int userId) {

    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity) {

    }

    @Override
    public void checkout(int userId) {

    }
}
