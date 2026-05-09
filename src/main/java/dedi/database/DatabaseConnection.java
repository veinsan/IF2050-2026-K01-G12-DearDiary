package dedi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton provider for the local PostgreSQL connection shared across
 * all database access classes in the DeDi application.
 *
 * Conforms to DPPLOO-12 (KNF01: local-only access, KNF04: persistent storage).
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/deardiary";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "postgres";

    private static Connection instance = null;

    private DatabaseConnection() {}

    /**
     * Returns the shared connection, reopening it if null or already closed.
     *
     * @return an active {@link Connection} to the local database
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }

    /**
     * Closes the active connection. Intended to be invoked during
     * application shutdown to release database resources.
     */
    public static void closeConnection() {
        try {
            if (instance != null && !instance.isClosed()) {
                instance.close();
                instance = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}