package dedi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton koneksi ke basis data PostgreSQL lokal.
 *
 * Digunakan oleh seluruh kelas database (IdeInovasiDatabase, dsb.)
 * untuk mendapatkan objek Connection tanpa duplikasi konfigurasi.
 *
 * Sesuai DPPL DeDi (DPPLOO-12):
 *   - DBMS    : PostgreSQL, berjalan lokal (offline)
 *   - KNF01   : Basis data lokal, akses hanya melalui aplikasi
 *   - KNF04   : Data tersimpan persisten di penyimpanan lokal
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:postgresql://localhost:5432/deardiary";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "postgres";

    private static Connection instance = null;

    // Mencegah instantiasi langsung
    private DatabaseConnection() {}

    /**
     * Mengembalikan instance koneksi tunggal ke basis data.
     * Membuat koneksi baru apabila belum ada atau sudah tertutup.
     *
     * @return objek Connection yang aktif
     * @throws SQLException jika koneksi gagal dibuat
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.isClosed()) {
            instance = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return instance;
    }

    /**
     * Menutup koneksi yang sedang aktif.
     * Dipanggil saat aplikasi ditutup.
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