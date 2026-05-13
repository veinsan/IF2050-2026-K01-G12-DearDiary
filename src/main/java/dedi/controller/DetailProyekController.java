package dedi.controller;

import dedi.database.IdeInovasiDatabase;
import dedi.database.LogEksperimenDatabase;
import dedi.model.IdeInovasi;
import dedi.model.LogEksperimen;
import dedi.model.Prototipe;

import java.util.List;

/**
 * Business-logic controller for loading a single innovation project's full
 * detail page (UC04).
 *
 * <p>Loads the parent {@link IdeInovasi} record plus its child
 * {@link LogEksperimen} and {@link Prototipe} collections from three
 * independent DAOs, and exposes them individually so the view can render
 * each section independently.
 *
 * <p>All methods return {@code null} / empty lists on DAO failure; the view
 * is responsible for showing an appropriate error state.
 */
public class DetailProyekController {

    private final IdeInovasiDatabase    db          = new IdeInovasiDatabase();
    private final LogEksperimenDatabase logDb       = new LogEksperimenDatabase();
    private final PrototipeController prototipeController = new PrototipeController();

    /**
     * Loads the {@link IdeInovasi} row for the given surrogate key.
     *
     * @param id the {@code id_ide} of the project to load
     * @return the matching {@link IdeInovasi}, or {@code null} if not found
     *         or a database error occurred
     */
    public IdeInovasi muatDetailProyek(int id) {
        return db.getById(id);
    }

    /**
     * Loads all experiment-log entries linked to the given idea, ordered by
     * {@code tanggal} descending (most recent first).
     *
     * @param id the {@code id_ide} of the parent idea
     * @return a (possibly empty) list of {@link LogEksperimen}
     */
    public List<LogEksperimen> muatListLog(int id) {
        return logDb.getLogByIdeId(id);
    }

    /**
     * Loads the full prototype-version history linked to the given idea,
     * ordered by insertion sequence ascending.
     *
     * @param id the {@code id_ide} of the parent idea
     * @return a (possibly empty) list of {@link Prototipe}
     */
    public List<Prototipe> muatRiwayatPrototipe(int id) {
        return prototipeController.getRiwayatPrototipe(id);
    }
}
