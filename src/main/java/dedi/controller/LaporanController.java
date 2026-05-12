package dedi.controller;

import dedi.database.IdeInovasiDatabase;
import dedi.database.DatabaseConnection;
import dedi.database.LogEksperimenDatabase;
import dedi.database.PrototipeDatabase;
import dedi.model.IdeInovasi;
import dedi.model.LogEksperimen;
import dedi.model.LaporanPDF;
import dedi.model.Prototipe;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Controller untuk UC10: Mengekspor Laporan PDF.
 * <p>
 * Design:
 * 1. SQL di-delegate ke database layer.
 * 2. Data fetching dan PDF generation dipisah.
 * 3. Null-safe: semua field dicek sebelum dimasukkan ke PDF.
 * <p>
 * <b>Catatan:</b> Menggunakan iText 5 API (com.itextpdf.text.*).
 */
public class LaporanController {

    private final IdeInovasiDatabase ideDb;
    private final LogEksperimenDatabase logDb;
    private final PrototipeDatabase prototipeDb;

    private ReportData cachedData;

    /** Constructor default untuk production. */
    public LaporanController() {
        this(new IdeInovasiDatabase(), new LogEksperimenDatabase(), new PrototipeDatabase());
    }

    /** Constructor dengan injection — untuk unit testing. */
    public LaporanController(IdeInovasiDatabase ideDb,
                           LogEksperimenDatabase logDb,
                           PrototipeDatabase prototipeDb) {
        this.ideDb = Objects.requireNonNull(ideDb);
        this.logDb = Objects.requireNonNull(logDb);
        this.prototipeDb = Objects.requireNonNull(prototipeDb);
    }

    // ==================== PUBLIC API ====================

    /**
     * Export ide inovasi dengan ID tertentu ke file PDF.
     *
     * @param idIde      ID ide inovasi
     * @param targetFile file tujuan
     * @return metadata LaporanPDF
     */
    public LaporanPDF export(int idIde, File targetFile) {
        Objects.requireNonNull(targetFile, "targetFile tidak boleh null");

        this.cachedData = fetchData(idIde);
        if (cachedData.ide == null) {
            throw new IllegalStateException("Ide inovasi dengan ID '" + idIde + "' tidak ditemukan.");
        }
        validateExtractableData(cachedData);

        File parent = targetFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        String dest = targetFile.getAbsolutePath();
        if (!dest.toLowerCase().endsWith(".pdf")) {
            dest += ".pdf";
        }

        try {
            buildPdfDocument(cachedData, dest);
        } catch (IOException e) {
            throw new IllegalStateException("Gagal menulis PDF: " + e.getMessage(), e);
        }

        LaporanPDF laporan = new LaporanPDF(
            new File(dest).getName(),
            parent != null ? parent.getAbsolutePath() : "",
            LocalDateTime.now()
        );
        simpanMetadata(idIde, laporan);
        return laporan;
    }

    /**
     * Export dengan nama file otomatis: Laporan_[kode]_[timestamp].pdf
     */
    public LaporanPDF exportWithDefaultName(int idIde, File directory) {
        Objects.requireNonNull(directory, "directory tidak boleh null");
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Harus direktori, bukan file.");
        }

        this.cachedData = fetchData(idIde);
        if (cachedData.ide == null) {
            throw new IllegalStateException("Ide tidak ditemukan.");
        }
        validateExtractableData(cachedData);

        String kode = cachedData.ide.getKodeInovasi();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = String.format("Laporan_%s_%s.pdf", sanitizeFileName(kode), timestamp);

        File target = new File(directory, fileName);

        try {
            buildPdfDocument(cachedData, target.getAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException("Gagal menulis PDF: " + e.getMessage(), e);
        }

        LaporanPDF laporan = new LaporanPDF(target.getName(), directory.getAbsolutePath(), LocalDateTime.now());
        simpanMetadata(idIde, laporan);
        return laporan;
    }

    public ReportData getCachedData() {
        return cachedData;
    }

    // ==================== DATA FETCHING ====================

    private ReportData fetchData(int idIde) {
        IdeInovasi ide = ideDb.getById(idIde);
        List<LogEksperimen> logs = logDb.getLogByIdeId(idIde);
        List<Prototipe> protos = prototipeDb.getPrototipeByIdeId(idIde);

        return new ReportData(
            ide,
            logs != null ? logs : Collections.emptyList(),
            protos != null ? protos : Collections.emptyList()
        );
    }

    private void validateExtractableData(ReportData data) {
        if ((data.logs == null || data.logs.isEmpty())
            && (data.prototypes == null || data.prototypes.isEmpty())) {
            throw new IllegalStateException("Tidak ada data untuk diekstrak.");
        }
    }

    private void simpanMetadata(int idIde, LaporanPDF laporan) {
        String sql = "INSERT INTO laporan_pdf (id_ide, nama_file, lokasi_penyimpanan, tanggal_generate) "
            + "VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getInstance();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idIde);
                ps.setString(2, laporan.getNamaFile());
                ps.setString(3, laporan.getLokasiPenyimpanan());
                ps.setTimestamp(4, Timestamp.valueOf(laporan.getTanggalGenerate()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[LaporanController] metadata laporan_pdf tidak tersimpan: " + e.getMessage());
        }
    }

    public static class ReportData {
        public final IdeInovasi ide;
        public final List<LogEksperimen> logs;
        public final List<Prototipe> prototypes;

        public ReportData(IdeInovasi ide, List<LogEksperimen> logs, List<Prototipe> prototypes) {
            this.ide = ide;
            this.logs = logs;
            this.prototypes = prototypes;
        }
    }

    // ==================== PDF GENERATION (iText 5) ====================

    private void buildPdfDocument(ReportData data, String destPath) throws IOException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(destPath));
            document.setMargins(60, 50, 60, 50); // left, right, top, bottom
            document.open();

