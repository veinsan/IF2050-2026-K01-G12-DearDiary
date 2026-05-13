# DearDiary (DeDi)

Aplikasi desktop berbasis JavaFX untuk mendokumentasikan proyek inovasi internal: dari pengelolaan ide inovasi, pencatatan log eksperimen, riwayat versi prototipe, hingga ekspor laporan ke PDF. Seluruh data disimpan secara lokal pada basis data PostgreSQL tanpa ketergantungan layanan jaringan (sesuai KNF01 - *local only*).

Aplikasi ini merupakan tugas besar mata kuliah **IF2050 Pengembangan Perangkat Lunak (2026, Kelas K01, Kelompok G12)**.

## 1. Prasyarat dan Instalasi

| Komponen        | Versi                          |
| --------------- | ------------------------------ |
| JDK             | Java 21 (OpenJDK 21)           |
| JavaFX          | 21 (terpasang otomatis via Maven) |
| Maven           | 3.8 atau lebih baru            |
| PostgreSQL      | 13 atau lebih baru (lokal)     |

Instalasi:

1. **Pasang Java 21 dan Maven.** Pastikan `java -version` dan `mvn -version` keduanya tersedia di terminal.
2. **Pasang PostgreSQL lokal** dan jalankan service-nya pada port standar `5432`. Akun yang digunakan adalah `postgres` dengan password `postgres` (lihat `dedi.database.DatabaseConnection`).
3. **Clone repository** dan masuk ke root proyek:

   ```bash
   git clone https://github.com/veinsan/IF2050-2026-K01-G12-DearDiary
   cd DearDiary
   ```

## 2. Cara Menjalankan Aplikasi

### Setup basis data awal

Buat basis data `deardiary` terlebih dahulu (PostgreSQL **tidak** akan membuatnya secara otomatis):

```bash
psql -U postgres -c "CREATE DATABASE deardiary;"
```

Setelah itu Anda punya dua pilihan:

- **Opsi A (otomatis).** Cukup jalankan aplikasi. Saat koneksi JDBC pertama, `DatabaseConnection.ensureSchema()` akan mendeteksi bahwa tabel `pengguna` belum ada lalu mengeksekusi `sql/schema.sql` dari root proyek.
- **Opsi B (manual).** Jalankan skema sendiri:

  ```bash
  psql -U postgres -d deardiary -f sql/schema.sql
  ```

> **Penting:** selalu jalankan Maven dari root proyek. Auto-bootstrap menyelesaikan path `sql/schema.sql` melalui `System.getProperty("user.dir")` - menjalankan dari direktori lain akan melewati pembuatan skema secara diam-diam.

### Menjalankan aplikasi

```bash
mvn javafx:run
```

Aplikasi akan terbuka pada jendela 880×560 dengan halaman login. Gunakan kredensial bawaan dari `schema.sql`:

| Username      | Password      | Peran        |
| ------------- | ------------- | ------------ |
| `researcher1` | `password123` | Researcher (akses CRUD penuh) |
| `guest1`      | `password123` | Tim R&D (read-only) |

### Perintah Maven lain

```bash
mvn test                          # menjalankan unit test (JUnit 5)
mvn -Dtest=NamaKelas test         # menjalankan satu kelas test
mvn package                       # menghasilkan target/DearDiary.jar
mvn clean                         # membersihkan target/
```

## 3. Daftar Modul yang Diimplementasi

