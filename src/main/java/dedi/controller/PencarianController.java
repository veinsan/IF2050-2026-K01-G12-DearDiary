package dedi.controller;

import java.util.List;

import dedi.database.IdeInovasiDatabase;
import dedi.model.IdeInovasi;

/**
 * Business-logic controller for searching innovation ideas (UC08).
 *
 * <p>Delegates search operations to {@link IdeInovasiDatabase} and keeps
 * the dashboard view decoupled from direct DAO access.
 */
public class PencarianController {

    private final IdeInovasiDatabase db = new IdeInovasiDatabase();

    /**
     * Searches innovation ideas by keyword across title, code, author, and
     * description.
     *
     * @param keyword the search term entered by the user
     * @return matching innovation ideas, or all ideas when the keyword is
     *         {@code null} or empty
     */
    public List<IdeInovasi> cariIde(String keyword) {
        return db.cariIde(keyword);
    }

    /**
     * Loads the full list of innovation ideas.
     *
     * @return all innovation ideas ordered by their surrogate primary key
     */
    public List<IdeInovasi> muatSemuaIde() {
        return db.getDaftarIde();
    }
}
