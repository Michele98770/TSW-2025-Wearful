package model.cartitem;

import model.ConnectionPool;
import model.DAOInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAO implements DAOInterface<CartItemBean, Long> {

    public CartItemDAO() {

    }

    @Override
    public CartItemBean doRetrieveByKey(Long id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        CartItemBean cartItem = null;

        String sql = "SELECT * FROM CartItem WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                cartItem = new CartItemBean(
                        resultSet.getLong("id"),
                        resultSet.getLong("idProdotto"),
                        resultSet.getLong("idCarrello"),
                        resultSet.getInt("quantita"),
                        resultSet.getBoolean("personalizzato"),
                        resultSet.getString("imgPath")
                );
            }
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } finally {
                try {
                    if (statement != null) statement.close();
                } finally {
                    ConnectionPool.releaseConnection(connection);
                }
            }
        }
        return cartItem;
    }

    @Override
    public List<CartItemBean> doRetrieveAll() throws SQLException {
        List<CartItemBean> cartItems = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM CartItem";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

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
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } finally {
                try {
                    if (statement != null) statement.close();
                } finally {
                    ConnectionPool.releaseConnection(connection);
                }
            }
        }
        return cartItems;
    }

    @Override
    public void doSave(CartItemBean cartItem) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        String sql = "INSERT INTO CartItem (idProdotto, idCarrello, quantita, personalizzato, imgPath) "
                + "VALUES (?, ?, ?, ?, ?)";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setLong(1, cartItem.getIdProdotto());
            statement.setLong(2, cartItem.getIdCarrello());
            statement.setInt(3, cartItem.getQuantita());
            statement.setBoolean(4, cartItem.isPersonalizzato());
            statement.setString(5, cartItem.getImgPath());

            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                cartItem.setId(generatedKeys.getLong(1));
            }
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
            } finally {
                try {
                    if (statement != null) statement.close();
                } finally {
                    ConnectionPool.releaseConnection(connection);
                }
            }
        }
    }

    @Override
    public void doUpdate(CartItemBean cartItem) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "UPDATE CartItem SET idProdotto = ?, idCarrello = ?, quantita = ?, "
                + "personalizzato = ?, imgPath = ? WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, cartItem.getIdProdotto());
            statement.setLong(2, cartItem.getIdCarrello());
            statement.setInt(3, cartItem.getQuantita());
            statement.setBoolean(4, cartItem.isPersonalizzato());
            statement.setString(5, cartItem.getImgPath());
            statement.setLong(6, cartItem.getId());

            statement.executeUpdate();
        } finally {
            try {
                if (statement != null) statement.close();
            } finally {
                ConnectionPool.releaseConnection(connection);
            }
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "DELETE FROM CartItem WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, id);
            statement.executeUpdate();
        } finally {
            try {
                if (statement != null) statement.close();
            } finally {
                ConnectionPool.releaseConnection(connection);
            }
        }
    }

    public List<CartItemBean> doRetrieveByCarrello(Long idCarrello) throws SQLException {
        List<CartItemBean> cartItems = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM CartItem WHERE idCarrello = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, idCarrello);
            resultSet = statement.executeQuery();

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
        } finally {
            try {
                if (resultSet != null) resultSet.close();
            } finally {
                try {
                    if (statement != null) statement.close();
                } finally {
                    ConnectionPool.releaseConnection(connection);
                }
            }
        }
        return cartItems;
    }
}