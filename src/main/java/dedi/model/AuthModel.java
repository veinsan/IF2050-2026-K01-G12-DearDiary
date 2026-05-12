package dedi.model;

import dedi.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Login authentication against the {@code pengguna} table.
 *
 * <p>{@link SQLException} is caught and logged via {@code printStackTrace};
 * a failed lookup returns {@code null}. The underlying {@link Connection} is
 * owned by {@link DatabaseConnection} and must not be closed here.
 */
public class AuthModel {

    private static final String SELECT_LOGIN_SQL =
        "SELECT username, password, role FROM pengguna " +
        "WHERE username = ? AND password = ?";

    /**
     * Returns the {@link Pengguna} matching the given credentials, or
     * {@code null} if no row matches.
     */
    private String lastErrorMessage = null;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public Pengguna getUserData(String username, String password) {
        try {
            System.out.println("[AuthModel] lookup user: '" + username + "'");
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(SELECT_LOGIN_SQL)) {
                ps.setString(1, username);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Pengguna(
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                        );
                    }
                    System.out.println("[AuthModel] no pengguna found for '" + username + "'");
                    lastErrorMessage = null;
                    return null;
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = e.getMessage();
            System.out.println("[AuthModel] SQLException while looking up user '" + username + "': " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
