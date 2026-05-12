package dedi.model;

/**
 * Prototype version-history entry linked to one {@link IdeInovasi}.
 * Mirrors the {@code prototipe} table.
 *
 * <p>{@code idPrototipe} is the surrogate key; {@code 0} marks an unsaved
 * instance. {@code versi} is a free-form version label (e.g. "v1.0").
 * {@code status} is one of {@code "Draft"}, {@code "Stable"},
 * {@code "Deprecated"} — enforced as a {@code CHECK} on the table.
 */
public class Prototipe {

    private int idPrototipe;
    private int idIde;
    private String versi;
    private String status;
    private String deskripsiPerubahan;

    public Prototipe() {}

    public Prototipe(int idPrototipe, int idIde, String versi,
                     String status, String deskripsiPerubahan) {
        this.idPrototipe       = idPrototipe;
        this.idIde             = idIde;
        this.versi             = versi;
        this.status            = status;
        this.deskripsiPerubahan = deskripsiPerubahan;
    }

    public int getIdPrototipe() { return idPrototipe; }
    public void setIdPrototipe(int idPrototipe) { this.idPrototipe = idPrototipe; }

    public int getIdIde() { return idIde; }
    public void setIdIde(int idIde) { this.idIde = idIde; }

    public String getVersi() { return versi; }
    public void setVersi(String versi) { this.versi = versi; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeskripsiPerubahan() { return deskripsiPerubahan; }
    public void setDeskripsiPerubahan(String deskripsiPerubahan) {
        this.deskripsiPerubahan = deskripsiPerubahan;
    }
}
