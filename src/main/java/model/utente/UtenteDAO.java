package model.utente;

import model.ConnectionPool;
import model.DAOInterface;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO implements DAOInterface<UtenteBean, String> {

    public UtenteDAO() {

    }

    @Override
    public UtenteBean doRetrieveByKey(String email) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        UtenteBean utente = null;

        String sql = "SELECT email, username, telefono, password, isAdmin FROM Utente WHERE email = ?";
        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, email);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                utente = new UtenteBean(
                        resultSet.getString("email"),
                        resultSet.getString("username"),
                        resultSet.getString("telefono"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isAdmin")
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
        return utente;
    }

    @Override
    public List<UtenteBean> doRetrieveAll() throws SQLException {
        List<UtenteBean> utenti = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        String sql = "SELECT email, username, telefono, password, isAdmin FROM Utente";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                utenti.add(new UtenteBean(
                        resultSet.getString("email"),
                        resultSet.getString("username"),
                        resultSet.getString("telefono"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isAdmin")
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
        return utenti;
    }

    @Override
    public void doSave(UtenteBean utente) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "INSERT INTO Utente (email, username, telefono, password, isAdmin) VALUES (?, ?, ?, ?, ?)";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, utente.getEmail());
            statement.setString(2, utente.getUsername());
            statement.setString(3, utente.getTelefono());
            statement.setString(4, utente.getPassword());
            statement.setBoolean(5, utente.isAdmin());

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
    public void doUpdate(UtenteBean utente) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "UPDATE Utente SET username = ?, telefono = ?, password = ?, isAdmin = ? WHERE email = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, utente.getUsername());
            statement.setString(2, utente.getTelefono());
            statement.setString(3, utente.getPassword());
            statement.setBoolean(4, utente.isAdmin());
            statement.setString(5, utente.getEmail());

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
    public void doDelete(String email) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;

        String sql = "DELETE FROM Utente WHERE email = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, email);
            statement.executeUpdate();
        } finally {
            try {
                if (statement != null) statement.close();
            } finally {
                ConnectionPool.releaseConnection(connection);
            }
        }
    }

    public UtenteBean doLogin(String email, String password) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        UtenteBean utente = null;

        String sql = "SELECT email, username, telefono, password, isAdmin FROM Utente WHERE email = ? AND password = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, email);
            statement.setString(2, password);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                utente = new UtenteBean(
                        resultSet.getString("email"),
                        resultSet.getString("username"),
                        resultSet.getString("telefono"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isAdmin")
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
        return utente;
    }

    public UtenteBean doRetrieveByEmail(String email) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        UtenteBean utente = null;

        String sql = "SELECT email, username, telefono, password, isAdmin FROM Utente WHERE email = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                utente = new UtenteBean(
                        resultSet.getString("email"),
                        resultSet.getString("username"),
                        resultSet.getString("telefono"),
                        resultSet.getString("password"),
                        resultSet.getBoolean("isAdmin")
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
        return utente;
    }
}