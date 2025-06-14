package model.ordine;

import model.ConnectionPool;
import model.DAOInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrdineDAO implements DAOInterface<OrdineBean, Long> {

    public OrdineDAO() {

    }

    @Override
    public OrdineBean doRetrieveByKey(Long id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OrdineBean ordine = null;

        String sql = "SELECT * FROM Ordine WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                ordine = new OrdineBean(
                        resultSet.getLong("id"),
                        resultSet.getString("idUtente"),
                        resultSet.getLong("infoConsegna"),
                        resultSet.getTimestamp("dataOrdine")
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
        return ordine;
    }

    @Override
    public List<OrdineBean> doRetrieveAll() throws SQLException {
        List<OrdineBean> ordini = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM Ordine";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                ordini.add(new OrdineBean(
                        resultSet.getLong("id"),
                        resultSet.getString("idUtente"),
                        resultSet.getLong("infoConsegna"),
                        resultSet.getTimestamp("dataOrdine")
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
        return ordini;
    }

    @Override
    public void doSave(OrdineBean ordine) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        String sql = "INSERT INTO Ordine (idUtente, infoConsegna, dataOrdine) VALUES (?, ?, ?)";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, ordine.getIdUtente());
            statement.setLong(2, ordine.getInfoConsegna());
            statement.setTimestamp(3, new Timestamp(ordine.getDataOrdine().getTime()));

            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                ordine.setId(generatedKeys.getLong(1));
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
    public void doUpdate(OrdineBean ordine) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "UPDATE Ordine SET idUtente = ?, infoConsegna = ?, dataOrdine = ? WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, ordine.getIdUtente());
            statement.setLong(2, ordine.getInfoConsegna());
            statement.setTimestamp(3, new Timestamp(ordine.getDataOrdine().getTime()));
            statement.setLong(4, ordine.getId());

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

        String sql = "DELETE FROM Ordine WHERE id = ?";

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

    public List<OrdineBean> doRetrieveByUtente(String idUtente) throws SQLException {
        List<OrdineBean> ordini = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM Ordine WHERE idUtente = ? ORDER BY dataOrdine DESC";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, idUtente);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ordini.add(new OrdineBean(
                        resultSet.getLong("id"),
                        resultSet.getString("idUtente"),
                        resultSet.getLong("infoConsegna"),
                        resultSet.getTimestamp("dataOrdine")
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
        return ordini;
    }
}