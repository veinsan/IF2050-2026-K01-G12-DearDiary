package dedi.view;

import dedi.Main;
import dedi.controller.PrototipeController;
import dedi.model.IdeInovasi;
import dedi.model.Prototipe;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FXML controller / View boundary for the Manage Prototype History page
 * (UC07: Mencatat Riwayat Prototipe).
 *
 * <p>This view allows the researcher to add new prototype versions and view the
 * historical progression of prototypes associated with a specific {@link IdeInovasi}.
 * Data validation and persistence are delegated to {@link PrototipeController}.
 *
 * <p>Navigation entry point: call {@link #setIdeInovasi(IdeInovasi)} immediately after
 * {@code FXMLLoader.load()} to inject the active project and trigger data loading.
 */
public class PrototipeFormView implements Initializable {

    // FXML-injected fields

    @FXML private Label judulProyekLabel;

    @FXML private TableView<Prototipe> tbl_prototipe;
    @FXML private TableColumn<Prototipe, String> colVersi;
    @FXML private TableColumn<Prototipe, String> colStatus;
    @FXML private TableColumn<Prototipe, String> colTanggal;
    @FXML private TableColumn<Prototipe, String> colDeskripsi;

    @FXML private TextField txtVersi;
    @FXML private ComboBox<String> choiceStatus;
    @FXML private TextArea txt_deskripsi_perubahan;

    @FXML private Label errorLabel;
    @FXML private Label successLabel;
    @FXML private Button btn_simpan_versi;

    // State

    private final PrototipeController controller = new PrototipeController();
    private IdeInovasi currentIde;

    // Initializable

    /**
     * Initializes the controller class. Automatically called after the FXML file
     * has been loaded. Sets up the combo box options and configures the table
     * column value factories.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceStatus.getItems().addAll("ToDo", "OnGoing", "Done");
        choiceStatus.getSelectionModel().selectFirst();

        colVersi.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVersi()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        colDeskripsi.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDeskripsiPerubahan()));
        colTanggal.setCellValueFactory(cellData -> {
            LocalDate tgl = cellData.getValue().getTanggalPerubahan();
            return new SimpleStringProperty(tgl != null ? tgl.toString() : "-");
        });
    }

    // Public API

    /**
     * Sets the active innovation project context and triggers the loading of its
     * associated prototype history into the table.
     *
     * @param ide the {@link IdeInovasi} target for this prototype history.
     */
    public void setIdeInovasi(IdeInovasi ide) {
        this.currentIde = ide;
        if (ide != null) {
            judulProyekLabel.setText("Proyek: " + ide.getJudul());
            muatRiwayatTabel();
        }
    }

    // Private Methods & Event Handlers

    /**
     * Fetches the prototype version history from the database via the controller
     * and refreshes the TableView.
     */
    private void muatRiwayatTabel() {
        if (currentIde == null) return;
        List<Prototipe> riwayat = controller.getRiwayatPrototipe(currentIde.getIdIde());
        tbl_prototipe.getItems().setAll(riwayat);
    }

    /**
     * Validates input, constructs a new {@link Prototipe} instance, and delegates
     * persistence. Shows success or error feedback and refreshes the table upon success.
     */
    @FXML
    private void handleSimpan() {
        clearMessages();

        String versi = txtVersi.getText();
        String status = choiceStatus.getValue();
        String deskripsi = txt_deskripsi_perubahan.getText();

        if (versi == null || versi.isBlank()) {
            tampilkanPesanError("Versi wajib diisi!");
            return;
        }

        Prototipe prototipeBaru = new Prototipe(
                0,
                currentIde.getIdIde(),
                versi,
                status != null ? status : "OnGoing",
                deskripsi,
                LocalDate.now()
        );

        boolean sukses = controller.manageVersi(prototipeBaru);

        if (sukses) {
            tampilkanPesanSukses();
            muatRiwayatTabel();

            txtVersi.clear();
            txt_deskripsi_perubahan.clear();
            choiceStatus.getSelectionModel().selectFirst();
        } else {
            tampilkanPesanError("Gagal menyimpan versi ke database.");
        }
    }

    /**
     * Cancels the operation and navigates back to the project detail view.
     * (Navigation is temporarily disabled for local testing).
     */
    @FXML
    private void handleBatal() {
        /* try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dedi/view/detail_proyek.fxml"));
            Parent root = loader.load();
            DetailProyekView view = loader.getController();
            view.loadProyek(currentIde.getIdIde());
            Main.switchRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Masih di comment soalnya DetailProyekView masih di branch azqi*/

        System.out.println("Tombol batal dipencet! (Navigasi dimatikan sementara)");
    }

    /**
     * Clears all visible error or success feedback messages from the UI.
     */
    private void clearMessages() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        successLabel.setVisible(false);
        successLabel.setManaged(false);
    }

    /**
     * Displays an error message banner on the form.
     *
     * @param pesan the error text to display.
     */
    private void tampilkanPesanError(String pesan) {
        errorLabel.setText(pesan);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    /**
     * Displays a success banner and triggers a pause transition to hide it
     * automatically after 2 seconds.
     */
    private void tampilkanPesanSukses() {
        successLabel.setText("Versi prototipe berhasil ditambahkan.");
        successLabel.setVisible(true);
        successLabel.setManaged(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> clearMessages());
        pause.play();
    }
}