package model.infoconsegna;

import model.ConnectionPool;
import model.DAOInterface;
import model.infoconsegna.InfoConsegnaBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InfoConsegnaDAO implements DAOInterface<InfoConsegnaBean, Long> {
    private Connection connectionPool;

    public InfoConsegnaDAO() {
        this.connectionPool = ConnectionPool.getConnection();
    }

    @Override
    public InfoConsegnaBean doRetrieveByKey(Long id) throws SQLException {
        String sql = "SELECT * FROM InfoConsegna WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new InfoConsegnaBean(
                            resultSet.getLong("id"),
                            resultSet.getString("citta"),
                            resultSet.getInt("cap"),
                            resultSet.getString("via"),
                            resultSet.getString("altro"),
                            resultSet.getString("destinatario"),
                            resultSet.getString("idUtente")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<InfoConsegnaBean> doRetrieveAll() throws SQLException {
        List<InfoConsegnaBean> infoConsegne = new ArrayList<>();
        String sql = "SELECT * FROM InfoConsegna";

        try (Connection connection = connectionPool;
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                infoConsegne.add(new InfoConsegnaBean(
                        resultSet.getLong("id"),
                        resultSet.getString("citta"),
                        resultSet.getInt("cap"),
                        resultSet.getString("via"),
                        resultSet.getString("altro"),
                        resultSet.getString("destinatario"),
                        resultSet.getString("idUtente")
                ));
            }
        }
        return infoConsegne;
    }

    @Override
    public void doSave(InfoConsegnaBean infoConsegna) throws SQLException {
        String sql = "INSERT INTO InfoConsegna (citta, cap, via, altro, destinatario, idUtente) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, infoConsegna.getCitta());
            statement.setInt(2, infoConsegna.getCap());
            statement.setString(3, infoConsegna.getVia());
            statement.setString(4, infoConsegna.getAltro());
            statement.setString(5, infoConsegna.getDestinatario());
            statement.setString(6, infoConsegna.getIdUtente());

            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    infoConsegna.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    @Override
    public void doUpdate(InfoConsegnaBean infoConsegna) throws SQLException {
        String sql = "UPDATE InfoConsegna SET citta = ?, cap = ?, via = ?, altro = ?, "
                + "destinatario = ?, idUtente = ? WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, infoConsegna.getCitta());
            statement.setInt(2, infoConsegna.getCap());
            statement.setString(3, infoConsegna.getVia());
            statement.setString(4, infoConsegna.getAltro());
            statement.setString(5, infoConsegna.getDestinatario());
            statement.setString(6, infoConsegna.getIdUtente());
            statement.setLong(7, infoConsegna.getId());

            statement.executeUpdate();
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        String sql = "DELETE FROM InfoConsegna WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Metodi aggiuntivi specifici per InfoConsegna
    public List<InfoConsegnaBean> doRetrieveByUtente(String idUtente) throws SQLException {
        List<InfoConsegnaBean> infoConsegne = new ArrayList<>();
        String sql = "SELECT * FROM InfoConsegna WHERE idUtente = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, idUtente);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    infoConsegne.add(new InfoConsegnaBean(
                            resultSet.getLong("id"),
                            resultSet.getString("citta"),
                            resultSet.getInt("cap"),
                            resultSet.getString("via"),
                            resultSet.getString("altro"),
                            resultSet.getString("destinatario"),
                            resultSet.getString("idUtente")
                    ));
                }
            }
        }
        return infoConsegne;
    }
}