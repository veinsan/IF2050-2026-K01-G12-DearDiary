package dedi.model;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Entity metadata untuk file PDF yang dihasilkan (UC10).
 * Immutable: setelah dibuat, datanya nggak berubah.
 */
public class LaporanPDF {
    private final String namaFile;
    private final String lokasiPenyimpanan;
    private final LocalDateTime tanggalGenerate;

    public LaporanPDF(String namaFile, String lokasiPenyimpanan, LocalDateTime tanggalGenerate) {
        this.namaFile = Objects.requireNonNull(namaFile, "namaFile tidak boleh null");
        this.lokasiPenyimpanan = Objects.requireNonNull(lokasiPenyimpanan, "lokasi tidak boleh null");
        this.tanggalGenerate = Objects.requireNonNull(tanggalGenerate, "tanggal tidak boleh null");
    }

    public String getNamaFile() {
        return namaFile;
    }

    public String getLokasiPenyimpanan() {
        return lokasiPenyimpanan;
    }

    public LocalDateTime getTanggalGenerate() {
        return tanggalGenerate;
    }

    /** Full path lengkap dengan separator OS-appropriate. */
    public String getFullPath() {
        return lokasiPenyimpanan + File.separator + namaFile;
    }

    public String getTanggalGenerateFormatted() {
        return tanggalGenerate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    }
}