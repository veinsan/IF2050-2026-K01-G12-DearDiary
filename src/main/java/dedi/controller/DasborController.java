package dedi.controller;

import java.util.List;

import dedi.database.IdeInovasiDatabase;
import dedi.model.IdeInovasi;

/**
 * Business-logic controller for the main dashboard showing a list of innovation
 * ideas (UC03: Melihat Daftar Ide Inovasi).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Load and display the complete list of innovation ideas.</li>
 *   <li>Support keyword search across title, code, author, and description.</li>
 *   <li>Support filtering by category and status.</li>
 *   <li>Delegate all data access to {@link IdeInovasiDatabase}.</li>
 * </ul>
 */
public class DasborController {

    private final IdeInovasiDatabase db = new IdeInovasiDatabase();

    /**
     * Loads the complete list of all innovation ideas ordered by ID.
     *
     * @return all innovation ideas, or an empty list if none exist or a
     *         database error occurred
     */
    public List<IdeInovasi> muatDaftarIde() {
        return db.getDaftarIde();
    }

    /**
     * Searches innovation ideas by keyword (case-insensitive substring match)
     * across title, code, author, and description.
     *
     * @param keyword the search term, or {@code null}/empty to match all ideas
     * @return matching innovation ideas
     */
    public List<IdeInovasi> cariIde(String keyword) {
        return db.cariIde(keyword);
    }

    /**
     * Filters innovation ideas by category and/or status.
     *
     * @param kategori filter by this category, or {@code null} to skip
     * @param status   filter by this status, or {@code null} to skip
     * @return filtered innovation ideas
     */
    public List<IdeInovasi> sariIde(String kategori, String status) {
        return db.filterIde(
            blankToNull(kategori),
            blankToNull(status)
        );
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
