package dedi.model;

import dedi.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Login authentication against the {@code pengguna} table.
 *
 * <p>{@link SQLException} is caught and logged via {@code printStackTrace};
 * a failed lookup returns {@code null}. The underlying {@link Connection} is
 * owned by {@link DatabaseConnection} and must not be closed here.
 */
public class AuthModel {

    private static final String SELECT_LOGIN_SQL =
        "SELECT username, password, role FROM pengguna WHERE username = ?";

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
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        if (!passwordMatches(password, storedPassword)) {
                            System.out.println("[AuthModel] password mismatch for '" + username + "'");
                            lastErrorMessage = null;
                            return null;
                        }
                        return new Pengguna(
                            rs.getString("username"),
                            storedPassword,
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

    private boolean passwordMatches(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (storedPassword.startsWith("sha256$")) {
            return storedPassword.equals(hashPassword(rawPassword));
        }
        return storedPassword.equals(rawPassword);
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                hex.append(String.format("%02x", b));
            }
            return "sha256$" + hex;
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 tidak tersedia", e);
        }
    }
}
