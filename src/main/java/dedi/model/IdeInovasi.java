package dedi.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Innovation project entity (UC02-UC05). Mirrors the {@code ide_inovasi} table.
 *
 * <p>{@code idIde} is the surrogate key assigned by the database; a value of
 * {@code 0} marks an unsaved instance. {@code kodeInovasi} is a system-generated
 * business key that the application is expected to leave unchanged once the row
 * has been persisted.
 *
 * <p>{@code status} must be one of {@code "ToDo"}, {@code "OnGoing"}, {@code "Done"}.
 * {@code prioritas} must be one of {@code "Rendah"}, {@code "Sedang"}, {@code "Tinggi"},
 * or {@code null} when unset. Both constraints are enforced by {@code CHECK} clauses
 * on the underlying table.
 */
public class IdeInovasi {

    private int idIde;
    private String kodeInovasi;
    private String judul;
    private String penulis;
    private String kategori;
    private String deskripsi;
    private LocalDate tanggalDibuat;
    private String status;
    private String penanggungJawab;
    private String prioritas;
    private List<LogEksperimen> daftarLog = new ArrayList<>();
    private List<Prototipe> daftarPrototipe = new ArrayList<>();

    public IdeInovasi() {}

    public IdeInovasi(int idIde, String kodeInovasi, String judul, String penulis,
                      String kategori, String deskripsi, LocalDate tanggalDibuat,
                      String status, String penanggungJawab, String prioritas) {
        this.idIde = idIde;
        this.kodeInovasi = kodeInovasi;
        this.judul = judul;
        this.penulis = penulis;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.tanggalDibuat = tanggalDibuat;
        this.status = status;
        this.penanggungJawab = penanggungJawab;
        this.prioritas = prioritas;
    }

    public int getIdIde() { return idIde; }
    public void setIdIde(int idIde) { this.idIde = idIde; }

    public String getKodeInovasi() { return kodeInovasi; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getPenulis() { return penulis; }
    public void setPenulis(String penulis) { this.penulis = penulis; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public LocalDate getTanggalDibuat() { return tanggalDibuat; }
    public void setTanggalDibuat(LocalDate tanggalDibuat) { this.tanggalDibuat = tanggalDibuat; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPenanggungJawab() { return penanggungJawab; }
    public void setPenanggungJawab(String penanggungJawab) { this.penanggungJawab = penanggungJawab; }

    public String getPrioritas() { return prioritas; }
    public void setPrioritas(String prioritas) { this.prioritas = prioritas; }

    public List<LogEksperimen> getDaftarLog() { return daftarLog; }
    public void setDaftarLog(List<LogEksperimen> daftarLog) { this.daftarLog = daftarLog; }

    public List<Prototipe> getDaftarPrototipe() { return daftarPrototipe; }
    public void setDaftarPrototipe(List<Prototipe> daftarPrototipe) { this.daftarPrototipe = daftarPrototipe; }
}
