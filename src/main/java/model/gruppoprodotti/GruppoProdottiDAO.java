package model.gruppoprodotti;

import model.ConnectionPool;
import model.DAOInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GruppoProdottiDAO implements DAOInterface<GruppoProdottiBean, Long> {

    public GruppoProdottiDAO() {

    }

    @Override
    public GruppoProdottiBean doRetrieveByKey(Long id) throws SQLException {
        Connection connection = null; // Dichiarazione qui per il blocco finally
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        GruppoProdottiBean gruppo = null; // Inizializzazione a null

        String sql = "SELECT * FROM GruppoProdotti WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection(); // Ottieni una connessione dal pool
            statement = connection.prepareStatement(sql);

            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                gruppo = new GruppoProdottiBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome")
                );
            }
        } finally {
            // Rilascia le risorse JDBC nell'ordine inverso di acquisizione
            try {
                if (resultSet != null) resultSet.close();
            } finally {
                try {
                    if (statement != null) statement.close();
                } finally {
                    ConnectionPool.releaseConnection(connection); // Rilascia la connessione al pool
                }
            }
        }
        return gruppo;
    }

    @Override
    public List<GruppoProdottiBean> doRetrieveAll() throws SQLException {
        List<GruppoProdottiBean> gruppi = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM GruppoProdotti";

        try {
            connection = ConnectionPool.getConnection(); // Ottieni una connessione dal pool
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                gruppi.add(new GruppoProdottiBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome")
                ));
            }
        } finally {
            // Rilascia le risorse JDBC nell'ordine inverso di acquisizione
            try {
                if (resultSet != null) resultSet.close();
            } finally {
                try {
                    if (statement != null) statement.close();
                } finally {
                    ConnectionPool.releaseConnection(connection); // Rilascia la connessione al pool
                }
            }
        }
        return gruppi;
    }

    @Override
    public void doSave(GruppoProdottiBean gruppo) throws SQLException {
        Connection connection = null; // Dichiarazione qui per il blocco finally
        PreparedStatement statement = null;
        ResultSet generatedKeys = null; // Per le chiavi generate automaticamente

        String sql = "INSERT INTO GruppoProdotti (nome) VALUES (?)";

        try {
            connection = ConnectionPool.getConnection(); // Ottieni una connessione dal pool
            // Specifica RETURN_GENERATED_KEYS quando crei il PreparedStatement
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, gruppo.getNome());
            statement.executeUpdate();

            // Recupera le chiavi generate (come l'ID auto-incrementato)
            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                gruppo.setId(generatedKeys.getLong(1));
            }
        } finally {
            // Rilascia le risorse JDBC nell'ordine inverso di acquisizione
            try {
                if (generatedKeys != null) generatedKeys.close();
            } finally {
                try {
                    if (statement != null) statement.close();
                } finally {
                    ConnectionPool.releaseConnection(connection); // Rilascia la connessione al pool
                }
            }
        }
    }

    @Override
    public void doUpdate(GruppoProdottiBean gruppo) throws SQLException {
        Connection connection = null; // Dichiarazione qui per il blocco finally
        PreparedStatement statement = null;

        String sql = "UPDATE GruppoProdotti SET nome = ? WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection(); // Ottieni una connessione dal pool
            statement = connection.prepareStatement(sql);

            statement.setString(1, gruppo.getNome());
            statement.setLong(2, gruppo.getId());
            statement.executeUpdate();
        } finally {
            // Rilascia le risorse JDBC nell'ordine inverso di acquisizione
            try {
                if (statement != null) statement.close();
            } finally {
                ConnectionPool.releaseConnection(connection); // Rilascia la connessione al pool
            }
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        Connection connection = null; // Dichiarazione qui per il blocco finally
        PreparedStatement statement = null;

        String sql = "DELETE FROM GruppoProdotti WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection(); // Ottieni una connessione dal pool
            statement = connection.prepareStatement(sql);

            statement.setLong(1, id);
            statement.executeUpdate();
        } finally {
            // Rilascia le risorse JDBC nell'ordine inverso di acquisizione
            try {
                if (statement != null) statement.close();
            } finally {
                ConnectionPool.releaseConnection(connection); // Rilascia la connessione al pool
            }
        }
    }
}