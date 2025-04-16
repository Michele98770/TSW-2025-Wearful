package model.orderitem;


import model.ConnectionPool;
import model.DAOInterface;
import model.orderitem.OrderItemBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO implements DAOInterface<OrderItemBean, Long> {
    private Connection connectionPool;

    public OrderItemDAO() {
        this.connectionPool = ConnectionPool.getConnection();
    }
    @Override
    public OrderItemBean doRetrieveByKey(Long id) throws SQLException {
        String sql = "SELECT * FROM OrderItem WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new OrderItemBean(
                            resultSet.getLong("id"),
                            resultSet.getString("nome"),
                            resultSet.getLong("idProdotto"),
                            resultSet.getLong("idOrdine"),
                            resultSet.getFloat("prezzo"),
                            resultSet.getInt("quantita"),
                            resultSet.getInt("IVA")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<OrderItemBean> doRetrieveAll() throws SQLException {
        List<OrderItemBean> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM OrderItem";

        try (Connection connection = connectionPool;
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

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
        }
        return orderItems;
    }

    @Override
    public void doSave(OrderItemBean orderItem) throws SQLException {
        String sql = "INSERT INTO OrderItem (nome, idProdotto, idOrdine, prezzo, quantita, IVA) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, orderItem.getNome());
            statement.setLong(2, orderItem.getIdProdotto());
            statement.setLong(3, orderItem.getIdOrdine());
            statement.setFloat(4, orderItem.getPrezzo());
            statement.setInt(5, orderItem.getQuantita());
            statement.setInt(6, orderItem.getIva());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderItem.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    @Override
    public void doUpdate(OrderItemBean orderItem) throws SQLException {
        String sql = "UPDATE OrderItem SET nome = ?, idProdotto = ?, idOrdine = ?, "
                + "prezzo = ?, quantita = ?, IVA = ? WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, orderItem.getNome());
            statement.setLong(2, orderItem.getIdProdotto());
            statement.setLong(3, orderItem.getIdOrdine());
            statement.setFloat(4, orderItem.getPrezzo());
            statement.setInt(5, orderItem.getQuantita());
            statement.setInt(6, orderItem.getIva());
            statement.setLong(7, orderItem.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        String sql = "DELETE FROM OrderItem WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Metodi aggiuntivi specifici per OrderItem
    public List<OrderItemBean> doRetrieveByOrdine(Long idOrdine) throws SQLException {
        List<OrderItemBean> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM OrderItem WHERE idOrdine = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, idOrdine);
            try (ResultSet resultSet = statement.executeQuery()) {
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
            }
        }
        return orderItems;
    }
}