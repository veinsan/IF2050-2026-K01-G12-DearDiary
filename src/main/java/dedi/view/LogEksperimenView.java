package dedi.view;

import dedi.model.LogEksperimen;
import javafx.geometry.Insets;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.File;
import java.time.LocalDate;

public class LogEksperimenView extends VBox {
    public TextArea txtTujuan = new TextArea();
    public TextArea txtHasil = new TextArea();
    public TextArea txtKesimpulan = new TextArea();
    public TextArea txtDetail = new TextArea();
    public TextField txtPath = new TextField();
    public Button btnPilihGambar = new Button("Pilih Gambar");
    public Button btnKembali = new Button("Kembali");
    public Button btnSimpan = new Button("Simpan Log");
    public Button btnHapus = new Button("Hapus Terpilih");
    public Label feedbackLabel = new Label();
    public ImageView imgPreview = new ImageView();
    public TableView<LogEksperimen> table = new TableView<>();

    public LogEksperimenView() {
        setSpacing(15);
        setPadding(new Insets(20));

        Label title = new Label("Kelola Log Eksperimen");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
        title.getStyleClass().add("page-title");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        
        txtTujuan.setPrefHeight(60);
        txtHasil.setPrefHeight(60);
        txtKesimpulan.setPrefHeight(60);
        txtDetail.setPrefHeight(60);
        txtPath.setEditable(false);
        imgPreview.setFitWidth(140);
        imgPreview.setFitHeight(90);
        imgPreview.setPreserveRatio(true);

        txtTujuan.getStyleClass().add("form-field");
        txtHasil.getStyleClass().add("form-field");
        txtKesimpulan.getStyleClass().add("form-field");
        txtDetail.getStyleClass().add("form-field");
        txtPath.getStyleClass().add("form-field");
        btnPilihGambar.getStyleClass().add("secondary-button");
        btnKembali.getStyleClass().add("secondary-button");
        btnSimpan.getStyleClass().add("primary-button");
        btnHapus.getStyleClass().add("danger-button");
        table.getStyleClass().add("ide-table");
        feedbackLabel.getStyleClass().add("error-label");
        feedbackLabel.setVisible(false);
        feedbackLabel.setManaged(false);

        grid.add(new Label("Tujuan:"), 0, 0); grid.add(txtTujuan, 1, 0);
        grid.add(new Label("Hasil:"), 0, 1); grid.add(txtHasil, 1, 1);
        grid.add(new Label("Kesimpulan:"), 0, 2); grid.add(txtKesimpulan, 1, 2);
        grid.add(new Label("Detail:"), 0, 3); grid.add(txtDetail, 1, 3);
        grid.add(new Label("Path Lampiran:"), 0, 4);
        grid.add(new HBox(10, txtPath, btnPilihGambar), 1, 4);
        grid.add(new Label("Preview:"), 0, 5); grid.add(imgPreview, 1, 5);

        HBox buttons = new HBox(10, btnKembali, btnSimpan, btnHapus);

        // Definisi Kolom Tabel
        TableColumn<LogEksperimen, LocalDate> colTgl = new TableColumn<>("Tanggal");
        TableColumn<LogEksperimen, String> colTujuan = new TableColumn<>("Tujuan");
        TableColumn<LogEksperimen, String> colHasil = new TableColumn<>("Hasil");
        TableColumn<LogEksperimen, String> colKesimpulan = new TableColumn<>("Kesimpulan");
        TableColumn<LogEksperimen, String> colDetail = new TableColumn<>("Detail");
        TableColumn<LogEksperimen, String> colLampiran = new TableColumn<>("Lampiran");
        colTgl.setPrefWidth(140);
        colTujuan.setPrefWidth(180);
        colHasil.setPrefWidth(220);
        colKesimpulan.setPrefWidth(220);
        colDetail.setPrefWidth(240);
        colLampiran.setPrefWidth(260);
        colLampiran.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getPathLampiran()));
        colLampiran.setCellFactory(column -> new TableCell<>() {
            private final ImageView thumbnail = new ImageView();

            {
                thumbnail.setFitWidth(86);
                thumbnail.setFitHeight(58);
                thumbnail.setPreserveRatio(true);
                thumbnail.setSmooth(true);
                thumbnail.setOnMouseClicked(event -> {
                    String path = getItem();
                    if (path != null && !path.isBlank()) {
                        showLampiranDetail(path);
                    }
                    event.consume();
                });
            }

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.isBlank()) {
                    setText("-");
                    setGraphic(null);
                    return;
                }

                File file = new File(path);
                if (!file.exists()) {
                    setText("Gambar tidak ditemukan");
                    setGraphic(null);
                    return;
                }

                thumbnail.setImage(new Image(file.toURI().toString(), true));
                setText(null);
                setGraphic(thumbnail);
                setStyle("-fx-cursor: hand;");
            }
        });
        table.getColumns().add(colTgl);
        table.getColumns().add(colTujuan);
        table.getColumns().add(colHasil);
        table.getColumns().add(colKesimpulan);
        table.getColumns().add(colDetail);
        table.getColumns().add(colLampiran);

        getChildren().addAll(title, grid, feedbackLabel, buttons, table);
    }

    private void showLampiranDetail(String path) {
        File file = new File(path);
        if (!file.exists()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Lampiran Tidak Ditemukan");
            alert.setHeaderText(null);
            alert.setContentText("File lampiran tidak ditemukan:\n" + path);
            alert.showAndWait();
            return;
        }

        ImageView fullImage = new ImageView(new Image(file.toURI().toString()));
        fullImage.setPreserveRatio(true);
        fullImage.setFitWidth(720);
        fullImage.setFitHeight(520);

        Label fileName = new Label(file.getName());
        fileName.getStyleClass().add("field-value-readonly");
        VBox content = new VBox(12, fullImage, fileName);
        content.setPadding(new Insets(12));

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detail Lampiran");
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }
}
