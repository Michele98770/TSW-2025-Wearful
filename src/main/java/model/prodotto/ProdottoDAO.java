package model.prodotto;

import model.ConnectionPool;
import model.DAOInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAO implements DAOInterface<ProdottoBean, Long> {

    public ProdottoDAO() {

    }

    @Override
    public ProdottoBean doRetrieveByKey(Long id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ProdottoBean prodotto = null;

        String sql = "SELECT * FROM Prodotto WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, id);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                prodotto = new ProdottoBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descrizione"),
                        resultSet.getString("taglia"),
                        resultSet.getString("colore"),
                        resultSet.getString("categoria"),
                        resultSet.getFloat("prezzo"),
                        resultSet.getInt("IVA"),
                        resultSet.getInt("disponibilita"),
                        resultSet.getBoolean("personalizzabile"),
                        resultSet.getString("imgPath"),
                        resultSet.getString("publisher"),
                        resultSet.getLong("gruppo")
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
        return prodotto;
    }

    @Override
    public List<ProdottoBean> doRetrieveAll() throws SQLException {
        List<ProdottoBean> prodotti = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM Prodotto";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                prodotti.add(new ProdottoBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descrizione"),
                        resultSet.getString("taglia"),
                        resultSet.getString("colore"),
                        resultSet.getString("categoria"),
                        resultSet.getFloat("prezzo"),
                        resultSet.getInt("IVA"),
                        resultSet.getInt("disponibilita"),
                        resultSet.getBoolean("personalizzabile"),
                        resultSet.getString("imgPath"),
                        resultSet.getString("publisher"),
                        resultSet.getLong("gruppo")
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
        return prodotti;
    }

    @Override
    public void doSave(ProdottoBean prodotto) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        String sql = "INSERT INTO Prodotto (nome, descrizione, taglia, colore, categoria, "
                + "prezzo, IVA, disponibilita, personalizzabile, imgPath, publisher, gruppo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setString(3, prodotto.getTaglia());
            statement.setString(4, prodotto.getColore());
            statement.setString(5, prodotto.getCategoria());
            statement.setFloat(6, prodotto.getPrezzo());
            statement.setInt(7, prodotto.getIva());
            statement.setInt(8, prodotto.getDisponibilita());
            statement.setBoolean(9, prodotto.isPersonalizzabile());
            statement.setString(10, prodotto.getImgPath());
            statement.setString(11, prodotto.getPublisher());
            statement.setLong(12, prodotto.getGruppo());

            statement.executeUpdate();

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                prodotto.setId(generatedKeys.getLong(1));
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
    public void doUpdate(ProdottoBean prodotto) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "UPDATE Prodotto SET nome = ?, descrizione = ?, taglia = ?, colore = ?, "
                + "categoria = ?, prezzo = ?, IVA = ?, disponibilita = ?, personalizzabile = ?, "
                + "imgPath = ?, publisher = ?, gruppo = ? WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setString(3, prodotto.getTaglia());
            statement.setString(4, prodotto.getColore());
            statement.setString(5, prodotto.getCategoria());
            statement.setFloat(6, prodotto.getPrezzo());
            statement.setInt(7, prodotto.getIva());
            statement.setInt(8, prodotto.getDisponibilita());
            statement.setBoolean(9, prodotto.isPersonalizzabile());
            statement.setString(10, prodotto.getImgPath());
            statement.setString(11, prodotto.getPublisher());
            statement.setLong(12, prodotto.getGruppo());
            statement.setLong(13, prodotto.getId());

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

        String sql = "DELETE FROM Prodotto WHERE id = ?";

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

    public List<ProdottoBean> doRetrieveByGruppo(Long gruppoId) throws SQLException {
        List<ProdottoBean> prodotti = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM Prodotto WHERE gruppo = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setLong(1, gruppoId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                prodotti.add(new ProdottoBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descrizione"),
                        resultSet.getString("taglia"),
                        resultSet.getString("colore"),
                        resultSet.getString("categoria"),
                        resultSet.getFloat("prezzo"),
                        resultSet.getInt("IVA"),
                        resultSet.getInt("disponibilita"),
                        resultSet.getBoolean("personalizzabile"),
                        resultSet.getString("imgPath"),
                        resultSet.getString("publisher"),
                        resultSet.getLong("gruppo")
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
        return prodotti;
    }

    public List<ProdottoBean> doRetrieveDisponibili() throws SQLException {
        List<ProdottoBean> prodotti = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT * FROM Prodotto WHERE disponibilita > 0";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                prodotti.add(new ProdottoBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descrizione"),
                        resultSet.getString("taglia"),
                        resultSet.getString("colore"),
                        resultSet.getString("categoria"),
                        resultSet.getFloat("prezzo"),
                        resultSet.getInt("IVA"),
                        resultSet.getInt("disponibilita"),
                        resultSet.getBoolean("personalizzabile"),
                        resultSet.getString("imgPath"),
                        resultSet.getString("publisher"),
                        resultSet.getLong("gruppo")
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
        return prodotti;
    }

    public boolean updateDisponibilita(Long idProdotto, int quantita) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "UPDATE Prodotto SET disponibilita = disponibilita + ? WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setInt(1, quantita);
            statement.setLong(2, idProdotto);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } finally {
            try {
                if (statement != null) statement.close();
            } finally {
                ConnectionPool.releaseConnection(connection);
            }
        }
    }
}