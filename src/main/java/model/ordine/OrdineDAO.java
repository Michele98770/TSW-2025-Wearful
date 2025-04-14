package model.ordine;

import model.ConnectionPool;
import model.DAOInterface;
import model.ordine.OrdineBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAO implements DAOInterface<OrdineBean, Long> {
    private ConnectionPool connectionPool;

    public OrdineDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public OrdineBean doRetrieveByKey(Long id) throws SQLException {
        String sql = "SELECT * FROM Ordine WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new OrdineBean(
                            resultSet.getLong("id"),
                            resultSet.getString("idUtente"),
                            resultSet.getLong("infoConsegna"),
                            resultSet.getTimestamp("dataOrdine")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<OrdineBean> doRetrieveAll() throws SQLException {
        List<OrdineBean> ordini = new ArrayList<>();
        String sql = "SELECT * FROM Ordine";

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                ordini.add(new OrdineBean(
                        resultSet.getLong("id"),
                        resultSet.getString("idUtente"),
                        resultSet.getLong("infoConsegna"),
                        resultSet.getTimestamp("dataOrdine")
                ));
            }
        }
        return ordini;
    }

    @Override
    public void doSave(OrdineBean ordine) throws SQLException {
        String sql = "INSERT INTO Ordine (idUtente, infoConsegna, dataOrdine) VALUES (?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, ordine.getIdUtente());
            statement.setLong(2, ordine.getInfoConsegna());
            statement.setTimestamp(3, new Timestamp(ordine.getDataOrdine().getTime()));

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ordine.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    @Override
    public void doUpdate(OrdineBean ordine) throws SQLException {
        String sql = "UPDATE Ordine SET idUtente = ?, infoConsegna = ?, dataOrdine = ? WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, ordine.getIdUtente());
            statement.setLong(2, ordine.getInfoConsegna());
            statement.setTimestamp(3, new Timestamp(ordine.getDataOrdine().getTime()));
            statement.setLong(4, ordine.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        String sql = "DELETE FROM Ordine WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Metodi aggiuntivi specifici per Ordine
    public List<OrdineBean> doRetrieveByUtente(String idUtente) throws SQLException {
        List<OrdineBean> ordini = new ArrayList<>();
        String sql = "SELECT * FROM Ordine WHERE idUtente = ? ORDER BY dataOrdine DESC";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, idUtente);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ordini.add(new OrdineBean(
                            resultSet.getLong("id"),
                            resultSet.getString("idUtente"),
                            resultSet.getLong("infoConsegna"),
                            resultSet.getTimestamp("dataOrdine")
                    ));
                }
            }
        }
        return ordini;
    }
}