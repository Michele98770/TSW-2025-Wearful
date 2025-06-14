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

    // Inizializzazione esplicita del pool
    public static synchronized void init(int poolSize) throws SQLException {
        if (initialized) {
            System.out.println("Connection pool already initialized.");
            return;
        }

        freeDbConnections = new LinkedList<>(); // Assicurati che sia inizializzato qui
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            for (int i = 0; i < poolSize; i++) {
                freeDbConnections.add(createDBConnection());
            }

            initialized = true;
            System.out.println("Connection pool initialized with " + poolSize + " connections.");
        } catch (ClassNotFoundException e) {
            initialized = false; // Se fallisce, resetta lo stato
            throw new SQLException("MySQL JDBC Driver not found or failed to load. Check your JDBC driver JAR.", e);
        } catch (SQLException e) {
            initialized = false; // Se fallisce, resetta lo stato
            System.err.println("Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
            throw e; // Rilancia l'eccezione SQL per notificare il listener
        }
    }

    // Metodo per creare una singola connessione al DB
    private static Connection createDBConnection() throws SQLException {
        System.out.println("Attempting to create new DB connection...");
        Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        connection.setAutoCommit(true); // Imposta auto-commit per default, come tipico per i pool
        System.out.println("New DB connection created.");
        return connection;
    }

    // Metodo per ottenere una connessione dal pool
    public static synchronized Connection getConnection() throws SQLException { // Aggiunto throws SQLException
        if (!initialized || freeDbConnections == null) {
            throw new SQLException("Connection Pool is not initialized.");
        }
        if (freeDbConnections.isEmpty()) {
            // Qui potresti voler aspettare che una connessione sia disponibile,
            // oppure lanciare un'eccezione subito. Per ora, lanceremo un'eccezione.
            throw new SQLException("Connection Pool is exhausted. No free connections available.");
        }

        Connection conn = freeDbConnections.remove(0);

        try {
            if (!conn.isValid(1)) { // Tempo di timeout in secondi
                System.out.println("DEBUG: Re-creating invalid connection.");
                try {
                    conn.close(); // Chiudi la connessione non valida
                } catch (SQLException e) {
                    System.err.println("Error closing invalid connection: " + e.getMessage());
                }
                return createDBConnection(); // Crea una nuova connessione valida e restituiscila
            }
        } catch (SQLException e) {
            System.err.println("SQLException when validating connection: " + e.getMessage());
            e.printStackTrace();
            // Se la validazione fallisce, tenta di creare una nuova connessione.
            // Potrebbe essere un problema di rete o DB, quindi crea un avviso.
            return createDBConnection(); // Tenta di rimpiazzare la connessione difettosa
        }

        return conn;
    }

    // Metodo per rilasciare una connessione al pool
    public static synchronized void releaseConnection(Connection connection) {
        if (connection != null && initialized) {
            try {
                if (!connection.isClosed() && connection.isValid(1)) { // Ricontrolla validità prima di rimettere
                    freeDbConnections.add(connection);
                    // System.out.println("DEBUG: Connection released to pool. Current size: " + freeDbConnections.size());
                } else {
                    System.out.println("DEBUG: Closing invalid or closed connection during release.");
                    connection.close(); // Chiudi la connessione non valida o già chiusa
                }
            } catch (SQLException e) {
                System.err.println("Error while releasing or closing connection: " + e.getMessage());
                e.printStackTrace();
            }
        } else if (connection != null) {
            // Se il pool non è più inizializzato ma riceviamo una connessione, chiudila
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection during shutdown: " + e.getMessage());
            }
        }
    }

    // Metodo per verificare se il pool è stato inizializzato
    public static synchronized boolean isInitialized() {
        return initialized;
    }

    // Metodo per lo spegnimento del pool
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

        // Pulizia driver MySQL (optional, ma buona pratica per evitare memory leaks)
        releaseResources();
    }

    // Metodo per il de-registrazione dei driver JDBC
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

        // Tenta di pulire il thread di cleanup delle connessioni abbandonate di MySQL (se presente)
        try {
            Class<?> clazz = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            clazz.getMethod("checkedShutdown").invoke(null);
            System.out.println("Executed AbandonedConnectionCleanupThread.checkedShutdown()");
        } catch (ClassNotFoundException e) {
            // La classe potrebbe non esistere in tutte le versioni del driver o configurazioni
            System.out.println("AbandonedConnectionCleanupThread not found (normal for some setups).");
        } catch (Exception e) {
            System.err.println("Error during AbandonedConnectionCleanupThread shutdown: " + e.getMessage());
            e.printStackTrace();
        }
    }
}