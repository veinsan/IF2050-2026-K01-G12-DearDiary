package dedi.database;
import dedi.model.LogEksperimen;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogEksperimenDatabase {
    public List<LogEksperimen> getAllLogsByIde(int idIde) {
        List<LogEksperimen> list = new ArrayList<>();
        Connection conn;
        try {
            conn = DatabaseConnection.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM log_eksperimen WHERE id_ide = ? ORDER BY tanggal DESC")) {
            pstmt.setInt(1, idIde);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new LogEksperimen(rs.getInt("id_log"), rs.getInt("id_ide"), 
                    rs.getDate("tanggal").toLocalDate(), rs.getString("tujuan"), 
                    rs.getString("hasil"), rs.getString("kesimpulan"), rs.getString("path_lampiran")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void insertLog(LogEksperimen log) {
        Connection conn;
        try {
            conn = DatabaseConnection.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        String sql = "INSERT INTO log_eksperimen (id_ide, tanggal, tujuan, hasil, kesimpulan, path_lampiran) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, log.getIdIde());
            pstmt.setDate(2, Date.valueOf(log.getTanggal()));
            pstmt.setString(3, log.getTujuan());
            pstmt.setString(4, log.getHasil());
            pstmt.setString(5, log.getKesimpulan());
            pstmt.setString(6, log.getPathLampiran());
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    public void deleteLog(int idLog) {
        Connection conn;
        try {
            conn = DatabaseConnection.getInstance();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM log_eksperimen WHERE id_log = ?")) {
            pstmt.setInt(1, idLog);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    /**
     * Backwards-compatible wrapper used by some callers expecting this name.
     */
    public List<LogEksperimen> getLogByIdeId(int idIde) {
        return getAllLogsByIde(idIde);
    }
}