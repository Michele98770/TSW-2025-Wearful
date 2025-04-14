package model.prodotto;

import model.ConnectionPool;
import model.DAOInterface;
import model.prodotto.ProdottoBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdottoDAO implements DAOInterface<ProdottoBean, Long> {
    private ConnectionPool connectionPool;

    public ProdottoDAO(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public ProdottoBean doRetrieveByKey(Long id) throws SQLException {
        String sql = "SELECT * FROM Prodotto WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new ProdottoBean(
                            resultSet.getLong("id"),
                            resultSet.getString("nome"),
                            resultSet.getString("descrizione"),
                            resultSet.getString("taglia"),
                            resultSet.getString("colore"),
                            resultSet.getString("categoria"),
                            resultSet.getFloat("prezzo"),
                            resultSet.getInt("IVA"),
                            resultSet.getBoolean("personalizzabile"),
                            resultSet.getString("imgPath"),
                            resultSet.getString("publisher"),
                            resultSet.getLong("gruppo")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<ProdottoBean> doRetrieveAll() throws SQLException {
        List<ProdottoBean> prodotti = new ArrayList<>();
        String sql = "SELECT * FROM Prodotto";

        try (Connection connection = connectionPool.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

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
                        resultSet.getBoolean("personalizzabile"),
                        resultSet.getString("imgPath"),
                        resultSet.getString("publisher"),
                        resultSet.getLong("gruppo")
                ));
            }
        }
        return prodotti;
    }

    @Override
    public void doSave(ProdottoBean prodotto) throws SQLException {
        String sql = "INSERT INTO Prodotto (nome, descrizione, taglia, colore, categoria, "
                + "prezzo, IVA, personalizzabile, imgPath, publisher, gruppo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setString(3, prodotto.getTaglia());
            statement.setString(4, prodotto.getColore());
            statement.setString(5, prodotto.getCategoria());
            statement.setFloat(6, prodotto.getPrezzo());
            statement.setInt(7, prodotto.getIva());
            statement.setBoolean(8, prodotto.isPersonalizzabile());
            statement.setString(9, prodotto.getImgPath());
            statement.setString(10, prodotto.getPublisher());
            statement.setLong(11, prodotto.getGruppo());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    prodotto.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    @Override
    public void doUpdate(ProdottoBean prodotto) throws SQLException {
        String sql = "UPDATE Prodotto SET nome = ?, descrizione = ?, taglia = ?, colore = ?, "
                + "categoria = ?, prezzo = ?, IVA = ?, personalizzabile = ?, imgPath = ?, "
                + "publisher = ?, gruppo = ? WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setString(3, prodotto.getTaglia());
            statement.setString(4, prodotto.getColore());
            statement.setString(5, prodotto.getCategoria());
            statement.setFloat(6, prodotto.getPrezzo());
            statement.setInt(7, prodotto.getIva());
            statement.setBoolean(8, prodotto.isPersonalizzabile());
            statement.setString(9, prodotto.getImgPath());
            statement.setString(10, prodotto.getPublisher());
            statement.setLong(11, prodotto.getGruppo());
            statement.setLong(12, prodotto.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        String sql = "DELETE FROM Prodotto WHERE id = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Metodi aggiuntivi specifici per Prodotto
    public List<ProdottoBean> doRetrieveByGruppo(Long gruppoId) throws SQLException {
        List<ProdottoBean> prodotti = new ArrayList<>();
        String sql = "SELECT * FROM Prodotto WHERE gruppo = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, gruppoId);
            try (ResultSet resultSet = statement.executeQuery()) {
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
                            resultSet.getBoolean("personalizzabile"),
                            resultSet.getString("imgPath"),
                            resultSet.getString("publisher"),
                            resultSet.getLong("gruppo")
                    ));
                }
            }
        }
        return prodotti;
    }
}