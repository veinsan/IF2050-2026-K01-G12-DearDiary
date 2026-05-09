-- Hapus tabel lama jika sudah ada. Urutan mengikuti ketergantungan foreign key.
DROP TABLE IF EXISTS prototipe        CASCADE;
DROP TABLE IF EXISTS log_eksperimen   CASCADE;
DROP TABLE IF EXISTS ide_inovasi      CASCADE;
DROP TABLE IF EXISTS pengguna         CASCADE;

-- Tabel akun pengguna sistem.
CREATE TABLE pengguna (
    username    VARCHAR(50)  PRIMARY KEY,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL
                    CHECK (role IN ('Researcher', 'Tim R&D'))
);

COMMENT ON TABLE  pengguna          IS 'Akun pengguna sistem DeDi (Researcher dan Tim R&D)';
COMMENT ON COLUMN pengguna.username IS 'Identifier unik milik akun pengguna';
COMMENT ON COLUMN pengguna.password IS 'Kata sandi akun pengguna';
COMMENT ON COLUMN pengguna.role     IS 'Peran pengguna: Researcher (CRUD) atau Tim R&D (view-only)';

-- Tabel data utama ide inovasi.
CREATE TABLE ide_inovasi (
    id_ide              SERIAL       PRIMARY KEY,
    kode_inovasi        VARCHAR(30)  NOT NULL UNIQUE,   -- Digenerate otomatis saat insert, lalu read-only
    judul               VARCHAR(255) NOT NULL,
    penulis             VARCHAR(100) NOT NULL,
    kategori            VARCHAR(100) NOT NULL,
    deskripsi           TEXT,
    tanggal_dibuat      DATE         NOT NULL DEFAULT CURRENT_DATE,
    status              VARCHAR(20)  NOT NULL DEFAULT 'ToDo'
                            CHECK (status IN ('ToDo', 'OnGoing', 'Done')),
    penanggung_jawab    VARCHAR(100),
    prioritas           VARCHAR(20)
                            CHECK (prioritas IN ('Rendah', 'Sedang', 'Tinggi'))
);

COMMENT ON TABLE  ide_inovasi                  IS 'Data utama proyek inovasi (UC02, UC03, UC04, UC05)';
COMMENT ON COLUMN ide_inovasi.id_ide           IS 'Primary key auto-increment';
COMMENT ON COLUMN ide_inovasi.kode_inovasi     IS 'Kode unik yang digenerate otomatis sistem dan bersifat read-only setelah dibuat';
COMMENT ON COLUMN ide_inovasi.judul            IS 'Judul ide inovasi';
COMMENT ON COLUMN ide_inovasi.penulis          IS 'Nama pencetus ide inovasi';
COMMENT ON COLUMN ide_inovasi.kategori         IS 'Kategori proyek inovasi';
COMMENT ON COLUMN ide_inovasi.deskripsi        IS 'Deskripsi rinci ide inovasi';
COMMENT ON COLUMN ide_inovasi.tanggal_dibuat   IS 'Tanggal pembuatan yang digenerate otomatis oleh sistem';
COMMENT ON COLUMN ide_inovasi.status           IS 'Status siklus hidup: ToDo, OnGoing, atau Done';
COMMENT ON COLUMN ide_inovasi.penanggung_jawab IS 'Nama penanggung jawab proyek';
COMMENT ON COLUMN ide_inovasi.prioritas        IS 'Tingkat prioritas: Rendah, Sedang, atau Tinggi';

-- Tabel log eksperimen per ide inovasi.
CREATE TABLE log_eksperimen (
    id_log              SERIAL       PRIMARY KEY,
    id_ide              INT          NOT NULL
                            REFERENCES ide_inovasi(id_ide) ON DELETE CASCADE,
    tanggal             DATE         NOT NULL DEFAULT CURRENT_DATE,
    tujuan              TEXT,
    hasil               TEXT,
    kesimpulan          TEXT,
    detail_eksperimen   TEXT,
    path_lampiran       VARCHAR(500)          -- Path lokal file gambar (.png/.jpg, maks. 5 MB)
);

COMMENT ON TABLE  log_eksperimen                   IS 'Log aktivitas eksperimen per ide inovasi (UC06)';
COMMENT ON COLUMN log_eksperimen.id_log            IS 'Primary key auto-increment';
COMMENT ON COLUMN log_eksperimen.id_ide            IS 'Foreign key ke ide_inovasi';
COMMENT ON COLUMN log_eksperimen.tanggal           IS 'Tanggal pelaksanaan eksperimen';
COMMENT ON COLUMN log_eksperimen.tujuan            IS 'Tujuan eksperimen';
COMMENT ON COLUMN log_eksperimen.hasil             IS 'Hasil eksperimen';
COMMENT ON COLUMN log_eksperimen.kesimpulan        IS 'Kesimpulan dari eksperimen';
COMMENT ON COLUMN log_eksperimen.detail_eksperimen IS 'Catatan teknis tambahan';
COMMENT ON COLUMN log_eksperimen.path_lampiran     IS 'Path absolut atau relatif file gambar di sistem berkas lokal (maks. 5 MB)';

-- Tabel riwayat versi prototipe.
CREATE TABLE prototipe (
    id_prototipe        SERIAL       PRIMARY KEY,
    id_ide              INT          NOT NULL
                            REFERENCES ide_inovasi(id_ide) ON DELETE CASCADE,
    versi               VARCHAR(20)  NOT NULL,           -- Contoh: v1.0, v1.1
    status              VARCHAR(50),
    deskripsi_perubahan TEXT,
    tanggal_perubahan   DATE         NOT NULL DEFAULT CURRENT_DATE
);

COMMENT ON TABLE  prototipe                        IS 'Riwayat versi prototipe per ide inovasi (UC07), bersifat historis tanpa rollback';
COMMENT ON COLUMN prototipe.id_prototipe           IS 'Primary key auto-increment';
COMMENT ON COLUMN prototipe.id_ide                 IS 'Foreign key ke ide_inovasi';
COMMENT ON COLUMN prototipe.versi                  IS 'Nomor versi prototipe, misalnya v1.0 atau v1.1';
COMMENT ON COLUMN prototipe.status                 IS 'Status progres prototipe pada versi ini';
COMMENT ON COLUMN prototipe.deskripsi_perubahan    IS 'Deskripsi perubahan yang dilakukan pada versi ini';
COMMENT ON COLUMN prototipe.tanggal_perubahan      IS 'Tanggal pencatatan versi';

-- Index untuk mempercepat pencarian dan penyaringan data.
CREATE INDEX idx_ide_status   ON ide_inovasi (status);
CREATE INDEX idx_ide_kategori ON ide_inovasi (kategori);
CREATE INDEX idx_log_id_ide   ON log_eksperimen (id_ide);
CREATE INDEX idx_proto_id_ide ON prototipe (id_ide);

-- Data awal untuk pengujian.
INSERT INTO pengguna (username, password, role) VALUES
    ('researcher1', 'password123', 'Researcher'),
    ('guest1',      'password123', 'Tim R&D');