package dedi.controller;

import dedi.database.PrototipeDatabase;
import dedi.model.Prototipe;

import java.util.List;

/**
 * Business-logic controller for managing prototype history (UC07).
 *
 * <p>Responsibilities:
 * <ul>
 * <li>Validate user input for new prototype versions.</li>
 * <li>Retrieve chronological history of prototypes for a specific idea.</li>
 * <li>Delegate all persistence operations to {@link PrototipeDatabase}.</li>
 * </ul>
 */
public class PrototipeController {

    private final PrototipeDatabase db = new PrototipeDatabase();

    /**
     * Loads the full prototype-version history linked to the given idea,
     * ordered by insertion sequence ascending.
     *
     * @param idIde the {@code id_ide} of the parent idea
     * @return a (possibly empty) list of {@link Prototipe}
     */
    public List<Prototipe> getRiwayatPrototipe(int idIde) {
        return db.getPrototipeByIdeId(idIde);
    }

    /**
     * Validates and persists {@code prototipeBaru} as a new row.
     * On success, the database writes the generated surrogate key back onto
     * the object via {@link Prototipe#setIdPrototipe(int)}.
     *
     * @param prototipeBaru fully populated {@link Prototipe} with {@code idPrototipe == 0}
     * @return {@code true} if validation passed and persistence succeeded
     */
    public boolean manageVersi(Prototipe prototipeBaru) {
        if (prototipeBaru == null || prototipeBaru.getVersi() == null || prototipeBaru.getVersi().isBlank()) {
            return false;
        }

        db.insertPrototipe(prototipeBaru);

        return prototipeBaru.getIdPrototipe() > 0;
    }
}