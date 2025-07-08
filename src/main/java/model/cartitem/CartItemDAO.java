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

    @Override
    public CartItemBean doRetrieveByKey(Long id) throws SQLException {
        String sql = "SELECT * FROM CartItem WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        CartItemBean item = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                item = new CartItemBean(
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
        return item;
    }

    @Override
    public List<CartItemBean> doRetrieveAll() throws SQLException {
        List<CartItemBean> items = new ArrayList<>();
        String sql = "SELECT * FROM CartItem";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                items.add(new CartItemBean(
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
        return items;
    }

    @Override
    public void doSave(CartItemBean item) throws SQLException {
        String sql = "INSERT INTO CartItem (idProdotto, idCarrello, quantita, personalizzato, imgPath) VALUES (?, ?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, item.getProductID());
            statement.setLong(2, item.getIdCarrello());
            statement.setInt(3, item.getQuantita());
            statement.setBoolean(4, item.isPersonalizzato());
            statement.setString(5, item.getImgPath());

            statement.executeUpdate();
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                item.setId(generatedKeys.getLong(1));
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
    public void doUpdate(CartItemBean item) throws SQLException {
        String sql = "UPDATE CartItem SET quantita = ?, personalizzato = ?, imgPath = ? WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, item.getQuantita());
            statement.setBoolean(2, item.isPersonalizzato());
            statement.setString(3, item.getImgPath());
            statement.setLong(4, item.getId());
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
        String sql = "DELETE FROM CartItem WHERE id = ?";
        Connection connection = null;
        PreparedStatement statement = null;

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
        List<CartItemBean> items = new ArrayList<>();
        String sql = "SELECT * FROM CartItem WHERE idCarrello = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, idCarrello);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                items.add(new CartItemBean(
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
        return items;
    }

    public CartItemBean doRetrieveByProdottoAndCarrello(Long idProdotto, Long idCarrello) throws SQLException {
        String sql = "SELECT * FROM CartItem WHERE idProdotto = ? AND idCarrello = ?";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        CartItemBean item = null;

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, idProdotto);
            statement.setLong(2, idCarrello);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                item = new CartItemBean(
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
        return item;
    }
}