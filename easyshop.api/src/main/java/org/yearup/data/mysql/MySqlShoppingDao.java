package org.yearup.data.mysql;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

@Component
public class MySqlShoppingDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        ShoppingCart cart = new ShoppingCart();
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE user_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {
                ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                int productId = resultSet.getInt("product_id");
                int quantity = resultSet.getInt("quantity");

                Product product = getProductById(productId);
                shoppingCartItem.setProduct(product);
                shoppingCartItem.setQuantity(quantity);
                cart.add(shoppingCartItem);

            }
            return cart;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Product getProductById(int productId) {
        Product product =null;
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try(Connection connection = getConnection()){
             PreparedStatement statement = connection.prepareStatement(sql);
                statement.setInt(1, productId);
                 try(ResultSet resultSet = statement.executeQuery()){
                     if(resultSet.next()){
                         product = mapRow(resultSet);
                     }
                 }
            }
            catch(SQLException e){
            throw new RuntimeException(e);
            }
        return product;
    }

    @Override
    public ShoppingCart addProduct(int userId, int productId){
        String sql = "INSERT into shopping_cart (user_id, product_id, quantity) VALUES(?,?,?)";
        try(Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);
            statement.executeQuery();
            return getByUserId(userId);
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(int userId, int productId) {
    String sql = "DELETE FROM shopping_cart WHERE product_id = ? and user_id =?";
    try(Connection connection = getConnection()) {
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, productId);
        statement.setInt(2, userId);
        statement.executeQuery();
    }catch (SQLException e ){
        throw new RuntimeException(e);
    }
    }

    @Override
    public void clearCart(int userId) {
        String sql = "DELETE FROM shopping_cart WHERE user_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
    }catch (SQLException e ){
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updateQuantity(int userId, int productId, int quantity) {

    }

    @Override
    public void checkout(int userId) {
        String sql = """
                    SELECT address, city, state, zip FROM users WHERE user_id = ?;
                    INSERT INTO orders (user_id, date, address, city, state, zip, shipping_amount) 
                    VALUES (?, ?, ?, ?, ?, ?, ?);
                    DELETE FROM shopping_cart WHERE user_id = ?;
                """;
        try (Connection connection = getConnection()){
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setInt(1, userId);
            ResultSet set = statement.executeQuery();
            if (set.next()){
                statement.setInt(2, userId);
                statement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                statement.setString(4, set.getString("address"));
                statement.setString(5, set.getString("city"));
                statement.setString(6, set.getString("state"));
                statement.setString(7, set.getString("zip"));
                statement.setBigDecimal(8, new BigDecimal(5.00));
                statement.executeQuery();
            }
            statement.setInt(9, userId);
            statement.executeQuery();
        } catch (SQLException e ){
            throw new RuntimeException();}
}

    private Product mapRow(ResultSet row) throws SQLException {
        int productId = row.getInt("product_id");
        String name = row.getString("name");
        BigDecimal price = row.getBigDecimal("price");
        int categoryId = row.getInt("category_id");
        String description = row.getString("description");
        String color = row.getString("color");
        int stock = row.getInt("stock");
        boolean isFeatured = row.getBoolean("featured");
        String imageUrl = row.getString("image_url");

        return new Product(productId, name, price, categoryId, description, color, stock, isFeatured, imageUrl);
    }
}

