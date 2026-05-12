package dedi.model;

import java.time.LocalDate;

/**
 * Prototype version history entity (UC07). Mirrors the {@code prototipe} table.
 *
 * <p>{@code idPrototipe} is the surrogate primary key assigned by the database;
 * a value of {@code 0} typically marks an unsaved instance. {@code idIde} acts
 * as the foreign key linking this prototype version to its parent {@link IdeInovasi}.
 *
 * <p>This entity tracks the physical or conceptual evolution of an innovation
 * project over time. It is designed to be an append-only ledger where each
 * iteration is recorded with a specific {@code versi} (e.g., "v1.0", "v1.1").
 */
public class Prototipe {

    private int idPrototipe;
    private int idIde;
    private String versi;
    private String status;
    private String deskripsiPerubahan;
    private LocalDate tanggalPerubahan;

    /**
     * Constructs a new, empty {@code Prototipe} instance.
     */
    public Prototipe() {}

    /**
     * Constructs a fully initialized {@code Prototipe} instance.
     *
     * @param idPrototipe        The unique ID assigned by the database.
     * @param idIde              The foreign key linking to the parent {@link IdeInovasi}.
     * @param versi              The semantic version label (e.g., "v1.0").
     * @param status             The current development status of this prototype version.
     * @param deskripsiPerubahan The description of changes made in this version.
     * @param tanggalPerubahan   The date when this version was recorded.
     */
    public Prototipe(int idPrototipe, int idIde, String versi, String status,
                     String deskripsiPerubahan, LocalDate tanggalPerubahan) {
        this.idPrototipe = idPrototipe;
        this.idIde = idIde;
        this.versi = versi;
        this.status = status;
        this.deskripsiPerubahan = deskripsiPerubahan;
        this.tanggalPerubahan = tanggalPerubahan;
    }

    /* Getter & Setter */

    public int getIdPrototipe() {
        return idPrototipe;
    }

    public void setIdPrototipe(int idPrototipe) {
        this.idPrototipe = idPrototipe;
    }

    public int getIdIde() {
        return idIde;
    }

    public void setIdIde(int idIde) {
        this.idIde = idIde;
    }

    public String getVersi() {
        return versi;
    }

    public void setVersi(String versi) {
        this.versi = versi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeskripsiPerubahan() {
        return deskripsiPerubahan;
    }

    public void setDeskripsiPerubahan(String deskripsiPerubahan) {
        this.deskripsiPerubahan = deskripsiPerubahan;
    }

    public LocalDate getTanggalPerubahan() {
        return this.tanggalPerubahan;
    }

    public void setTanggalPerubahan(LocalDate tanggalPerubahan) {
        this.tanggalPerubahan = tanggalPerubahan;
    }
}