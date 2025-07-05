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

    public static synchronized void init(int poolSize) throws SQLException {
        if (initialized) {
            System.out.println("Connection pool already initialized.");
            return;
        }

        freeDbConnections = new LinkedList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            for (int i = 0; i < poolSize; i++) {
                freeDbConnections.add(createDBConnection());
            }

            initialized = true;
            System.out.println("Connection pool initialized with " + poolSize + " connections.");
        } catch (ClassNotFoundException e) {
            initialized = false;
            throw new SQLException("MySQL JDBC Driver not found or failed to load. Check your JDBC driver JAR.", e);
        } catch (SQLException e) {
            initialized = false;
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private static Connection createDBConnection() throws SQLException {
        System.out.println("Attempting to create new DB connection...");
        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        connection.setAutoCommit(true); // Imposta auto-commit per default, come tipico per i pool
        System.out.println("New DB connection created.");
        return connection;
    }

    public static synchronized Connection getConnection() throws SQLException { // Aggiunto throws SQLException
        if (!initialized || freeDbConnections == null) {
            throw new SQLException("Connection Pool is not initialized.");
        }
        if (freeDbConnections.isEmpty()) {
            throw new SQLException("Connection Pool is exhausted. No free connections available.");
        }

        Connection conn = freeDbConnections.remove(0);

        try {
            if (!conn.isValid(1)) {
                System.out.println("DEBUG: Re-creating invalid connection.");
                try {
                    conn.close(); // Chiudi la connessione non valida
                } catch (SQLException e) {
                    System.err.println("Error closing invalid connection: " + e.getMessage());
                }
                return createDBConnection();
            }
        } catch (SQLException e) {
            System.err.println("SQLException when validating connection: " + e.getMessage());
            e.printStackTrace();

            return createDBConnection();
        }

        return conn;
    }


    public static synchronized void releaseConnection(Connection connection) {
        if (connection != null && initialized) {
            try {
                if (!connection.isClosed() && connection.isValid(1)) {
                    freeDbConnections.add(connection);

                } else {
                    System.out.println("DEBUG: Closing invalid or closed connection during release.");
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error while releasing or closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection during shutdown: " + e.getMessage());
            }
        }
    }

    public static synchronized boolean isInitialized() {
        return initialized;
    }


    public static synchronized void shutdown() {
        if (!initialized) {
            System.out.println("Connection pool is not initialized, nothing to shut down.");
            return;
        }

        System.out.println("Shutting down connection pool...");
        for (Connection conn : freeDbConnections) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection during shutdown: " + e.getMessage());
            }
        }
        if (freeDbConnections != null) {
            freeDbConnections.clear();
        }
        initialized = false;
        System.out.println("Connection pool shut down.");


        releaseResources();
    }


    public static void releaseResources() {
        System.out.println("Deregistering JDBC drivers...");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println("Deregistered JDBC driver: " + driver.getClass().getName());
            } catch (SQLException e) {
                System.err.println("Error deregistering driver " + driver.getClass().getName() + ": " + e.getMessage());
            }
        }


        try {
            Class<?> clazz = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            clazz.getMethod("checkedShutdown").invoke(null);
            System.out.println("Executed AbandonedConnectionCleanupThread.checkedShutdown()");
        } catch (ClassNotFoundException e) {

            System.out.println("AbandonedConnectionCleanupThread not found (normal for some setups).");
        } catch (Exception e) {
            System.err.println("Error during AbandonedConnectionCleanupThread shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}