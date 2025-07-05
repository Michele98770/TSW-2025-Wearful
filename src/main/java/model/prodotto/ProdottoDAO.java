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
import java.util.Objects;

public class ProdottoDAO implements DAOInterface<ProdottoBean, Long> {

    public ProdottoDAO() {

    }

    @Override
    public ProdottoBean doRetrieveByKey(Long id) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        ProdottoBean prodotto = null;

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
                        resultSet.getString("codiceColore"),
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
                        resultSet.getString("codiceColore"),
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

        String sql = "INSERT INTO Prodotto (nome, descrizione, taglia, colore, codiceColore, categoria, prezzo, IVA, disponibilita, personalizzabile, imgPath, publisher, gruppo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setString(3, prodotto.getTaglia());
            statement.setString(4, prodotto.getColore());
            statement.setString(5, prodotto.getCodiceColore());
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

        String sql = "UPDATE Prodotto SET nome=?, descrizione=?, taglia=?, colore=?, codiceColore=?, categoria=?, prezzo=?, IVA=?, disponibilita=?, personalizzabile=?, imgPath=?, publisher=?, gruppo=? WHERE id=?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            statement.setString(1, prodotto.getNome());
            statement.setString(2, prodotto.getDescrizione());
            statement.setString(3, prodotto.getTaglia());
            statement.setString(4, prodotto.getColore());
            statement.setString(5, prodotto.getCodiceColore());
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

    // NUOVO APPROCCIO PER DO_RETRIEVE_ALL_FILTERED
    public List<ProdottoBean> doRetrieveAllFiltered(
            String category,
            Float minPrice,
            Float maxPrice,
            List<String> sizes,
            int offset,
            int limit,
            String sortBy,
            String searchQuery
    ) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<ProdottoBean> prodotti = new ArrayList<>();

        StringBuilder sqlBuilder = new StringBuilder("SELECT id, nome, descrizione, taglia, colore, codiceColore, categoria, prezzo, IVA, disponibilita, personalizzabile, imgPath, publisher, gruppo FROM Prodotto WHERE id IN (SELECT MIN(id) FROM Prodotto WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        // Aggiungi condizioni di ricerca testuale alla subquery
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sqlBuilder.append(" AND (LOWER(nome) LIKE ? OR LOWER(descrizione) LIKE ? OR LOWER(categoria) LIKE ?)");
            String searchPattern = "%" + searchQuery.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        // Aggiungi filtri alla subquery
        if (category != null && !category.isEmpty()) {
            sqlBuilder.append(" AND categoria = ?");
            params.add(category);
        }

        if (minPrice != null) {
            sqlBuilder.append(" AND prezzo >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sqlBuilder.append(" AND prezzo <= ?");
            params.add(maxPrice);
        }

        if (sizes != null && !sizes.isEmpty()) {
            sqlBuilder.append(" AND (");
            for (int i = 0; i < sizes.size(); i++) {
                sqlBuilder.append("taglia = ?");
                params.add(sizes.get(i));
                if (i < sizes.size() - 1) {
                    sqlBuilder.append(" OR ");
                }
            }
            sqlBuilder.append(")");
        }

        sqlBuilder.append(" GROUP BY nome)"); // Chiude la subquery e raggruppa per nome al suo interno

        // Aggiungi l'ordinamento e la paginazione alla query esterna
        if (sortBy != null && !sortBy.isEmpty()) {
            switch (sortBy.toLowerCase()) {
                case "nome_asc":
                    sqlBuilder.append(" ORDER BY nome ASC");
                    break;
                case "nome_desc":
                    sqlBuilder.append(" ORDER BY nome DESC");
                    break;
                case "prezzo_asc":
                    sqlBuilder.append(" ORDER BY prezzo ASC");
                    break;
                case "prezzo_desc":
                    sqlBuilder.append(" ORDER BY prezzo DESC");
                    break;
                default:
                    sqlBuilder.append(" ORDER BY id ASC"); // Ordine di default
                    break;
            }
        } else {
            sqlBuilder.append(" ORDER BY id ASC"); // Ordine di default
        }

        sqlBuilder.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        String sql = sqlBuilder.toString();
        System.out.println("SQL Query for filtered products: " + sql);
        System.out.println("Parameters: " + params);

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                } else if (param instanceof Float) {
                    statement.setFloat(i + 1, (Float) param);
                } else if (param instanceof Integer) {
                    statement.setInt(i + 1, (Integer) param);
                } else if (param instanceof Long) {
                    statement.setLong(i + 1, (Long) param);
                }
            }

            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                prodotti.add(new ProdottoBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descrizione"),
                        resultSet.getString("taglia"),
                        resultSet.getString("colore"),
                        resultSet.getString("codiceColore"),
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

    // COUNT_ALL_FILTERED: Questo metodo rimane come prima, il COUNT(DISTINCT nome) è corretto
    public int countAllFiltered(
            String category,
            Float minPrice,
            Float maxPrice,
            List<String> sizes,
            String searchQuery
    ) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int totalProducts = 0;

        StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(DISTINCT nome) FROM Prodotto WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sqlBuilder.append(" AND (LOWER(nome) LIKE ? OR LOWER(descrizione) LIKE ? OR LOWER(categoria) LIKE ?)");
            String searchPattern = "%" + searchQuery.toLowerCase() + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }

        if (category != null && !category.isEmpty()) {
            sqlBuilder.append(" AND categoria = ?");
            params.add(category);
        }

        if (minPrice != null) {
            sqlBuilder.append(" AND prezzo >= ?");
            params.add(minPrice);
        }

        if (maxPrice != null) {
            sqlBuilder.append(" AND prezzo <= ?");
            params.add(maxPrice);
        }

        if (sizes != null && !sizes.isEmpty()) {
            sqlBuilder.append(" AND (");
            for (int i = 0; i < sizes.size(); i++) {
                sqlBuilder.append("taglia = ?");
                params.add(sizes.get(i));
                if (i < sizes.size() - 1) {
                    sqlBuilder.append(" OR ");
                }
            }
            sqlBuilder.append(")");
        }

        String sql = sqlBuilder.toString();
        System.out.println("SQL Count Query: " + sql);
        System.out.println("Count Parameters: " + params);

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);

            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    statement.setString(i + 1, (String) param);
                } else if (param instanceof Float) {
                    statement.setFloat(i + 1, (Float) param);
                } else if (param instanceof Integer) {
                    statement.setInt(i + 1, (Integer) param);
                } else if (param instanceof Long) {
                    statement.setLong(i + 1, (Long) param);
                }
            }

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalProducts = resultSet.getInt(1);
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
        return totalProducts;
    }


    public List<ProdottoBean> doRetrieveByGruppo(Long gruppoId) throws SQLException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<ProdottoBean> prodottiDelGruppo = new ArrayList<>();

        String sql = "SELECT id, nome, descrizione, taglia, colore, codiceColore, categoria, prezzo, IVA, disponibilita, personalizzabile, imgPath, publisher, gruppo FROM Prodotto WHERE gruppo = ?";

        try {
            connection = ConnectionPool.getConnection();
            statement = connection.prepareStatement(sql);
            statement.setLong(1, gruppoId);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                prodottiDelGruppo.add(new ProdottoBean(
                        resultSet.getLong("id"),
                        resultSet.getString("nome"),
                        resultSet.getString("descrizione"),
                        resultSet.getString("taglia"),
                        resultSet.getString("colore"),
                        resultSet.getString("codiceColore"),
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
        return prodottiDelGruppo;
    }
}