            addHeader(document);
            addIdeSection(document, data.ide);
            addLogSection(document, data.logs);
            addPrototipeSection(document, data.prototypes);
            addFooter(document);
        } catch (DocumentException e) {
            throw new IOException("Gagal membuat PDF: " + e.getMessage(), e);
        } finally {
            document.close();
        }
    }

    private void addHeader(Document doc) throws DocumentException {
        Paragraph title = new Paragraph("LAPORAN DOKUMENTASI RISET",
            new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, new BaseColor(33, 37, 41)));
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);

        Paragraph subtitle = new Paragraph("DeDi (Dear Diary)",
            new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, new BaseColor(108, 117, 125)));
        subtitle.setAlignment(Element.ALIGN_CENTER);
        doc.add(subtitle);

        Paragraph date = new Paragraph("Generated: " + formatNow(),
            new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, new BaseColor(108, 117, 125)));
        date.setAlignment(Element.ALIGN_CENTER);
        doc.add(date);

        doc.add(new Paragraph("\n"));
    }

    private void addIdeSection(Document doc, IdeInovasi ide) throws DocumentException {
        doc.add(createSectionTitle("1. Informasi Ide Inovasi"));

        PdfPTable table = new PdfPTable(new float[]{30, 70});
        table.setWidthPercentage(100);

        addKeyValueRow(table, "Kode Inovasi", ide.getKodeInovasi());
        addKeyValueRow(table, "Judul", ide.getJudul());
        addKeyValueRow(table, "Penulis", ide.getPenulis());
        addKeyValueRow(table, "Kategori", ide.getKategori());
        addKeyValueRow(table, "Status", ide.getStatus());
        addKeyValueRow(table, "Prioritas", ide.getPrioritas());
        addKeyValueRow(table, "Tanggal Dibuat", formatDate(ide.getTanggalDibuat()));
        addKeyValueRow(table, "Penanggung Jawab", ide.getPenanggungJawab());
        addKeyValueRow(table, "Deskripsi", ide.getDeskripsi());

        doc.add(table);
        doc.add(new Paragraph("\n"));
    }

    private void addLogSection(Document doc, List<LogEksperimen> logs) throws DocumentException {
        doc.add(createSectionTitle("2. Log Eksperimen"));

        if (logs.isEmpty()) {
            Paragraph empty = new Paragraph("Belum ada log eksperimen.",
                new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY));
            doc.add(empty);
            doc.add(new Paragraph("\n"));
            return;
        }

        PdfPTable table = new PdfPTable(new float[]{12, 22, 22, 22, 22});
        table.setWidthPercentage(100);

        String[] headers = {"Tanggal", "Tujuan", "Hasil", "Kesimpulan", "Lampiran"};
        for (String h : headers) {
            table.addCell(createHeaderCell(h));
        }
        table.setHeaderRows(1);

        for (LogEksperimen log : logs) {
            table.addCell(createCell(formatDate(log.getTanggal())));
            table.addCell(createCell(log.getTujuan()));
            table.addCell(createCell(log.getHasil()));
            table.addCell(createCell(log.getKesimpulan()));
            table.addCell(createCell(orDefault(log.getPathLampiran())));
        }

        doc.add(table);
        doc.add(new Paragraph("\n"));
    }

    private void addPrototipeSection(Document doc, List<Prototipe> prototypes) throws DocumentException {
        doc.add(createSectionTitle("3. Riwayat Prototipe"));

        if (prototypes.isEmpty()) {
            Paragraph empty = new Paragraph("Belum ada riwayat prototipe.",
                new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.GRAY));
            doc.add(empty);
            doc.add(new Paragraph("\n"));
            return;
        }

        PdfPTable table = new PdfPTable(new float[]{12, 15, 18, 55});
        table.setWidthPercentage(100);

        String[] headers = {"Versi", "Status", "Tanggal", "Deskripsi Perubahan"};
        for (String h : headers) {
            table.addCell(createHeaderCell(h));
        }
        table.setHeaderRows(1);

        for (Prototipe p : prototypes) {
            table.addCell(createCell(p.getVersi()));
            table.addCell(createCell(p.getStatus()));
            table.addCell(createCell(formatDate(p.getTanggalPerubahan())));
            table.addCell(createCell(p.getDeskripsiPerubahan()));
        }

        doc.add(table);
        doc.add(new Paragraph("\n"));
    }

    private void addFooter(Document doc) throws DocumentException {
        Paragraph p = new Paragraph("— Akhir Laporan —",
            new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, new BaseColor(150, 150, 150)));
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
    }

    // ==================== HELPERS ====================

    private Paragraph createSectionTitle(String text) {
        Paragraph p = new Paragraph(text,
            new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(33, 37, 41)));
        p.setSpacingAfter(6f);
        return p;
    }

    private void addKeyValueRow(PdfPTable table, String label, String value) {
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new BaseColor(248, 249, 250));

        PdfPCell valueCell = new PdfPCell(new Phrase(orDefault(value)));

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private PdfPCell createHeaderCell(String text) {
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new BaseColor(33, 37, 41));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell createCell(String text) {
        return new PdfPCell(new Phrase(orDefault(text)));
    }

    private String orDefault(String text) {
        return (text == null || text.trim().isEmpty()) ? "-" : text.trim();
    }

    private String formatNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss"));
    }

    private String formatDate(java.time.LocalDate date) {
        if (date == null) return "-";
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    private String sanitizeFileName(String input) {
        if (input == null) return "unknown";
        return input.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }
}
