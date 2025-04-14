package model;

import java.sql.SQLException;
import java.util.List;

public interface DAOInterface<T, K> {

    T doRetrieveByKey(K key) throws SQLException;
    List<T> doRetrieveAll() throws SQLException;
    void doSave(T entity) throws SQLException;
    void doUpdate(T entity) throws SQLException;
    void doDelete(K key) throws SQLException;
}