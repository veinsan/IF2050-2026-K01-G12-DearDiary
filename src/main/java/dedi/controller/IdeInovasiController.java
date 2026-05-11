package dedi.controller;

import dedi.database.IdeInovasiDatabase;
import dedi.model.IdeInovasi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Business-logic controller for innovation-idea CRUD (UC03, UC04, UC05).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Validate user input before persistence.</li>
 *   <li>Auto-generate {@code kodeInovasi} (immutable after creation).</li>
 *   <li>Auto-generate {@code tanggalDibuat} at creation time.</li>
 *   <li>Delegate all persistence to {@link IdeInovasiDatabase}.</li>
 * </ul>
 *
 * <p>Role enforcement (Researcher-only mutations) is the caller's
 * responsibility; this class does not read session state.
 */
public class IdeInovasiController {

    private final IdeInovasiDatabase db = new IdeInovasiDatabase();

    /**
     * Returns {@code true} when {@code data} passes all mandatory-field checks.
     * Currently enforces that {@code judul} is non-null and non-blank.
     *
     * @param data the {@link IdeInovasi} to validate
     * @return {@code true} if valid, {@code false} otherwise
     */
    public boolean validasiInput(IdeInovasi data) {
        return data != null
            && data.getJudul() != null
            && !data.getJudul().isBlank();
    }

    /**
     * Generates a unique, system-assigned innovation code in the format
     * {@code INV-YYYYMMDD-NNNN} where {@code NNNN} is derived from the
     * current millisecond clock to minimise collisions within a session.
     *
     * @return a new, never-null kode string
     */
    public String generateKodeInovasi() {
        String datePart   = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%04d", System.currentTimeMillis() % 10_000);
        return "INV-" + datePart + "-" + randomPart;
    }

    /**
     * Returns today's date formatted as {@code yyyy-MM-dd} (ISO-8601).
     * Used by the view to show the auto-assigned creation date before saving.
     *
     * @return today's date string
     */
    public String generateTanggal() {
        return LocalDate.now().toString();
    }

    /**
     * Persists {@code data} as a new row. On success the database writes the
     * generated surrogate key back onto {@code data} via
     * {@link IdeInovasi#setIdIde(int)}, so callers can inspect
     * {@code data.getIdIde() > 0} to confirm success.
     *
     * <p>{@code data} must have been constructed with the system-generated
     * {@code kodeInovasi} and {@code tanggalDibuat} already set (use
     * {@link #generateKodeInovasi()} and {@link LocalDate#now()} for those).
     *
     * @param data fully populated {@link IdeInovasi} with {@code idIde == 0}
     */
    public void simpanDataBaru(IdeInovasi data) {
        db.insertIde(data);
    }

    /**
     * Updates the existing row identified by {@code data.getIdIde()}.
     * {@code kodeInovasi} is never included in the UPDATE statement (the
     * underlying SQL omits it), so it remains immutable.
     *
     * @param data modified {@link IdeInovasi} with {@code idIde > 0}
     * @return {@code true} if the row was updated
     */
    public boolean updateDataLama(IdeInovasi data) {
        return db.updateIde(data);
    }

    /**
     * Deletes the innovation idea identified by {@code idIde}. Cascade rules
     * on the database will also remove all linked log and prototype rows.
     *
     * @param idIde surrogate key of the row to remove
     */
    public void deleteIde(int idIde) {
        db.deleteIde(idIde);
    }
}