| Nama Modul                     | Deskripsi                                                                                       | Pembagian            |
| ------------------------------ | ----------------------------------------------------------------------------------------------- | -------------------- |
| Login & Otentikasi (UC01)      | Validasi kredensial pengguna terhadap tabel `pengguna`, sesi disimpan pada `LoginController.currentUser`, pemisahan akses berdasarkan peran Researcher / Tim R&D. | Riantama Putra       |
| Dashboard Utama (UC02)         | Daftar ide inovasi dalam tampilan tabel, navigasi dua-klik ke detail proyek, tombol mutasi yang otomatis tersembunyi untuk peran Tim R&D. | Mishael Gilland      |
| CRUD Ide Inovasi (UC03–UC05)   | Penambahan, pengubahan, dan penghapusan ide inovasi. `kode_inovasi` dibangkitkan otomatis (`INV-YYYYMMDD-NNNN`) dan bersifat *immutable*. | Bryant Azraqi Mohammad |
| Log Eksperimen (UC06)          | CRUD log eksperimen yang terkait satu ide inovasi, termasuk lampiran gambar `.png`/`.jpg` (maks. 5 MB). | Irghi Satya Priangga     |
| Riwayat Prototipe (UC07)       | Pencatatan versi prototipe yang bersifat *append-only* - rollback secara sengaja tidak didukung. | Aldyto Rafif         |
| Pencarian Ide Inovasi (UC08)   | Pencarian *substring case-insensitive* atas kolom judul, deskripsi, kategori, kode, dan penulis. | Mishael Gilland      |
| Penyaringan Ide Inovasi (UC09) | Filter berdasarkan kategori dan/atau status (`ToDo`, `OnGoing`, `Done`) secara opsional.        | Mishael Gilland      |
| Ekspor Laporan PDF (UC10)      | Pembuatan laporan PDF berisi detail ide inovasi, log eksperimen, dan riwayat prototipe menggunakan iText 5; metadata laporan dicatat ke tabel `laporan_pdf`. | Herlambang Setiaji Prabowo        |

## 4. Daftar Tabel Basis Data

Skema disusun dalam Bahasa Indonesia (identifier tabel dan kolom dipertahankan apa adanya).

### `pengguna` - akun pengguna sistem

| Kolom      | Tipe           | Keterangan                                                            |
| ---------- | -------------- | --------------------------------------------------------------------- |
| `username` | `VARCHAR(50)`  | Primary key.                                                          |
| `password` | `VARCHAR(255)` | *Digest* SHA-256 dalam format `sha256$<hex>`. Tidak nullable.         |
| `role`    | `VARCHAR(20)`  | `Researcher` (CRUD penuh) atau `Tim R&D` (read-only). CHECK constraint. |

### `ide_inovasi` - proyek inovasi (UC02–UC05)

| Kolom              | Tipe           | Keterangan                                                                                      |
| ------------------ | -------------- | ----------------------------------------------------------------------------------------------- |
| `id_ide`           | `SERIAL`       | Primary key, auto-increment.                                                                    |
| `kode_inovasi`     | `VARCHAR(30)`  | Kode unik yang dibangkitkan sistem (`INV-YYYYMMDD-NNNN`), *immutable*. `NOT NULL UNIQUE`.       |
| `judul`            | `VARCHAR(255)` | Judul ide. `NOT NULL`.                                                                          |
| `penulis`          | `VARCHAR(100)` | Penulis ide. `NOT NULL`.                                                                        |
| `kategori`         | `VARCHAR(100)` | Kategori ide. `NOT NULL`.                                                                       |
| `deskripsi`        | `TEXT`         | Deskripsi panjang, boleh kosong.                                                                |
| `tanggal_dibuat`   | `DATE`         | Default `CURRENT_DATE`. `NOT NULL`.                                                             |
| `status`           | `VARCHAR(20)`  | `ToDo`, `OnGoing`, atau `Done`. Default `ToDo`. CHECK constraint.                               |
| `penanggung_jawab` | `VARCHAR(100)` | Boleh kosong.                                                                                   |
| `prioritas`        | `VARCHAR(20)`  | `Rendah`, `Sedang`, atau `Tinggi`. CHECK constraint.                                            |

Indeks: `idx_ide_status (status)`, `idx_ide_kategori (kategori)`.

### `log_eksperimen` - catatan eksperimen per ide (UC06)

| Kolom               | Tipe           | Keterangan                                                                                  |
| ------------------- | -------------- | ------------------------------------------------------------------------------------------- |
| `id_log`            | `SERIAL`       | Primary key.                                                                                |
| `id_ide`            | `INT`          | Foreign key ke `ide_inovasi(id_ide)` dengan `ON DELETE CASCADE`. `NOT NULL`.                |
| `tanggal`           | `DATE`         | Default `CURRENT_DATE`. `NOT NULL`.                                                         |
| `tujuan`            | `TEXT`         | Tujuan eksperimen.                                                                          |
| `hasil`             | `TEXT`         | Hasil eksperimen.                                                                           |
| `kesimpulan`        | `TEXT`         | Kesimpulan eksperimen.                                                                      |
| `detail_eksperimen` | `TEXT`         | Detail tambahan (ditambahkan via additive migration di `DatabaseConnection`).               |
| `path_lampiran`     | `VARCHAR(500)` | Path lokal ke gambar `.png`/`.jpg` (maks. 5 MB).                                            |

