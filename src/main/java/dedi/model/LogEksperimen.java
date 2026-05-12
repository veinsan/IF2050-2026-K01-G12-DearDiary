package dedi.model;

import java.time.LocalDate;

/**
 * Experiment-log entry linked to one {@link IdeInovasi}. Mirrors the
 * {@code log_eksperimen} table.
 *
 * <p>{@code idLog} is the surrogate key; {@code 0} marks an unsaved instance.
 * {@code pathLampiran} may be {@code null} when no file attachment exists.
 */
public class LogEksperimen {

    private int idLog;
    private int idIde;
    private LocalDate tanggal;
    private String detailEksperimen;
    private String pathLampiran;

    public LogEksperimen() {}

    public LogEksperimen(int idLog, int idIde, LocalDate tanggal,
                         String detailEksperimen, String pathLampiran) {
        this.idLog            = idLog;
        this.idIde            = idIde;
        this.tanggal          = tanggal;
        this.detailEksperimen = detailEksperimen;
        this.pathLampiran     = pathLampiran;
    }

    public int getIdLog() { return idLog; }
    public void setIdLog(int idLog) { this.idLog = idLog; }

    public int getIdIde() { return idIde; }
    public void setIdIde(int idIde) { this.idIde = idIde; }

    public LocalDate getTanggal() { return tanggal; }
    public void setTanggal(LocalDate tanggal) { this.tanggal = tanggal; }

    public String getDetailEksperimen() { return detailEksperimen; }
    public void setDetailEksperimen(String detailEksperimen) {
        this.detailEksperimen = detailEksperimen;
    }

    public String getPathLampiran() { return pathLampiran; }
    public void setPathLampiran(String pathLampiran) { this.pathLampiran = pathLampiran; }
}
