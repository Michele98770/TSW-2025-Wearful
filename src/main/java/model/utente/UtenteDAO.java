package model.utente;

import model.ConnectionPool;
import model.DAOInterface;
import model.utente.UtenteBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO implements DAOInterface<UtenteBean, String> {
    private Connection connectionPool;

    public UtenteDAO() {
        this.connectionPool = ConnectionPool.getConnection();
    }

    @Override
    public UtenteBean doRetrieveByKey(String email) throws SQLException {
        String sql = "SELECT * FROM Utente WHERE email = ?";
        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UtenteBean(
                            resultSet.getString("email"),
                            resultSet.getString("username"),
                            resultSet.getString("telefono"),
                            resultSet.getString("password"),
                            resultSet.getBoolean("isAdmin")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<UtenteBean> doRetrieveAll() throws SQLException {
        List<UtenteBean> utenti = new ArrayList<>();
        String sql = "SELECT * FROM Utente";

        try (Connection connection = connectionPool;
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                utenti.add(new UtenteBean(
                        resultSet.getString("email"),
                        resultSet.getString("username"),
                        resultSet.getString("telefono"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isAdmin")
                ));
            }
        }
        return utenti;
    }

    @Override
    public void doSave(UtenteBean utente) throws SQLException {
        String sql = "INSERT INTO Utente (email, username, telefono, password, isAdmin) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, utente.getEmail());
            statement.setString(2, utente.getUsername());
            statement.setString(3, utente.getTelefono());
            statement.setString(4, utente.getPassword());
            statement.setBoolean(5, utente.isAdmin());

            statement.executeUpdate();
        }
    }

    @Override
    public void doUpdate(UtenteBean utente) throws SQLException {
        String sql = "UPDATE Utente SET username = ?, telefono = ?, password = ?, isAdmin = ? WHERE email = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, utente.getUsername());
            statement.setString(2, utente.getTelefono());
            statement.setString(3, utente.getPassword());
            statement.setBoolean(4, utente.isAdmin());
            statement.setString(5, utente.getEmail());

            statement.executeUpdate();
        }
    }

    @Override
    public void doDelete(String email) throws SQLException {
        String sql = "DELETE FROM Utente WHERE email = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.executeUpdate();
        }
    }

    // Metodo aggiuntivo per il login
    public UtenteBean doLogin(String email, String password) throws SQLException {
        String sql = "SELECT * FROM Utente WHERE email = ? AND password = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UtenteBean(
                            resultSet.getString("email"),
                            resultSet.getString("username"),
                            resultSet.getString("telefono"),
                            resultSet.getString("password"),
                            resultSet.getBoolean("isAdmin")
                    );
                }
            }
        }
        return null;
    }
}