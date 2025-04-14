package model.cartitem;

import model.ConnectionPool;
import model.DAOInterface;
import model.cartitem.CartItemBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAO implements DAOInterface<CartItemBean, Long> {
    private ConnectionPool connectionPool;

    public CartItemDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public CartItemBean doRetrieveByKey(Long id) throws SQLException {
        String sql = "SELECT * FROM CartItem WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new CartItemBean(
                            resultSet.getLong("id"),
                            resultSet.getLong("idProdotto"),
                            resultSet.getLong("idCarrello"),
                            resultSet.getInt("quantita"),
                            resultSet.getBoolean("personalizzato"),
                            resultSet.getString("imgPath")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<CartItemBean> doRetrieveAll() throws SQLException {
        List<CartItemBean> cartItems = new ArrayList<>();
        String sql = "SELECT * FROM CartItem";

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                cartItems.add(new CartItemBean(
                        resultSet.getLong("id"),
                        resultSet.getLong("idProdotto"),
                        resultSet.getLong("idCarrello"),
                        resultSet.getInt("quantita"),
                        resultSet.getBoolean("personalizzato"),
                        resultSet.getString("imgPath")
                ));
            }
        }
        return cartItems;
    }

    @Override
    public void doSave(CartItemBean cartItem) throws SQLException {
        String sql = "INSERT INTO CartItem (idProdotto, idCarrello, quantita, personalizzato, imgPath) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, cartItem.getIdProdotto());
            statement.setLong(2, cartItem.getIdCarrello());
            statement.setInt(3, cartItem.getQuantita());
            statement.setBoolean(4, cartItem.isPersonalizzato());
            statement.setString(5, cartItem.getImgPath());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cartItem.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    @Override
    public void doUpdate(CartItemBean cartItem) throws SQLException {
        String sql = "UPDATE CartItem SET idProdotto = ?, idCarrello = ?, quantita = ?, "
                + "personalizzato = ?, imgPath = ? WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, cartItem.getIdProdotto());
            statement.setLong(2, cartItem.getIdCarrello());
            statement.setInt(3, cartItem.getQuantita());
            statement.setBoolean(4, cartItem.isPersonalizzato());
            statement.setString(5, cartItem.getImgPath());
            statement.setLong(6, cartItem.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        String sql = "DELETE FROM CartItem WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Metodi aggiuntivi specifici per CartItem
    public List<CartItemBean> doRetrieveByCarrello(Long idCarrello) throws SQLException {
        List<CartItemBean> cartItems = new ArrayList<>();
        String sql = "SELECT * FROM CartItem WHERE idCarrello = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idCarrello);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    cartItems.add(new CartItemBean(
                            resultSet.getLong("id"),
                            resultSet.getLong("idProdotto"),
                            resultSet.getLong("idCarrello"),
                            resultSet.getInt("quantita"),
                            resultSet.getBoolean("personalizzato"),
                            resultSet.getString("imgPath")
                    ));
                }
            }
        }
        return cartItems;
    }
}