package dedi.database;

import dedi.model.Prototipe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access methods for the {@code prototipe} table.
 *
 * <p>Child rows are cascade-deleted when their parent {@code ide_inovasi} row
 * is deleted. {@link SQLException} is caught and logged via
 * {@code printStackTrace}; read methods return empty lists on failure, write
 * methods return {@code false}. The underlying {@link Connection} is owned by
 * {@link DatabaseConnection} and must not be closed here.
 */
public class PrototipeDatabase {

    private static final String COLUMNS =
        "id_prototipe, id_ide, versi, status, deskripsi_perubahan";

    private static final String INSERT_SQL =
        "INSERT INTO prototipe (id_ide, versi, status, deskripsi_perubahan) " +
        "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE prototipe SET versi = ?, status = ?, deskripsi_perubahan = ? " +
        "WHERE id_prototipe = ?";

    private static final String DELETE_SQL =
        "DELETE FROM prototipe WHERE id_prototipe = ?";

    private static final String SELECT_BY_IDE_SQL =
        "SELECT " + COLUMNS + " FROM prototipe WHERE id_ide = ? ORDER BY id_prototipe";

    /**
     * Inserts {@code p} as a new row and writes the generated
     * {@code id_prototipe} back via {@link Prototipe#setIdPrototipe(int)}.
     */
    public void insertPrototipe(Prototipe p) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, p.getIdIde());
                ps.setString(2, p.getVersi());
                ps.setString(3, p.getStatus());
                ps.setString(4, p.getDeskripsiPerubahan());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        p.setIdPrototipe(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the row identified by {@code p.getIdPrototipe()}.
     *
     * @return {@code true} if a row was updated.
     */
    public boolean updatePrototipe(Prototipe p) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                ps.setString(1, p.getVersi());
                ps.setString(2, p.getStatus());
                ps.setString(3, p.getDeskripsiPerubahan());
                ps.setInt(4, p.getIdPrototipe());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes the row with the given {@code id_prototipe}.
     *
     * @return {@code true} if a row was deleted.
     */
    public boolean deletePrototipe(int idPrototipe) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
                ps.setInt(1, idPrototipe);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns all prototype rows for the given {@code id_ide}, ordered by
     * {@code id_prototipe} ascending (chronological insertion order).
     */
    public List<Prototipe> getPrototipeByIdeId(int idIde) {
        List<Prototipe> rows = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_IDE_SQL)) {
                ps.setInt(1, idIde);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        rows.add(mapRow(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    private Prototipe mapRow(ResultSet rs) throws SQLException {
        return new Prototipe(
            rs.getInt("id_prototipe"),
            rs.getInt("id_ide"),
            rs.getString("versi"),
            rs.getString("status"),
            rs.getString("deskripsi_perubahan")
        );
    }
}
