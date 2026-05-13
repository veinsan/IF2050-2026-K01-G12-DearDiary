package dedi.view;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import dedi.Main;
import dedi.controller.LoginController;
import dedi.controller.LaporanController;
import dedi.controller.PencarianController;
import dedi.controller.PenyaringanController;
import dedi.model.IdeInovasi;
import dedi.model.LaporanPDF;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.File;

public class DasborView implements Initializable {

    @FXML private Label                          usernameLabel;
    @FXML private Label                          roleLabel;
    @FXML private TableView<IdeInovasi>          daftarIdeListView;
    @FXML private TableColumn<IdeInovasi, String> kodeCol;
    @FXML private TableColumn<IdeInovasi, String> judulCol;
    @FXML private TableColumn<IdeInovasi, String> penulisCol;
    @FXML private TableColumn<IdeInovasi, String> statusCol;
    @FXML private TableColumn<IdeInovasi, String> prioritasCol;
    @FXML private TextField                      searchField;
    @FXML private ComboBox<String>               filterStatusCombo;
    @FXML private Button                         tambahIdeButton;

    private final PencarianController pencarianController = new PencarianController();
    private final PenyaringanController penyaringanController = new PenyaringanController();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (LoginController.currentUser != null) {
            usernameLabel.setText(LoginController.currentUser.getUsername());
            roleLabel.setText("(" + LoginController.currentUser.getRole() + ")");
        }

        boolean isResearcher = LoginController.currentUser != null
            && "Researcher".equals(LoginController.currentUser.getRole());
        tambahIdeButton.setVisible(isResearcher);
        tambahIdeButton.setManaged(isResearcher);

        daftarIdeListView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        kodeCol.setCellValueFactory(d ->
            new SimpleStringProperty(nvl(d.getValue().getKodeInovasi())));
        judulCol.setCellValueFactory(d ->
            new SimpleStringProperty(nvl(d.getValue().getJudul())));
        penulisCol.setCellValueFactory(d ->
            new SimpleStringProperty(nvl(d.getValue().getPenulis())));
        statusCol.setCellValueFactory(d ->
            new SimpleStringProperty(nvl(d.getValue().getStatus())));
        prioritasCol.setCellValueFactory(d ->
            new SimpleStringProperty(nvl(d.getValue().getPrioritas())));

        daftarIdeListView.setRowFactory(tv -> {
            TableRow<IdeInovasi> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    navigateToDetail(row.getItem().getIdIde());
                }
            });
            return row;
        });

        filterStatusCombo.getItems().addAll("Semua", "ToDo", "OnGoing", "Done");
        filterStatusCombo.getSelectionModel().selectFirst();

        loadDaftarIde();
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        List<IdeInovasi> results = pencarianController.cariIde(keyword);
        daftarIdeListView.getItems().setAll(results);
    }

    @FXML
    private void handleFilter() {
        String status = filterStatusCombo.getValue();
        if ("Semua".equals(status)) {
            status = null;
        }
        List<IdeInovasi> results = penyaringanController.filterIde(null, status);
        daftarIdeListView.getItems().setAll(results);
    }

    @FXML
    private void handleResetFilter() {
        searchField.clear();
        filterStatusCombo.getSelectionModel().selectFirst();
        loadDaftarIde();
    }

    @FXML
    private void handleExportPdf() {
        if (daftarIdeListView.getItems().isEmpty()) {
            showAlert("Peringatan", "Tidak ada data untuk diekstrak.");
            return;
        }

        IdeInovasi selected = daftarIdeListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Peringatan", "Pilih satu ide inovasi pada tabel terlebih dahulu.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Simpan Laporan PDF");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("Laporan_" + selected.getKodeInovasi() + ".pdf");

        File file = fileChooser.showSaveDialog(daftarIdeListView.getScene().getWindow());
        if (file == null) {
            return;
        }

        try {
            LaporanPDF laporan = new LaporanController().export(selected.getIdIde(), file);
            showAlert("Sukses", "PDF berhasil disimpan di:\n" + laporan.getFullPath());
        } catch (Exception e) {
            showAlert("Gagal", e.getMessage());
        }
    }

    @FXML
    private void handleTambahIde() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/dedi/view/form_ide_inovasi.fxml"));
            Parent root = loader.load();
            FormIdeInovasiView formView = loader.getController();
            formView.setMode(FormIdeInovasiView.Mode.TAMBAH, null);
            Main.switchRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        LoginController.currentUser = null;
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/dedi/view/login.fxml"));
            Main.switchRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    private void loadDaftarIde() {
        daftarIdeListView.getItems().setAll(pencarianController.muatSemuaIde());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static String nvl(String s) {
        return (s != null && !s.isBlank()) ? s : "-";
    }
}
