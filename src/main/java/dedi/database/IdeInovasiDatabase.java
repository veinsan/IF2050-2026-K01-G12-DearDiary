package dedi.database;

import dedi.model.IdeInovasi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class IdeInovasiDatabase {

    private static final String COLUMNS =
        "id_ide, kode_inovasi, judul, penulis, kategori, deskripsi, " +
        "tanggal_dibuat, status, penanggung_jawab, prioritas";

    private static final String INSERT_SQL =
        "INSERT INTO ide_inovasi (kode_inovasi, judul, penulis, kategori, deskripsi, " +
        "tanggal_dibuat, status, penanggung_jawab, prioritas) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_SQL =
        "UPDATE ide_inovasi SET judul = ?, penulis = ?, kategori = ?, deskripsi = ?, " +
        "tanggal_dibuat = ?, status = ?, penanggung_jawab = ?, prioritas = ? " +
        "WHERE id_ide = ?";

    private static final String DELETE_SQL =
        "DELETE FROM ide_inovasi WHERE id_ide = ?";

    private static final String SELECT_BY_ID_SQL =
        "SELECT " + COLUMNS + " FROM ide_inovasi WHERE id_ide = ?";

    private static final String SELECT_ALL_SQL =
        "SELECT " + COLUMNS + " FROM ide_inovasi ORDER BY id_ide";

    private static final String SEARCH_SQL =
        "SELECT " + COLUMNS + " FROM ide_inovasi " +
        "WHERE judul ILIKE ? OR kode_inovasi ILIKE ? OR penulis ILIKE ? OR deskripsi ILIKE ? " +
        "ORDER BY id_ide";

    /**
     * Inserts {@code ide} as a new row and writes the generated {@code id_ide}
     * back onto the argument via {@link IdeInovasi#setIdIde(int)}.
     */
    public void insertIde(IdeInovasi ide) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                bindWriteFields(ps, ide, /* idAtEnd = */ false);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        ide.setIdIde(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the row identified by {@code ide.getIdIde()}.
     *
     * @return {@code true} if a row was updated.
     */
    public boolean updateIde(IdeInovasi ide) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {
                bindWriteFields(ps, ide, /* idAtEnd = */ true);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes the row with the given {@code id_ide}. Child rows in
     * {@code log_eksperimen} and {@code prototipe} are removed by the
     * schema's {@code ON DELETE CASCADE}.
     *
     * @return {@code true} if a row was deleted.
     */
    public boolean deleteIde(int idIde) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {
                ps.setInt(1, idIde);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Returns the row with the given {@code id_ide}, or {@code null} if none. */
    public IdeInovasi getById(int idIde) {
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {
                ps.setInt(1, idIde);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? mapRow(rs) : null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Returns all rows ordered by {@code id_ide} ascending. */
    public List<IdeInovasi> getDaftarIde() {
        List<IdeInovasi> rows = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(SELECT_ALL_SQL);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    /**
     * Case-insensitive substring search over {@code judul}, {@code kode_inovasi},
     * {@code penulis}, and {@code deskripsi}. A {@code null} or empty keyword
     * matches every row.
     */
    public List<IdeInovasi> cariIde(String keyword) {
        String pattern = "%" + (keyword == null ? "" : keyword) + "%";
        List<IdeInovasi> rows = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(SEARCH_SQL)) {
                ps.setString(1, pattern);
                ps.setString(2, pattern);
                ps.setString(3, pattern);
                ps.setString(4, pattern);
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

    /**
     * Returns rows matching the given {@code kategori} and/or {@code status}
     * exactly. A {@code null} argument means "any value" for that dimension;
     * passing {@code null} for both is equivalent to {@link #getDaftarIde()}.
     */
    public List<IdeInovasi> filterIde(String kategori, String status) {
        StringBuilder sql = new StringBuilder("SELECT ").append(COLUMNS).append(" FROM ide_inovasi");
        List<String> params = new ArrayList<>();
        if (kategori != null) {
            params.add(kategori);
        }
        if (status != null) {
            params.add(status);
        }
        if (!params.isEmpty()) {
            sql.append(" WHERE ");
            boolean first = true;
            if (kategori != null) {
                sql.append("kategori = ?");
                first = false;
            }
            if (status != null) {
                if (!first) sql.append(" AND ");
                sql.append("status = ?");
            }
        }
        sql.append(" ORDER BY id_ide");

        List<IdeInovasi> rows = new ArrayList<>();
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setString(i + 1, params.get(i));
                }
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

    private void bindWriteFields(PreparedStatement ps, IdeInovasi ide, boolean idAtEnd) throws SQLException {
        int i = 1;
        if (!idAtEnd) {
            ps.setString(i++, ide.getKodeInovasi());
        }
        ps.setString(i++, ide.getJudul());
        ps.setString(i++, ide.getPenulis());
        ps.setString(i++, ide.getKategori());
        ps.setString(i++, ide.getDeskripsi());
        ps.setObject(i++, ide.getTanggalDibuat());
        ps.setString(i++, ide.getStatus());
        ps.setString(i++, ide.getPenanggungJawab());
        ps.setString(i++, ide.getPrioritas());
        if (idAtEnd) {
            ps.setInt(i, ide.getIdIde());
        }
    }

    private IdeInovasi mapRow(ResultSet rs) throws SQLException {
        return new IdeInovasi(
            rs.getInt("id_ide"),
            rs.getString("kode_inovasi"),
            rs.getString("judul"),
            rs.getString("penulis"),
            rs.getString("kategori"),
            rs.getString("deskripsi"),
            rs.getObject("tanggal_dibuat", LocalDate.class),
            rs.getString("status"),
            rs.getString("penanggung_jawab"),
            rs.getString("prioritas")
        );
    }
}
