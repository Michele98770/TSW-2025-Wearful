package model.gruppoprodotti;

import model.ConnectionPool;
import model.DAOInterface;
import model.gruppoprodotti.GruppoProdottiBean;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GruppoProdottiDAO implements DAOInterface<GruppoProdottiBean, Long> {
    private Connection connectionPool;

    public GruppoProdottiDAO() {
        this.connectionPool = ConnectionPool.getConnection();
    }
    @Override
    public GruppoProdottiBean doRetrieveByKey(Long id) throws SQLException {
        String sql = "SELECT * FROM GruppoProdotti WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new GruppoProdottiBean(
                            resultSet.getLong("id"),
                            resultSet.getString("nome")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<GruppoProdottiBean> doRetrieveAll() throws SQLException {
        List<GruppoProdottiBean> gruppi = new ArrayList<>();
        String sql = "SELECT * FROM GruppoProdotti";

        try (Connection connection = connectionPool;
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                gruppi.add(new GruppoProdottiBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome")
                ));
            }
        }
        return gruppi;
    }

    @Override
    public void doSave(GruppoProdottiBean gruppo) throws SQLException {
        String sql = "INSERT INTO GruppoProdotti (nome) VALUES (?)";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, gruppo.getNome());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    gruppo.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    @Override
    public void doUpdate(GruppoProdottiBean gruppo) throws SQLException {
        String sql = "UPDATE GruppoProdotti SET nome = ? WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, gruppo.getNome());
            statement.setLong(2, gruppo.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void doDelete(Long id) throws SQLException {
        String sql = "DELETE FROM GruppoProdotti WHERE id = ?";

        try (Connection connection = connectionPool;
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }
}