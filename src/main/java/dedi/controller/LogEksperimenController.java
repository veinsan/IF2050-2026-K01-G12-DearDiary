package dedi.controller;

import dedi.database.LogEksperimenDatabase;
import dedi.model.LogEksperimen;
import dedi.view.LogEksperimenView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;

public class LogEksperimenController {
    private LogEksperimenView view;
    private LogEksperimenDatabase db;
    private int idIdeAktif = 1;

    public LogEksperimenController(LogEksperimenView view) {
        this(view, 1);
    }

    /**
     * Create controller bound to a specific idea id.
     */
    public LogEksperimenController(LogEksperimenView view, int idIdeAktif) {
        this.view = view;
        this.db = new LogEksperimenDatabase();
        this.idIdeAktif = idIdeAktif;
        initController();
    }

    private void initController() {
        // Binding Kolom Tabel
        view.table.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("tanggal"));
        view.table.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("tujuan"));

        // Event Handler Tombol
        view.btnSimpan.setOnAction(e -> handleSimpan());
        view.btnHapus.setOnAction(e -> handleHapus());

        refreshTable();
    }

    private void handleSimpan() {
        LogEksperimen log = new LogEksperimen(
            0, idIdeAktif, LocalDate.now(), 
            view.txtTujuan.getText(), view.txtHasil.getText(), 
            view.txtKesimpulan.getText(), view.txtPath.getText()
        );
        db.insertLog(log);
        clearFields();
        refreshTable();
    }

    private void handleHapus() {
        LogEksperimen selected = view.table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            db.deleteLog(selected.getIdLog());
            refreshTable();
        }
    }

    private void refreshTable() {
        view.table.getItems().setAll(db.getAllLogsByIde(idIdeAktif));
    }

    /**
     * Change the active idea id and refresh the table. Useful when the
     * view is opened for a specific project after construction.
     */
    public void setIdIdeAktif(int idIdeAktif) {
        this.idIdeAktif = idIdeAktif;
        refreshTable();
    }

    private void clearFields() {
        view.txtTujuan.clear(); view.txtHasil.clear(); 
        view.txtKesimpulan.clear(); view.txtPath.clear();
    }
}