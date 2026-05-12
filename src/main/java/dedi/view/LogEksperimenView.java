package dedi.view;

import dedi.model.LogEksperimen;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class LogEksperimenView extends VBox {
    public TextArea txtTujuan = new TextArea();
    public TextArea txtHasil = new TextArea();
    public TextArea txtKesimpulan = new TextArea();
    public TextField txtPath = new TextField();
    public Button btnSimpan = new Button("Simpan Log");
    public Button btnHapus = new Button("Hapus Terpilih");
    public TableView<LogEksperimen> table = new TableView<>();

    public LogEksperimenView() {
        setSpacing(15);
        setPadding(new Insets(20));

        Label title = new Label("Kelola Log Eksperimen");
        title.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        
        txtTujuan.setPrefHeight(60);
        txtHasil.setPrefHeight(60);
        txtKesimpulan.setPrefHeight(60);

        grid.add(new Label("Tujuan:"), 0, 0); grid.add(txtTujuan, 1, 0);
        grid.add(new Label("Hasil:"), 0, 1); grid.add(txtHasil, 1, 1);
        grid.add(new Label("Kesimpulan:"), 0, 2); grid.add(txtKesimpulan, 1, 2);
        grid.add(new Label("Path Lampiran:"), 0, 3); grid.add(txtPath, 1, 3);

        HBox buttons = new HBox(10, btnSimpan, btnHapus);

        // Definisi Kolom Tabel
        TableColumn<LogEksperimen, LocalDate> colTgl = new TableColumn<>("Tanggal");
        TableColumn<LogEksperimen, String> colTujuan = new TableColumn<>("Tujuan");
        table.getColumns().addAll(colTgl, colTujuan);

        getChildren().addAll(title, grid, buttons, table);
    }
}