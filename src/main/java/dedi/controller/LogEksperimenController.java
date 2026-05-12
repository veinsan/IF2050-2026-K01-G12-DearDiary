package dedi.controller;

import dedi.database.LogEksperimenDatabase;
import dedi.model.LogEksperimen;
import dedi.view.LogEksperimenView;
import javafx.scene.image.Image;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;

public class LogEksperimenController {
    private static final long MAX_LAMPIRAN_BYTES = 5L * 1024L * 1024L;

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
        view.btnPilihGambar.setOnAction(e -> handlePilihGambar());
        view.btnSimpan.setOnAction(e -> handleSimpan());
        view.btnHapus.setOnAction(e -> handleHapus());

        refreshTable();
    }

    private void handleSimpan() {
        if (!validasiLampiran()) {
            return;
        }

        LogEksperimen log = new LogEksperimen(
            0, idIdeAktif, LocalDate.now(), 
            view.txtTujuan.getText(), view.txtHasil.getText(), 
            view.txtKesimpulan.getText(), view.txtDetail.getText(), view.txtPath.getText()
        );
        db.insertLog(log);
        clearFields();
        refreshTable();
        showSuccess("Log eksperimen berhasil disimpan.");
    }

    private void handlePilihGambar() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Pilih Lampiran Gambar");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Gambar PNG/JPG", "*.png", "*.jpg", "*.jpeg")
        );

        File selected = chooser.showOpenDialog(view.getScene().getWindow());
        if (selected == null) {
            return;
        }

        String validationMessage = getLampiranError(selected);
        if (validationMessage != null) {
            showError(validationMessage);
            return;
        }

        view.txtPath.setText(selected.getAbsolutePath());
        view.imgPreview.setImage(new Image(selected.toURI().toString(), true));
        showSuccess("Lampiran gambar siap disimpan.");
    }

    private void handleHapus() {
        LogEksperimen selected = view.table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            db.deleteLog(selected.getIdLog());
            refreshTable();
            showSuccess("Log eksperimen berhasil dihapus.");
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
        view.txtKesimpulan.clear(); view.txtDetail.clear(); view.txtPath.clear();
        view.imgPreview.setImage(null);
    }

    private boolean validasiLampiran() {
        String path = view.txtPath.getText();
        if (path == null || path.isBlank()) {
            return true;
        }

        String validationMessage = getLampiranError(new File(path));
        if (validationMessage != null) {
            showError(validationMessage);
            return false;
        }
        return true;
    }

    private String getLampiranError(File file) {
        if (file == null || !file.exists() || !file.isFile()) {
            return "Berkas lampiran tidak ditemukan.";
        }
        String name = file.getName().toLowerCase();
        if (!(name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg"))) {
            return "Lampiran harus berupa gambar .png atau .jpg.";
        }
        if (file.length() > MAX_LAMPIRAN_BYTES) {
            return "Ukuran berkas gambar maksimal 5 MB.";
        }
        return null;
    }

    private void showError(String message) {
        view.feedbackLabel.getStyleClass().setAll("error-label");
        view.feedbackLabel.setText(message);
        view.feedbackLabel.setVisible(true);
        view.feedbackLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        view.feedbackLabel.getStyleClass().setAll("success-label");
        view.feedbackLabel.setText(message);
        view.feedbackLabel.setVisible(true);
        view.feedbackLabel.setManaged(true);
    }
}
