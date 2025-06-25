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

        // AGGIORNATO: includi codiceColore nella SELECT
        String sql = "SELECT id, nome, descrizione, taglia, colore, codiceColore, categoria, prezzo, IVA, disponibilita, personalizzabile, imgPath, publisher, gruppo FROM Prodotto WHERE id = ?";

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
                        resultSet.getString("codiceColore"), // AGGIUNTO: recupera il codice colore
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
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<ProdottoBean> prodotti = new ArrayList<>();

        // AGGIORNATO: includi codiceColore nella SELECT
        String sql = "SELECT id, nome, descrizione, taglia, colore, codiceColore, categoria, prezzo, IVA, disponibilita, personalizzabile, imgPath, publisher, gruppo FROM Prodotto";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                prodotti.add(new ProdottoBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descrizione"),
                        resultSet.getString("taglia"),
                        resultSet.getString("colore"),
                        resultSet.getString("codiceColore"), // AGGIUNTO: recupera il codice colore
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

        // AGGIORNATO: Includi codiceColore nella query INSERT
        String sql = "INSERT INTO Prodotto (nome, descrizione, taglia, colore, codiceColore, categoria, prezzo, IVA, disponibilita, personalizzabile, imgPath, publisher, gruppo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setString(3, prodotto.getTaglia());
            statement.setString(4, prodotto.getColore());
            statement.setString(5, prodotto.getCodiceColore()); // AGGIUNTO: imposta il codice colore
            statement.setString(6, prodotto.getCategoria());
            statement.setFloat(7, prodotto.getPrezzo());
            statement.setInt(8, prodotto.getIva());
            statement.setInt(9, prodotto.getDisponibilita());
            statement.setBoolean(10, prodotto.isPersonalizzabile());
            statement.setString(11, prodotto.getImgPath());
            statement.setString(12, prodotto.getPublisher());
            statement.setLong(13, prodotto.getGruppo());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La creazione del prodotto è fallita, nessuna riga modificata.");
            }

            generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                prodotto.setId(generatedKeys.getLong(1));
            } else {
                throw new SQLException("La creazione del prodotto è fallita, nessun ID ottenuto.");
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

        // AGGIORNATO: Includi codiceColore nella query UPDATE
        String sql = "UPDATE Prodotto SET nome=?, descrizione=?, taglia=?, colore=?, codiceColore=?, categoria=?, prezzo=?, IVA=?, disponibilita=?, personalizzabile=?, imgPath=?, publisher=?, gruppo=? WHERE id=?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setString(3, prodotto.getTaglia());
            statement.setString(4, prodotto.getColore());
            statement.setString(5, prodotto.getCodiceColore()); // AGGIUNTO: aggiorna il codice colore
            statement.setString(6, prodotto.getCategoria());
            statement.setFloat(7, prodotto.getPrezzo());
            statement.setInt(8, prodotto.getIva());
            statement.setInt(9, prodotto.getDisponibilita());
            statement.setBoolean(10, prodotto.isPersonalizzabile());
            statement.setString(11, prodotto.getImgPath());
            statement.setString(12, prodotto.getPublisher());
            statement.setLong(13, prodotto.getGruppo());
            statement.setLong(14, prodotto.getId());

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
        int rowsAffected = 0;

        String sql = "DELETE FROM Prodotto WHERE id = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, id);

            rowsAffected = statement.executeUpdate();
        } finally {
            try {
                if (statement != null) statement.close();
            } finally {
                ConnectionPool.releaseConnection(connection);
            }
        }
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