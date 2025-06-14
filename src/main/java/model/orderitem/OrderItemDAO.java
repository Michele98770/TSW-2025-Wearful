package model.orderitem;

import model.ConnectionPool;
import model.DAOInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO implements DAOInterface<OrderItemBean, Long> {

    public OrderItemDAO() {

    }

    @Override
    public OrderItemBean doRetrieveByKey(Long id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OrderItemBean orderItem = null;

        String sql = "SELECT * FROM OrderItem WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                orderItem = new OrderItemBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getLong("idProdotto"),
                        resultSet.getLong("idOrdine"),
                        resultSet.getFloat("prezzo"),
                        resultSet.getInt("quantita"),
                        resultSet.getInt("IVA")
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
        return orderItem;
    }

    @Override
    public List<OrderItemBean> doRetrieveAll() throws SQLException {
        List<OrderItemBean> orderItems = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM OrderItem";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                orderItems.add(new OrderItemBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getLong("idProdotto"),
                        resultSet.getLong("idOrdine"),
                        resultSet.getFloat("prezzo"),
                        resultSet.getInt("quantita"),
                        resultSet.getInt("IVA")
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
        return orderItems;
    }

    @Override
    public void doSave(OrderItemBean orderItem) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        String sql = "INSERT INTO OrderItem (nome, idProdotto, idOrdine, prezzo, quantita, IVA) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, orderItem.getNome());
            statement.setLong(2, orderItem.getIdProdotto());
            statement.setLong(3, orderItem.getIdOrdine());
            statement.setFloat(4, orderItem.getPrezzo());
            statement.setInt(5, orderItem.getQuantita());
            statement.setInt(6, orderItem.getIva());

            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                orderItem.setId(generatedKeys.getLong(1));
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
    public void doUpdate(OrderItemBean orderItem) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "UPDATE OrderItem SET nome = ?, idProdotto = ?, idOrdine = ?, "
                + "prezzo = ?, quantita = ?, IVA = ? WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, orderItem.getNome());
            statement.setLong(2, orderItem.getIdProdotto());
            statement.setLong(3, orderItem.getIdOrdine());
            statement.setFloat(4, orderItem.getPrezzo());
            statement.setInt(5, orderItem.getQuantita());
            statement.setInt(6, orderItem.getIva());
            statement.setLong(7, orderItem.getId());

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

        String sql = "DELETE FROM OrderItem WHERE id = ?";

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

    public List<OrderItemBean> doRetrieveByOrdine(Long idOrdine) throws SQLException {
        List<OrderItemBean> orderItems = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM OrderItem WHERE idOrdine = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, idOrdine);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                orderItems.add(new OrderItemBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getLong("idProdotto"),
                        resultSet.getLong("idOrdine"),
                        resultSet.getFloat("prezzo"),
                        resultSet.getInt("quantita"),
                        resultSet.getInt("IVA")
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
        return orderItems;
    }
}