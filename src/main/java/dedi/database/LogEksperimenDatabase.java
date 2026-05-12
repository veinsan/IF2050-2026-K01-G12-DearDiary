package dedi.database;

import dedi.model.LogEksperimen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data-access methods for the {@code log_eksperimen} table.
 *
 * <p>Child rows are cascade-deleted when their parent {@code ide_inovasi} row
 * is deleted. {@link SQLException} is caught and logged via
 * {@code printStackTrace}; read methods return empty lists on failure, write
 * methods return {@code false}. The underlying {@link Connection} is owned by
 * {@link DatabaseConnection} and must not be closed here.
 */
public class LogEksperimenDatabase {

    private static final String COLUMNS =
        "id_log, id_ide, tanggal, detail_eksperimen, path_lampiran";

    private static final String INSERT_SQL =
        "INSERT INTO log_eksperimen (id_ide, tanggal, detail_eksperimen, path_lampiran) " +
        "VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE log_eksperimen SET tanggal = ?, detail_eksperimen = ?, path_lampiran = ? " +
        "WHERE id_log = ?";

    private static final String DELETE_SQL =
        "DELETE FROM log_eksperimen WHERE id_log = ?";

    private static final String SELECT_BY_IDE_SQL =
        "SELECT " + COLUMNS + " FROM log_eksperimen WHERE id_ide = ? ORDER BY tanggal DESC";

    /**
     * Inserts {@code log} as a new row and writes the generated {@code id_log}
     * back onto the argument via {@link LogEksperimen#setIdLog(int)}.
     */
    public void insertLog(LogEksperimen log) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, log.getIdIde());
                ps.setObject(2, log.getTanggal());
                ps.setString(3, log.getDetailEksperimen());
                ps.setString(4, log.getPathLampiran());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        log.setIdLog(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the row identified by {@code log.getIdLog()}.
     *
     * @return {@code true} if a row was updated.
     */
    public boolean updateLog(LogEksperimen log) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                ps.setObject(1, log.getTanggal());
                ps.setString(2, log.getDetailEksperimen());
                ps.setString(3, log.getPathLampiran());
                ps.setInt(4, log.getIdLog());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes the row with the given {@code id_log}.
     *
     * @return {@code true} if a row was deleted.
     */
    public boolean deleteLog(int idLog) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
                ps.setInt(1, idLog);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns all log rows for the given {@code id_ide}, ordered by
     * {@code tanggal} descending (newest first).
     */
    public List<LogEksperimen> getLogByIdeId(int idIde) {
        List<LogEksperimen> rows = new ArrayList<>();
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

    private LogEksperimen mapRow(ResultSet rs) throws SQLException {
        return new LogEksperimen(
            rs.getInt("id_log"),
            rs.getInt("id_ide"),
            rs.getObject("tanggal", LocalDate.class),
            rs.getString("detail_eksperimen"),
            rs.getString("path_lampiran")
        );
    }
}