Indeks: `idx_log_id_ide (id_ide)`.

### `prototipe` - riwayat versi prototipe (UC07, *append-only*)

| Kolom                 | Tipe           | Keterangan                                                                                  |
| --------------------- | -------------- | ------------------------------------------------------------------------------------------- |
| `id_prototipe`        | `SERIAL`       | Primary key.                                                                                |
| `id_ide`              | `INT`          | Foreign key ke `ide_inovasi(id_ide)` dengan `ON DELETE CASCADE`. `NOT NULL`.                |
| `versi`               | `VARCHAR(20)`  | Label versi semantik, mis. `v1.0`, `v1.1`. `NOT NULL`.                                      |
| `status`              | `VARCHAR(50)`  | Status prototipe (label bebas).                                                             |
| `deskripsi_perubahan` | `TEXT`         | Catatan perubahan pada versi ini.                                                           |
| `tanggal_perubahan`   | `DATE`         | Default `CURRENT_DATE`. `NOT NULL`.                                                         |

Indeks: `idx_proto_id_ide (id_ide)`. **Catatan:** baris pada tabel ini tidak boleh di-`UPDATE` atau di-`DELETE` selain melalui *cascade* dari `ide_inovasi`.

### `laporan_pdf` - metadata laporan PDF (UC10)

| Kolom                | Tipe           | Keterangan                                                                                  |
| -------------------- | -------------- | ------------------------------------------------------------------------------------------- |
| `id_laporan`         | `SERIAL`       | Primary key.                                                                                |
| `id_ide`             | `INT`          | Foreign key ke `ide_inovasi(id_ide)` dengan `ON DELETE CASCADE`. `NOT NULL`.                |
| `nama_file`          | `VARCHAR(255)` | Nama file PDF yang dihasilkan. `NOT NULL`.                                                  |
| `lokasi_penyimpanan` | `VARCHAR(500)` | Path direktori penyimpanan. `NOT NULL`.                                                     |
| `tanggal_generate`   | `TIMESTAMP`    | Default `CURRENT_TIMESTAMP`. `NOT NULL`.                                                    |

Indeks: `idx_laporan_id_ide (id_ide)`.

## 5. Pembagian Tugas Implementasi

| Nama Anggota             | NIM            | Kelas yang Diimplementasikan                                                                                                                                                            |
| ------------------------ | -------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Riantama Putra           | 18224061    | `dedi.Main`, `dedi.controller.LoginController`, `dedi.model.AuthModel`, `dedi.model.Pengguna`, `login.fxml` & `login.css`, `dashboard.fxml` (skeleton), `dedi.view.DasborView` (skeleton) |
| Bryant Azraqi Mohammad   | 18224067    | `dedi.controller.IdeInovasiController`, `dedi.controller.DetailProyekController`, `dedi.database.IdeInovasiDatabase`, `dedi.model.IdeInovasi`, `dedi.view.FormIdeInovasiView`, `dedi.view.DetailProyekView`, `form_ide_inovasi.fxml`, `detail_proyek.fxml` |
| Mishael Gilland          | 18224005    | `dedi.view.DasborView` (integrasi penuh), `dedi.controller.PencarianController`, `dedi.controller.PenyaringanController`, integrasi pencarian dan filter pada `dashboard.fxml`              |
| Irghi Satya Priangga             | 18224041     | `dedi.controller.LogEksperimenController`, `dedi.database.LogEksperimenDatabase`, `dedi.model.LogEksperimen`, `dedi.view.LogEksperimenView`                                              |
| Aldyto Rafif Abhinaya             | 18224043    | `dedi.controller.PrototipeController`, `dedi.database.PrototipeDatabase`, `dedi.model.Prototipe`, `dedi.view.PrototipeFormView`, `form_prototipe.fxml`                                   |
| Herlambang Setiaji Prabowo            | 18224113    | `dedi.controller.LaporanController`, `dedi.model.LaporanPDF`, integrasi tabel `laporan_pdf` & ekspor PDF iText 5 pada `dedi.view.DasborView`                                              |
