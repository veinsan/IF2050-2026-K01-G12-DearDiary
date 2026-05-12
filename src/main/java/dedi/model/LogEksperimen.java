package dedi.model;
import java.time.LocalDate;

public class LogEksperimen {
    private int idLog, idIde;
    private LocalDate tanggal;
    private String tujuan, hasil, kesimpulan, detailEksperimen, pathLampiran;

    public LogEksperimen() {}

    public LogEksperimen(int idLog, int idIde, LocalDate tanggal, String tujuan, String hasil, String kesimpulan, String pathLampiran) {
        this(idLog, idIde, tanggal, tujuan, hasil, kesimpulan, null, pathLampiran);
    }

    public LogEksperimen(int idLog, int idIde, LocalDate tanggal, String tujuan, String hasil, String kesimpulan,
                         String detailEksperimen, String pathLampiran) {
        this.idLog = idLog; this.idIde = idIde; this.tanggal = tanggal;
        this.tujuan = tujuan; this.hasil = hasil; this.kesimpulan = kesimpulan;
        this.detailEksperimen = detailEksperimen; this.pathLampiran = pathLampiran;
    }

    // Getters and Setters
    public int getIdLog() { return idLog; }
    public void setIdLog(int idLog) { this.idLog = idLog; }
    public int getIdIde() { return idIde; }
    public void setIdIde(int idIde) { this.idIde = idIde; }
    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }
    public String getTujuan() { return tujuan; }
    public void setTujuan(String tujuan) { this.tujuan = tujuan; }
    public String getHasil() { return hasil; }
    public void setHasil(String hasil) { this.hasil = hasil; }
    public String getKesimpulan() { return kesimpulan; }
    public void setKesimpulan(String kesimpulan) { this.kesimpulan = kesimpulan; }
    public String getDetailEksperimenText() { return detailEksperimen; }
    public void setDetailEksperimenText(String detailEksperimen) { this.detailEksperimen = detailEksperimen; }
    public String getPathLampiran() { return pathLampiran; }
    public void setPathLampiran(String pathLampiran) { this.pathLampiran = pathLampiran; }

    /**
     * Convenience accessor used by the view layer. Returns a short textual
     * description of the experiment detail. Preferentially returns the
     * 'hasil' field, falling back to 'tujuan' when hasil is null/empty.
     */
    public String getDetailEksperimen() {
        if (detailEksperimen != null && !detailEksperimen.isBlank()) return detailEksperimen;
        if (hasil != null && !hasil.isBlank()) return hasil;
        if (tujuan != null && !tujuan.isBlank()) return tujuan;
        return "";
    }
}
