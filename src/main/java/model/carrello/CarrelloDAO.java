package model.carrello;

import model.ConnectionPool;
import model.DAOInterface;
import model.carrello.CarrelloBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarrelloDAO implements DAOInterface<CarrelloBean, Long> {
    private Connection connectionPool;

    public CarrelloDAO() {
        this.connectionPool = ConnectionPool.getConnection();
    }
    @Override
    public CarrelloBean doRetrieveByKey(Long id) throws SQLException {
        String sql = "SELECT * FROM Carrello WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new CarrelloBean(
                            resultSet.getLong("id"),
                            resultSet.getString("idUtente")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<CarrelloBean> doRetrieveAll() throws SQLException {
        List<CarrelloBean> carrelli = new ArrayList<>();
        String sql = "SELECT * FROM Carrello";

        try (Connection connection = connectionPool;
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                carrelli.add(new CarrelloBean(
                        resultSet.getLong("id"),
                        resultSet.getString("idUtente")
                ));
            }
        }
        return carrelli;
    }

    @Override
    public void doSave(CarrelloBean carrello) throws SQLException {
        String sql = "INSERT INTO Carrello (idUtente) VALUES (?)";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, carrello.getIdUtente());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    carrello.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    @Override
    public void doUpdate(CarrelloBean carrello) throws SQLException {
        String sql = "UPDATE Carrello SET idUtente = ? WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, carrello.getIdUtente());
            statement.setLong(2, carrello.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        String sql = "DELETE FROM Carrello WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    // Metodo aggiuntivo per ottenere il carrello di un utente
    public CarrelloBean doRetrieveByUtente(String idUtente) throws SQLException {
        String sql = "SELECT * FROM Carrello WHERE idUtente = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, idUtente);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new CarrelloBean(
                            resultSet.getLong("id"),
                            resultSet.getString("idUtente")
                    );
                }
            }
        }
        return null;
    }
}