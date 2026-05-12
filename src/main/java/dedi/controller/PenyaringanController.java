package dedi.controller;

import java.util.List;

import dedi.database.IdeInovasiDatabase;
import dedi.model.IdeInovasi;

/**
 * Business-logic controller for filtering innovation ideas by category and status
 * (UC09).
 */
public class PenyaringanController {

    private final IdeInovasiDatabase db = new IdeInovasiDatabase();

    public List<IdeInovasi> filterIde(String kategori, String status) {
        return db.filterIde(blankToNull(kategori), blankToNull(status));
    }

    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }
}
