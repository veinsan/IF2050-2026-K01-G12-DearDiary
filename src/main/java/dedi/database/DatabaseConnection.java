package dedi.database;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton provider for the local PostgreSQL connection shared across
 * all database access classes in the DeDi application.
 *
 * This class will attempt to bootstrap the database schema from
 * ./sql/schema.sql automatically if the core table `pengguna` is not found.
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/deardiary";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "irghisatya8";

    private static Connection instance = null;

    private DatabaseConnection() {}

    /**
     * Returns the shared connection, reopening it if null or already closed.
     * If the expected schema is missing, attempts to run ./sql/schema.sql.
     *
     * @return an active {@link Connection} to the local database
     * @throws SQLException if the connection cannot be established
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
            try {
                ensureSchema(instance);
            } catch (Exception e) {
                // Don't fail hard here; allow app to surface DB errors later.
                System.err.println("[DatabaseConnection] schema bootstrap failed: " + e.getMessage());
                e.printStackTrace();
            }
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

    /**
     * If the core table `pengguna` is missing, read and execute statements from sql/schema.sql.
     */
    private static void ensureSchema(Connection conn) throws SQLException, IOException {
        DatabaseMetaData md = conn.getMetaData();
        // Try to find the `pengguna` table (Postgres typically uses lowercase names)
        try (ResultSet rs = md.getTables(null, null, "pengguna", new String[] {"TABLE"})) {
            if (rs != null && rs.next()) {
                // table exists - nothing to do
                return;
            }
        }

        // No pengguna table found - attempt to run sql/schema.sql from project root
        Path schemaPath = Paths.get(System.getProperty("user.dir"), "sql", "schema.sql");
        if (!Files.exists(schemaPath)) {
            System.err.println("[DatabaseConnection] schema.sql not found at: " + schemaPath.toString());
            return;
        }

        String sql = new String(Files.readAllBytes(schemaPath), StandardCharsets.UTF_8);

        // Naive split on semicolons followed by newline or end-of-input; good enough for our schema file.
        String[] statements = sql.split(";\\s*(?:\\r?\\n|$)");

        try (Statement st = conn.createStatement()) {
            conn.setAutoCommit(false);
            for (String s : statements) {
                String stmt = s.trim();
                if (stmt.isEmpty()) continue;
                try {
                    st.execute(stmt);
                } catch (SQLException ex) {
                    // Log and continue - partial schema may still be useful for debugging.
                    System.err.println("[DatabaseConnection] Failed to execute statement: " + stmt);
                    ex.printStackTrace();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            System.out.println("[DatabaseConnection] schema.sql executed (bootstrap complete).");
        } catch (SQLException ex) {
            // Attempt to rollback if possible
            try {
                conn.rollback();
            } catch (SQLException r) {
                // ignore
            }
            throw ex;
        }
    }
}