package dedi.view;

import dedi.Main;
import dedi.controller.DetailProyekController;
import dedi.controller.IdeInovasiController;
import dedi.controller.LaporanController;
import dedi.controller.LoginController;
import dedi.controller.LogEksperimenController;
import dedi.model.IdeInovasi;
import dedi.model.LaporanPDF;
import dedi.model.LogEksperimen;
import dedi.model.Prototipe;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * FXML controller / View boundary for the innovation-project detail page
 * (UC04: Melihat Detail Proyek Inovasi).
 *
 * <p>Navigation entry point: call {@link #loadProyek(int)} immediately after
 * {@code FXMLLoader.load()} to inject the project identifier and trigger data
 * loading. All {@code @FXML} fields are injected before this method runs.
 *
 * <p>Sections displayed:
 * <ol>
 *   <li>Full {@link IdeInovasi} attribute block.</li>
 *   <li>{@link LogEksperimen} list (newest first).</li>
 *   <li>{@link Prototipe} version history (insertion order).</li>
 * </ol>
 *
 * <p>Edit, Delete, "Tambah Log", and "Tambah Prototipe" buttons are visible
 * only when the current user holds the {@code "Researcher"} role. Export-PDF
 * and Back are available to all roles.
 *
 * <p>Attribute from SKPL: {@code controller : DetailProyekController}.
 */
public class DetailProyekView {

    /* ------------------------------------------------------------------ */
    /* FXML-injected — innovation detail labels                            */
    /* ------------------------------------------------------------------ */

    @FXML private Label kodeInovasiLabel;
    @FXML private Label judulLabel;
    @FXML private Label kategoriLabel;
    @FXML private Label statusLabel;
    @FXML private Label prioritasLabel;
    @FXML private Label penulisLabel;
    @FXML private Label penanggungJawabLabel;
    @FXML private Label tanggalDibuatLabel;
    @FXML private Label deskripsiLabel;

    /* ------------------------------------------------------------------ */
    /* FXML-injected — log & prototype sections                            */
    /* ------------------------------------------------------------------ */

    @FXML private ListView<LogEksperimen> logListView;
    @FXML private Label                   emptyLogLabel;

    @FXML private ListView<Prototipe> prototipeListView;
    @FXML private Label               emptyProtoLabel;

    /* ------------------------------------------------------------------ */
    /* FXML-injected — role-gated action buttons                          */
    /* ------------------------------------------------------------------ */

    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button tambahLogButton;
    @FXML private Button tambahProtoButton;

    /* ------------------------------------------------------------------ */
    /* State                                                               */
    /* ------------------------------------------------------------------ */

    /** SKPL-specified attribute. */
    private final DetailProyekController controller    = new DetailProyekController();
    private final IdeInovasiController   ideController = new IdeInovasiController();

    private IdeInovasi currentIde;

    /* ------------------------------------------------------------------ */
    /* Public API called by the navigating controller                      */
    /* ------------------------------------------------------------------ */

    /**
     * Loads the full project detail for {@code idIde} and renders every
     * section. Call immediately after {@code FXMLLoader.load()}.
     *
     * <p>If the project cannot be found (e.g., deleted between navigation
     * and load), an error alert is shown and the method returns early.
     *
     * @param idIde surrogate key of the {@link IdeInovasi} to display
     */
    public void loadProyek(int idIde) {
        IdeInovasi ide = controller.muatDetailProyek(idIde);
        if (ide == null) {
            showErrorAlert("Data proyek tidak dapat dimuat saat ini.");
            return;
        }
        this.currentIde = ide;

        tampilkanDetailProyek(ide);
        tampilkanListLog(controller.muatListLog(idIde));
        tampilkanRiwayatPrototipe(controller.muatRiwayatPrototipe(idIde));
        configureRoleBasedButtons();
    }

    /* ------------------------------------------------------------------ */
    /* SKPL-specified public view methods                                  */
    /* ------------------------------------------------------------------ */

    /**
     * Populates all innovation-detail labels from {@code ide}.
     * Safe to call with any non-null {@link IdeInovasi}; null field values
     * are displayed as {@code "-"}.
     *
     * @param ide the record to display (must not be null)
     */
    public void tampilkanDetailProyek(IdeInovasi ide) {
        kodeInovasiLabel.setText(nvl(ide.getKodeInovasi()));
        judulLabel.setText(nvl(ide.getJudul()));
        kategoriLabel.setText(nvl(ide.getKategori()));
        statusLabel.setText(nvl(ide.getStatus()));
        prioritasLabel.setText(nvl(ide.getPrioritas()));
        penulisLabel.setText(nvl(ide.getPenulis()));
        penanggungJawabLabel.setText(nvl(ide.getPenanggungJawab()));
        tanggalDibuatLabel.setText(
            ide.getTanggalDibuat() != null ? ide.getTanggalDibuat().toString() : "-");
        deskripsiLabel.setText(nvl(ide.getDeskripsi()));
    }

    /**
     * Populates the experiment-log list, or shows the empty-state label
     * when {@code logs} is null or empty.
     *
     * @param logs list returned by {@link DetailProyekController#muatListLog}
     */
    public void tampilkanListLog(List<LogEksperimen> logs) {
        if (logs == null || logs.isEmpty()) {
            emptyLogLabel.setVisible(true);
            emptyLogLabel.setManaged(true);
            logListView.setVisible(false);
            logListView.setManaged(false);
            return;
        }

        emptyLogLabel.setVisible(false);
        emptyLogLabel.setManaged(false);
        logListView.setVisible(true);
        logListView.setManaged(true);

        logListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(LogEksperimen item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String tanggal = item.getTanggal() != null
                        ? item.getTanggal().toString() : "?";
                    String detail  = item.getDetailEksperimen() != null
                        ? item.getDetailEksperimen() : "";
                    String lampiran = item.getPathLampiran() != null
                        ? "  📎 " + item.getPathLampiran() : "";
                    setText("[" + tanggal + "]  " + detail + lampiran);
                }
            }
        });
        logListView.getItems().setAll(logs);
    }

    /**
     * Populates the prototype-version list, or shows the empty-state label
     * when {@code prototypes} is null or empty.
     *
     * @param prototypes list returned by
     *                   {@link DetailProyekController#muatRiwayatPrototipe}
     */
    public void tampilkanRiwayatPrototipe(List<Prototipe> prototypes) {
        if (prototypes == null || prototypes.isEmpty()) {
            emptyProtoLabel.setVisible(true);
            emptyProtoLabel.setManaged(true);
            prototipeListView.setVisible(false);
            prototipeListView.setManaged(false);
            return;
        }

        emptyProtoLabel.setVisible(false);
        emptyProtoLabel.setManaged(false);
        prototipeListView.setVisible(true);
        prototipeListView.setManaged(true);

        prototipeListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Prototipe item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("v" + nvl(item.getVersi())
                        + "  [" + nvl(item.getStatus()) + "]  —  "
                        + nvl(item.getDeskripsiPerubahan()));
                }
            }
        });
        prototipeListView.getItems().setAll(prototypes);
    }

    /* ------------------------------------------------------------------ */
    /* FXML event handlers                                                 */
    /* ------------------------------------------------------------------ */

    /** Opens the edit form pre-filled with the current project (Researcher only). */
    @FXML
    private void handleEdit() {
        if (currentIde == null) return;
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/dedi/view/form_ide_inovasi.fxml"));
            Parent root = loader.load();
            FormIdeInovasiView formView = loader.getController();
            formView.setMode(FormIdeInovasiView.Mode.EDIT, currentIde);
            Main.switchRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Confirms then deletes the current project and returns to the dashboard (Researcher only). */
    @FXML
    private void handleDelete() {
        if (currentIde == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus Ide Inovasi: " + currentIde.getJudul());
        confirm.setContentText(
            "Semua log eksperimen dan riwayat prototipe terkait akan ikut terhapus.\n"
            + "Tindakan ini tidak dapat dibatalkan.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                ideController.deleteIde(currentIde.getIdIde());
                navigateToDashboard();
            }
        });
    }

    /** Opens the log management page bound to the current project. */
    @FXML
    private void handleTambahLog() {
        if (currentIde == null) {
            showErrorAlert("Tidak ada proyek yang dipilih.");
            return;
        }
        LogEksperimenView logView = new LogEksperimenView();
        new LogEksperimenController(logView, currentIde.getIdIde());
        logView.getStylesheets().add(getClass().getResource("/dedi/css/app.css").toExternalForm());
        Main.switchRoot(logView);
    }

    /** Placeholder — full prototype form is a separate deliverable. */
    @FXML
    private void handleTambahPrototipe() {
        if (currentIde == null) return; // Pastikan ada proyek yang lagi dibuka
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dedi/view/form_prototipe.fxml"));
            Parent root = loader.load();

            PrototipeFormView formView = loader.getController();
            formView.setIdeInovasi(currentIde);

            Main.switchRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Navigates back to the dashboard list. */
    @FXML
    private void handleBack() {
        navigateToDashboard();
    }

    /** Placeholder — PDF export uses iTextPDF and is a separate deliverable. */
    @FXML
    private void handleExportPdf() {
        if (currentIde == null) {
            showAlert("Peringatan", "Tidak ada ide yang dipilih.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );

        // Default filename
        String timestamp = java.time.LocalDateTime.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        fileChooser.setInitialFileName(
            "Laporan_" + currentIde.getKodeInovasi() + "_" + timestamp + ".pdf"
        );

        File file = fileChooser.showSaveDialog(judulLabel.getScene().getWindow());
        if (file == null) return; // user cancel

        try {
            LaporanController controller = new LaporanController();
            LaporanPDF laporan = controller.export(currentIde.getIdIde(), file);

            showAlert("Sukses", "PDF berhasil disimpan di:\n" + laporan.getFullPath());
        } catch (Exception e) {
            showAlert("Gagal", "Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }


    /* ------------------------------------------------------------------ */
    /* Helpers                                                             */
    /* ------------------------------------------------------------------ */

    private void configureRoleBasedButtons() {
        boolean isResearcher = LoginController.currentUser != null
            && "Researcher".equals(LoginController.currentUser.getRole());

        setButtonVisible(editButton,       isResearcher);
        setButtonVisible(deleteButton,     isResearcher);
        setButtonVisible(tambahLogButton,  isResearcher);
        setButtonVisible(tambahProtoButton, isResearcher);
    }

    private static void setButtonVisible(Button b, boolean visible) {
        b.setVisible(visible);
        b.setManaged(visible);
    }

    private void navigateToDashboard() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/dedi/view/dashboard.fxml"));
            Main.switchRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Gagal Memuat Data");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private static String nvl(String s) {
        return (s != null && !s.isBlank()) ? s : "-";
    }
}
