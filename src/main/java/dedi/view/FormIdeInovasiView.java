package dedi.view;

import dedi.controller.IdeInovasiController;
import dedi.controller.LoginController;
import dedi.model.IdeInovasi;
import dedi.Main;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * FXML controller / View boundary for the Add-and-Edit innovation-idea form
 * (UC05: Mengelola Ide Inovasi).
 *
 * <p>The form operates in two modes, set by the caller via
 * {@link #setMode(Mode, IdeInovasi)} immediately after
 * {@code FXMLLoader.load()}:
 * <ul>
 *   <li>{@link Mode#TAMBAH} — blank form, system auto-generates
 *       {@code kodeInovasi} and {@code tanggalDibuat}.</li>
 *   <li>{@link Mode#EDIT} — pre-filled from an existing
 *       {@link IdeInovasi}; {@code kodeInovasi} is displayed but locked.</li>
 * </ul>
 *
 * <p>On save the view delegates validation and persistence to
 * {@link IdeInovasiController}, then navigates automatically:
 * EDIT mode returns to the detail page; TAMBAH mode returns to the dashboard.
 *
 * <p>Attributes from SKPL: {@code controller}, {@code inputJudul},
 * {@code inputKategori}, {@code inputDeskripsi}, {@code inputPenanggungJawab},
 * {@code inputPrioritas}.
 */
public class FormIdeInovasiView implements Initializable {

    /** Distinguishes create vs. update operations. */
    public enum Mode { TAMBAH, EDIT }

    /* ------------------------------------------------------------------ */
    /* FXML-injected fields                                                */
    /* ------------------------------------------------------------------ */

    @FXML private Label    titleLabel;
    @FXML private Label    kodeInovasiLabel;
    @FXML private Label    tanggalLabel;

    @FXML private TextField judulField;
    @FXML private TextField kategoriField;
    @FXML private TextArea  deskripsiArea;
    @FXML private TextField penanggungJawabField;

    @FXML private ComboBox<String> prioritasCombo;
    @FXML private ComboBox<String> statusCombo;

    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    /* ------------------------------------------------------------------ */
    /* State                                                               */
    /* ------------------------------------------------------------------ */

    private final IdeInovasiController controller = new IdeInovasiController();

    private Mode      currentMode;
    private IdeInovasi existingIde;
    private String    generatedKode;

    /* ------------------------------------------------------------------ */
    /* Initializable                                                       */
    /* ------------------------------------------------------------------ */

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        prioritasCombo.getItems().addAll("", "Rendah", "Sedang", "Tinggi");
        statusCombo.getItems().addAll("ToDo", "OnGoing", "Done");
        prioritasCombo.getSelectionModel().selectFirst();
        statusCombo.getSelectionModel().selectFirst();
    }

    /* ------------------------------------------------------------------ */
    /* Public API called by the navigating controller                      */
    /* ------------------------------------------------------------------ */

    /**
     * Configures the form for the given {@code mode}.
     *
     * <p>For {@link Mode#TAMBAH}: auto-generates {@code kodeInovasi} and
     * shows today's date; all input fields start blank.
     * For {@link Mode#EDIT}: pre-populates every field from {@code ide};
     * {@code kodeInovasi} is displayed read-only and cannot be changed.
     *
     * @param mode the operation mode (must not be null)
     * @param ide  the existing record to edit (ignored in TAMBAH mode,
     *             must not be null in EDIT mode)
     */
    public void setMode(Mode mode, IdeInovasi ide) {
        this.currentMode  = mode;
        this.existingIde  = ide;

        if (mode == Mode.TAMBAH) {
            titleLabel.setText("Tambah Ide Inovasi Baru");
            generatedKode = controller.generateKodeInovasi();
            kodeInovasiLabel.setText(generatedKode);
            tanggalLabel.setText(controller.generateTanggal());
        } else {
            titleLabel.setText("Edit Ide Inovasi");
            kodeInovasiLabel.setText(ide.getKodeInovasi());
            tanggalLabel.setText(
                ide.getTanggalDibuat() != null ? ide.getTanggalDibuat().toString() : "-");

            judulField.setText(nvl(ide.getJudul()));
            kategoriField.setText(nvl(ide.getKategori()));
            deskripsiArea.setText(nvl(ide.getDeskripsi()));
            penanggungJawabField.setText(nvl(ide.getPenanggungJawab()));

            if (ide.getPrioritas() != null) {
                prioritasCombo.setValue(ide.getPrioritas());
            }
            if (ide.getStatus() != null) {
                statusCombo.setValue(ide.getStatus());
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /* SKPL-specified public view methods                                  */
    /* ------------------------------------------------------------------ */

    /** Makes the form visible (used when embedding rather than scene-swap). */
    public void tampilkanForm() {
        judulField.getScene().getRoot().setVisible(true);
    }

    /**
     * Locks the kode field so the user cannot edit it.
     * The underlying control is already a read-only {@code Label}, so this
     * is a no-op in the FXML layout; exposed for completeness.
     */
    public void kunciAtributKode() {
        // kodeInovasiLabel is a Label (not a TextField); it is inherently read-only.
    }

    /** Shows the persistent success message banner. */
    public void tampilkanPesanSukses() {
        successLabel.setText("Data berhasil disimpan.");
        successLabel.setVisible(true);
        successLabel.setManaged(true);
    }

    /** Shows the mandatory-field error message. */
    public void tampilkanPesanError() {
        errorLabel.setText("Atribut wajib harus diisi. Pastikan Judul tidak kosong.");
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /* ------------------------------------------------------------------ */
    /* FXML event handlers                                                 */
    /* ------------------------------------------------------------------ */

    /** Validates input, persists data, shows feedback, then navigates away. */
    @FXML
    private void handleSimpan() {
        clearMessages();

        String judul          = judulField.getText();
        String kategori       = kategoriField.getText();
        String deskripsi      = deskripsiArea.getText();
        String penanggungJawab = penanggungJawabField.getText();
        String prioritas      = prioritasCombo.getValue();
        String status         = statusCombo.getValue();
        String username       = LoginController.currentUser != null
                                ? LoginController.currentUser.getUsername() : "";

        IdeInovasi data;

        if (currentMode == Mode.TAMBAH) {
            data = new IdeInovasi(
                0,
                generatedKode,
                judul,
                username,
                kategori,
                deskripsi,
                LocalDate.now(),
                (status != null && !status.isBlank()) ? status : "ToDo",
                penanggungJawab,
                (prioritas != null && !prioritas.isBlank()) ? prioritas : null
            );
        } else {
            existingIde.setJudul(judul);
            existingIde.setKategori(kategori);
            existingIde.setDeskripsi(deskripsi);
            existingIde.setPenanggungJawab(penanggungJawab);
            existingIde.setPrioritas((prioritas != null && !prioritas.isBlank()) ? prioritas : null);
            existingIde.setStatus((status != null && !status.isBlank()) ? status : "ToDo");
            data = existingIde;
        }

        if (!controller.validasiInput(data)) {
            tampilkanPesanError();
            return;
        }

        boolean ok;
        if (currentMode == Mode.TAMBAH) {
            controller.simpanDataBaru(data);
            ok = data.getIdIde() > 0;
        } else {
            ok = controller.updateDataLama(data);
        }

        if (!ok) {
            errorLabel.setText("Gagal menyimpan data ke database. Coba lagi.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }

        tampilkanPesanSukses();

        int savedId = data.getIdIde();
        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(e -> {
            if (currentMode == Mode.EDIT) {
                navigateToDetail(savedId);
            } else {
                navigateToDashboard();
            }
        });
        pause.play();
    }

    /** Discards edits and navigates back without saving. */
    @FXML
    private void handleBatal() {
        if (currentMode == Mode.EDIT && existingIde != null) {
            navigateToDetail(existingIde.getIdIde());
        } else {
            navigateToDashboard();
        }
    }

    /* ------------------------------------------------------------------ */
    /* Navigation helpers                                                  */
    /* ------------------------------------------------------------------ */

    private void navigateToDetail(int idIde) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/dedi/view/detail_proyek.fxml"));
            Parent root = loader.load();
            DetailProyekView view = loader.getController();
            view.loadProyek(idIde);
            Main.switchRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    /* ------------------------------------------------------------------ */
    /* Utility                                                             */
    /* ------------------------------------------------------------------ */

    private void clearMessages() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }
}
