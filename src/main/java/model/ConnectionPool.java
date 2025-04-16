package model;

import java.sql.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class ConnectionPool {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/wearful"
            + "?useUnicode=true&useJDBCCompliantTimezoneShift=true"
            + "&useLegacyDatetimecode=false&serverTimezone=UTC";

    private static final String USERNAME = "root";
    private static final String PASSWORD = "miky";
    private static List<Connection> freeDbConnections;
    private static boolean initialized = false;

    // Inizializzazione esplicita
    public static synchronized void init(int poolSize) throws SQLException {
        if (initialized) {
            return;
        }

        freeDbConnections = new LinkedList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            for (int i = 0; i < poolSize; i++) {
                freeDbConnections.add(createDBConnection());
            }

            initialized = true;
            System.out.println("Connection pool initialized with " + poolSize + " connections");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    private static Connection createDBConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }

    public static synchronized Connection getConnection()  {

        Connection conn = freeDbConnections.remove(0);

        try {
            if (!conn.isValid(1)) {
                conn.close();
                return getConnection(); // Ricorsione per ottenere una nuova connessione valida
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public static synchronized void releaseConnection(Connection connection) {
        if (connection != null && initialized) {
            try {
                if (!connection.isClosed() && connection.isValid(1)) {
                    freeDbConnections.add(connection);
                } else {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error while releasing connection: " + e.getMessage());
            }
        }
    }

    public static synchronized void shutdown() {
        if (!initialized) {
            return;
        }

        for (Connection conn : freeDbConnections) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
        freeDbConnections.clear();
        initialized = false;

        // Pulizia driver MySQL
        releaseResources();
    }

    public static void releaseResources() {

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
            } catch (SQLException e) {
                System.err.println("Error deregistering driver: " + e.getMessage());
            }
        }

        try {
            Class<?> clazz = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            clazz.getMethod("checkedShutdown").invoke(null);
        } catch (Exception e) {

        }
    }
}