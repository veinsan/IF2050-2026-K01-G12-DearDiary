-- Drop existing tables in dependency order to allow clean re-initialization.
DROP TABLE IF EXISTS prototipe        CASCADE;
DROP TABLE IF EXISTS log_eksperimen   CASCADE;
DROP TABLE IF EXISTS laporan_pdf      CASCADE;
DROP TABLE IF EXISTS ide_inovasi      CASCADE;
DROP TABLE IF EXISTS pengguna         CASCADE;

-- System user accounts with role-based access control.
CREATE TABLE pengguna (
    username    VARCHAR(50)  PRIMARY KEY,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL
                    CHECK (role IN ('Researcher', 'Tim R&D'))
);

COMMENT ON TABLE  pengguna      IS 'System user accounts for the DeDi application';
COMMENT ON COLUMN pengguna.password IS 'SHA-256 password digest in sha256$hex format';
COMMENT ON COLUMN pengguna.role     IS 'Access role: Researcher (full CRUD) or Tim R&D (read-only)';

-- Core entity representing an innovation project.
CREATE TABLE ide_inovasi (
    id_ide              SERIAL       PRIMARY KEY,
    kode_inovasi        VARCHAR(30)  NOT NULL UNIQUE,
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

COMMENT ON TABLE  ide_inovasi              IS 'Innovation projects (UC02-UC05)';
COMMENT ON COLUMN ide_inovasi.kode_inovasi IS 'System-generated unique code, immutable after creation';
COMMENT ON COLUMN ide_inovasi.status       IS 'Lifecycle stage: ToDo, OnGoing, or Done';
COMMENT ON COLUMN ide_inovasi.prioritas    IS 'Priority level: Rendah, Sedang, or Tinggi';

-- Experiment activity log linked to an innovation project.
CREATE TABLE log_eksperimen (
    id_log              SERIAL       PRIMARY KEY,
    id_ide              INT          NOT NULL
                            REFERENCES ide_inovasi(id_ide) ON DELETE CASCADE,
    tanggal             DATE         NOT NULL DEFAULT CURRENT_DATE,
    tujuan              TEXT,
    hasil               TEXT,
    kesimpulan          TEXT,
    detail_eksperimen   TEXT,
    path_lampiran       VARCHAR(500)
);

COMMENT ON TABLE  log_eksperimen               IS 'Experiment logs per innovation project (UC06)';
COMMENT ON COLUMN log_eksperimen.path_lampiran IS 'Local filesystem path to image attachment (.png/.jpg, max 5 MB)';

-- Append-only version history for prototypes; rollback is not supported.
CREATE TABLE prototipe (
    id_prototipe        SERIAL       PRIMARY KEY,
    id_ide              INT          NOT NULL
                            REFERENCES ide_inovasi(id_ide) ON DELETE CASCADE,
    versi               VARCHAR(20)  NOT NULL,
    status              VARCHAR(50),
    deskripsi_perubahan TEXT,
    tanggal_perubahan   DATE         NOT NULL DEFAULT CURRENT_DATE
);

COMMENT ON TABLE  prototipe       IS 'Append-only prototype version history per innovation project (UC07)';
COMMENT ON COLUMN prototipe.versi IS 'Semantic version label, e.g. v1.0, v1.1';

-- Generated PDF report metadata (UC10).
CREATE TABLE laporan_pdf (
    id_laporan          SERIAL       PRIMARY KEY,
    id_ide              INT          NOT NULL
                            REFERENCES ide_inovasi(id_ide) ON DELETE CASCADE,
    nama_file           VARCHAR(255) NOT NULL,
    lokasi_penyimpanan  VARCHAR(500) NOT NULL,
    tanggal_generate    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE laporan_pdf IS 'Metadata for exported PDF reports';

-- Indexes on frequently filtered and joined columns.
CREATE INDEX idx_ide_status   ON ide_inovasi (status);
CREATE INDEX idx_ide_kategori ON ide_inovasi (kategori);
CREATE INDEX idx_log_id_ide   ON log_eksperimen (id_ide);
CREATE INDEX idx_proto_id_ide ON prototipe (id_ide);
CREATE INDEX idx_laporan_id_ide ON laporan_pdf (id_ide);

-- Seed data for development and testing.
INSERT INTO pengguna (username, password, role) VALUES
    ('researcher1', 'sha256$ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Researcher'),
    ('guest1',      'sha256$ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f', 'Tim R&D');
