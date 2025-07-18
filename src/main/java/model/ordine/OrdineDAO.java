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
import java.sql.Date;

public class OrdineDAO implements DAOInterface<OrdineBean, Long> {

    public OrdineDAO() {

    }

    @Override
    public OrdineBean doRetrieveByKey(Long id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        OrdineBean ordine = null;
        String sql = "SELECT id, idUtente, infoConsegna, dataOrdine, stato FROM Ordine WHERE id = ?";

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
                        resultSet.getTimestamp("dataOrdine"),
                        resultSet.getString("stato")
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
        return doRetrieveAll("dataOrdine", "desc", null, null);
    }

    public List<OrdineBean> doRetrieveAll(String sortBy, String sortOrder) throws SQLException {
        return doRetrieveAll(sortBy, sortOrder, null, null);
    }

    public List<OrdineBean> doRetrieveAll(String sortBy, String sortOrder, java.util.Date startDate, java.util.Date endDate) throws SQLException {
        List<OrdineBean> ordini = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        StringBuilder sql = new StringBuilder("SELECT id, idUtente, infoConsegna, dataOrdine, stato FROM Ordine WHERE 1=1");

        int paramIndex = 1;

        if (startDate != null) {
            sql.append(" AND dataOrdine >= ?");
        }
        if (endDate != null) {
            sql.append(" AND dataOrdine <= ?");
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            sql.append(" ORDER BY ");
            switch (sortBy) {
                case "id":
                    sql.append("id");
                    break;
                case "idUtente":
                    sql.append("idUtente");
                    break;
                case "dataOrdine":
                    sql.append("dataOrdine");
                    break;
                case "stato":
                    sql.append("stato");
                    break;
                default:
                    sql.append("id");
                    break;
            }
            if (sortOrder != null && ("desc".equalsIgnoreCase(sortOrder) || "asc".equalsIgnoreCase(sortOrder))) {
                sql.append(" ").append(sortOrder.toUpperCase());
            } else {
                sql.append(" ASC");
            }
        }

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql.toString());

            if (startDate != null) {
                statement.setDate(paramIndex++, new java.sql.Date(startDate.getTime()));
            }
            if (endDate != null) {
                statement.setDate(paramIndex++, new java.sql.Date(endDate.getTime()));
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ordini.add(new OrdineBean(
                        resultSet.getLong("id"),
                        resultSet.getString("idUtente"),
                        resultSet.getLong("infoConsegna"),
                        resultSet.getTimestamp("dataOrdine"),
                        resultSet.getString("stato")
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

    public List<OrdineBean> doRetrieveAll(java.util.Date startDate, java.util.Date endDate) throws SQLException {
        return doRetrieveAll("dataOrdine", "desc", startDate, endDate);
    }

    @Override
    public void doSave(OrdineBean ordine) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        String sql = "INSERT INTO Ordine (idUtente, infoConsegna, dataOrdine, stato) VALUES (?, ?, ?, ?)";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, ordine.getIdUtente());
            statement.setLong(2, ordine.getInfoConsegna());
            statement.setTimestamp(3, new Timestamp(ordine.getDataOrdine().getTime()));
            if (ordine.getStato() == null || ordine.getStato().isEmpty()) {
                statement.setString(4, "Spedito");
                ordine.setStato("Spedito");
            } else {
                statement.setString(4, ordine.getStato());
            }
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
        String sql = "UPDATE Ordine SET idUtente = ?, infoConsegna = ?, dataOrdine = ?, stato = ? WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, ordine.getIdUtente());
            statement.setLong(2, ordine.getInfoConsegna());
            statement.setTimestamp(3, new Timestamp(ordine.getDataOrdine().getTime()));
            statement.setString(4, ordine.getStato());
            statement.setLong(5, ordine.getId());
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
        String sql = "SELECT id, idUtente, infoConsegna, dataOrdine, stato FROM Ordine WHERE idUtente = ? ORDER BY dataOrdine DESC";

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
                        resultSet.getTimestamp("dataOrdine"),
                        resultSet.getString("stato")
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

    public void doDelete(Long id, Connection connection) throws SQLException {
        PreparedStatement statement = null;
        String sql = "DELETE FROM Ordine WHERE id = ?";

        try {
            statement = connection.prepareStatement(sql);
            statement.setLong(1, id);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}