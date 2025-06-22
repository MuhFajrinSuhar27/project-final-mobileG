package com.example.projekkhayalan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "emergency_app.db";
    private static final int DATABASE_VERSION = 5;
    private static final String TAG = "DatabaseHelper";


    public static final String CREATE_TABLE_ADMIN = "CREATE TABLE admin ("
            + "id_admin INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "nama_pengguna TEXT NOT NULL UNIQUE, "
            + "kata_sandi TEXT NOT NULL, "
            + "nama_lengkap TEXT NOT NULL, "
            + "email TEXT NOT NULL UNIQUE, "
            + "nomor_telepon TEXT NOT NULL, "
            + "level_akses TEXT DEFAULT 'admin', "
            + "tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "terakhir_login TIMESTAMP);";


    public static final String CREATE_TABLE_PETUGAS = "CREATE TABLE petugas ("
            + "id_petugas INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "nama_pengguna TEXT NOT NULL UNIQUE, "
            + "kata_sandi TEXT NOT NULL, "
            + "nama_lengkap TEXT NOT NULL, "
            + "nomor_telepon TEXT NOT NULL, "
            + "email TEXT, "
            + "spesialisasi TEXT, "
            + "area_tugas TEXT, "
            + "status TEXT DEFAULT 'tersedia', "
            + "foto_profil TEXT, "
            + "tanggal_daftar TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "terakhir_login TIMESTAMP);";


    public static final String CREATE_TABLE_PANGGILAN_SOS = "CREATE TABLE panggilan_sos ("
            + "id_panggilan INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "latitude REAL NOT NULL, "
            + "longitude REAL NOT NULL, "
            + "alamat_lokasi TEXT, "
            + "waktu_panggilan TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "status_panggilan TEXT DEFAULT 'aktif', "
            + "id_petugas INTEGER);";


    private static final String TABLE_ADMIN = "admin";
    private static final String COLUMN_ADMIN_USERNAME = "nama_pengguna";
    private static final String COLUMN_ADMIN_PASSWORD = "kata_sandi";
    private static final String COLUMN_ADMIN_NAMA = "nama_lengkap";
    private static final String COLUMN_ADMIN_EMAIL = "email";
    private static final String COLUMN_ADMIN_TELEPON = "nomor_telepon";
    private static final String COLUMN_ADMIN_LEVEL = "level_akses";

    private static final String TABLE_PETUGAS = "petugas";
    private static final String COLUMN_PETUGAS_USERNAME = "nama_pengguna";
    private static final String COLUMN_PETUGAS_PASSWORD = "kata_sandi";
    private static final String COLUMN_PETUGAS_NAMA = "nama_lengkap";
    private static final String COLUMN_PETUGAS_TELEPON = "nomor_telepon";
    private static final String COLUMN_PETUGAS_EMAIL = "email";
    private static final String COLUMN_PETUGAS_SPESIALISASI = "spesialisasi";
    private static final String COLUMN_PETUGAS_AREA = "area_tugas";
    private static final String COLUMN_PETUGAS_STATUS = "status";

    private static final String TABLE_PANGGILAN_SOS = "panggilan_sos";
    private static final String COLUMN_PANGGILAN_ID = "id_panggilan";
    private static final String COLUMN_PANGGILAN_LAT = "latitude";
    private static final String COLUMN_PANGGILAN_LONG = "longitude";
    private static final String COLUMN_PANGGILAN_ALAMAT = "alamat_lokasi";
    private static final String COLUMN_PANGGILAN_WAKTU = "waktu_panggilan";
    private static final String COLUMN_PANGGILAN_STATUS = "status_panggilan";
    private static final String COLUMN_PANGGILAN_PETUGAS = "id_petugas";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Buat tabel admin dan petugas
            db.execSQL(CREATE_TABLE_ADMIN);
            db.execSQL(CREATE_TABLE_PETUGAS);
            db.execSQL(CREATE_TABLE_PANGGILAN_SOS);

            Log.d(TAG, "Database tables created successfully");


            ContentValues adminValues = new ContentValues();
            adminValues.put(COLUMN_ADMIN_USERNAME, "admin");
            adminValues.put(COLUMN_ADMIN_PASSWORD, "admin123");
            adminValues.put(COLUMN_ADMIN_NAMA, "Administrator");
            adminValues.put(COLUMN_ADMIN_EMAIL, "admin@siagaki.id");
            adminValues.put(COLUMN_ADMIN_TELEPON, "08123456789");
            long adminResult = db.insert(TABLE_ADMIN, null, adminValues);
            Log.d(TAG, "Admin default ditambahkan dengan ID: " + adminResult);

            // Menambahkan petugas default untuk testing
            ContentValues petugasValues = new ContentValues();
            petugasValues.put(COLUMN_PETUGAS_USERNAME, "petugas");
            petugasValues.put(COLUMN_PETUGAS_PASSWORD, "petugas123");
            petugasValues.put(COLUMN_PETUGAS_NAMA, "Petugas Siaga");
            petugasValues.put(COLUMN_PETUGAS_TELEPON, "08234567891");
            petugasValues.put(COLUMN_PETUGAS_EMAIL, "petugas@siagaki.id");
            petugasValues.put(COLUMN_PETUGAS_SPESIALISASI, "Evakuasi");
            petugasValues.put(COLUMN_PETUGAS_AREA, "Jakarta Selatan");
            petugasValues.put(COLUMN_PETUGAS_STATUS, "tersedia");
            long petugasResult = db.insert(TABLE_PETUGAS, null, petugasValues);
            Log.d(TAG, "Petugas default ditambahkan dengan ID: " + petugasResult);

        } catch (Exception e) {
            Log.e(TAG, "Error saat membuat database: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop semua tabel yang ada dan membuatnya kembali
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANGGILAN_SOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PETUGAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADMIN);
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    // Method untuk menyimpan panggilan SOS
    public long saveSosCall(double latitude, double longitude, String alamat) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PANGGILAN_LAT, latitude);
            values.put(COLUMN_PANGGILAN_LONG, longitude);
            values.put(COLUMN_PANGGILAN_ALAMAT, alamat);
            values.put(COLUMN_PANGGILAN_WAKTU, getCurrentTimestamp());
            values.put(COLUMN_PANGGILAN_STATUS, "aktif");

            long id = db.insert(TABLE_PANGGILAN_SOS, null, values);
            Log.d(TAG, "SOS call saved with ID: " + id);
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Error saving SOS call: " + e.getMessage());
            return -1;
        }
    }

    // Method helper untuk timestamp
    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }


    public Cursor getActiveSosCalls() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selection = COLUMN_PANGGILAN_STATUS + " = ?";
            String[] selectionArgs = {"aktif"};
            Cursor cursor = db.query(
                    TABLE_PANGGILAN_SOS,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COLUMN_PANGGILAN_WAKTU + " DESC" // Terbaru dulu
            );

            Log.d(TAG, "Active SOS calls: " + cursor.getCount());
            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error getting active SOS calls: " + e.getMessage());
            return null;
        }
    }

    // PERBAIKAN: Method untuk mendapatkan semua panggilan SOS (untuk petugas)
    // Kita tidak lagi filter berdasarkan area, tapi kita menampilkan semua panggilan SOS aktif
    // lalu di UI kita tunjukkan area/alamat panggilan supaya petugas bisa memutuskan
    public Cursor getAllActiveSosCalls() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selection = COLUMN_PANGGILAN_STATUS + " = ?";
            String[] selectionArgs = {"aktif"};

            Cursor cursor = db.query(
                    TABLE_PANGGILAN_SOS,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COLUMN_PANGGILAN_WAKTU + " DESC"
            );

            Log.d(TAG, "All active SOS calls: " + cursor.getCount());
            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error getting all active SOS calls: " + e.getMessage());
            return null;
        }
    }

    // Method lama untuk mendapatkan panggilan SOS di area tertentu (tetap ada untuk kompatibilitas)
    public Cursor getSosCallsByArea(String area) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String selection = COLUMN_PANGGILAN_STATUS + " = ? AND " + COLUMN_PANGGILAN_ALAMAT + " LIKE ?";
            String[] selectionArgs = {"aktif", "%" + area + "%"};

            Cursor cursor = db.query(
                    TABLE_PANGGILAN_SOS,
                    null,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    COLUMN_PANGGILAN_WAKTU + " DESC"
            );

            Log.d(TAG, "SOS calls for area " + area + ": " + cursor.getCount());
            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error getting SOS calls by area: " + e.getMessage());
            return null;
        }
    }


    public boolean updateSosCallStatus(long id, String status) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PANGGILAN_STATUS, status);

            int rows = db.update(
                    TABLE_PANGGILAN_SOS,
                    values,
                    COLUMN_PANGGILAN_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            Log.d(TAG, "Updated SOS call " + id + " status to " + status + ": " + (rows > 0));
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating SOS call status: " + e.getMessage());
            return false;
        }
    }


    public boolean assignOfficerToSosCall(long sosCallId, int petugasId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PANGGILAN_PETUGAS, petugasId);

            int rows = db.update(
                    TABLE_PANGGILAN_SOS,
                    values,
                    COLUMN_PANGGILAN_ID + " = ?",
                    new String[]{String.valueOf(sosCallId)}
            );

            Log.d(TAG, "Assigned officer " + petugasId + " to SOS call " + sosCallId + ": " + (rows > 0));
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error assigning officer to SOS call: " + e.getMessage());
            return false;
        }
    }


    public boolean checkAdminLogin(String username, String password) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] columns = {COLUMN_ADMIN_USERNAME};
            String selection = COLUMN_ADMIN_USERNAME + "=?" + " AND " + COLUMN_ADMIN_PASSWORD + "=?";
            String[] selectionArgs = {username, password};

            Cursor cursor = db.query(TABLE_ADMIN, columns, selection, selectionArgs, null, null, null);
            int count = cursor.getCount();
            cursor.close();

            Log.d(TAG, "Admin login check untuk " + username + ": " + (count > 0 ? "berhasil" : "gagal"));
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error saat login admin: " + e.getMessage());
            return false;
        }
    }


    public boolean checkPetugasLogin(String username, String password) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String[] columns = {COLUMN_PETUGAS_USERNAME};
            String selection = COLUMN_PETUGAS_USERNAME + "=?" + " AND " + COLUMN_PETUGAS_PASSWORD + "=?";
            String[] selectionArgs = {username, password};

            Cursor cursor = db.query(TABLE_PETUGAS, columns, selection, selectionArgs, null, null, null);
            int count = cursor.getCount();
            cursor.close();

            Log.d(TAG, "Petugas login check untuk " + username + ": " + (count > 0 ? "berhasil" : "gagal"));
            return count > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error saat login petugas: " + e.getMessage());
            return false;
        }
    }


    public Cursor getPetugasData(String username) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(
                    TABLE_PETUGAS,
                    null,
                    COLUMN_PETUGAS_USERNAME + "=?",
                    new String[]{username},
                    null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                Log.d(TAG, "Data petugas untuk " + username + " ditemukan");
            } else {
                Log.d(TAG, "Data petugas untuk " + username + " tidak ditemukan");
            }

            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error saat mengambil data petugas: " + e.getMessage());
            return null;
        }
    }

    public Cursor getAdminData(String username) {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(
                    TABLE_ADMIN,
                    null,
                    COLUMN_ADMIN_USERNAME + "=?",
                    new String[]{username},
                    null, null, null);

            if (cursor != null && cursor.getCount() > 0) {
                Log.d(TAG, "Data admin untuk " + username + " ditemukan");
            } else {
                Log.d(TAG, "Data admin untuk " + username + " tidak ditemukan");
            }

            return cursor;
        } catch (Exception e) {
            Log.e(TAG, "Error saat mengambil data admin: " + e.getMessage());
            return null;
        }
    }


    public void updateAdminLastLogin(String username) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("terakhir_login", getCurrentTimestamp());

            db.update(TABLE_ADMIN, values, COLUMN_ADMIN_USERNAME + "=?", new String[]{username});
            Log.d(TAG, "Update terakhir login admin " + username + " berhasil");
        } catch (Exception e) {
            Log.e(TAG, "Error saat update terakhir login admin: " + e.getMessage());
        }
    }


    public void updatePetugasLastLogin(String username) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("terakhir_login", getCurrentTimestamp());

            db.update(TABLE_PETUGAS, values, COLUMN_PETUGAS_USERNAME + "=?", new String[]{username});
            Log.d(TAG, "Update terakhir login petugas " + username + " berhasil");
        } catch (Exception e) {
            Log.e(TAG, "Error saat update terakhir login petugas: " + e.getMessage());
        }
    }

    // Method untuk update status petugas
    public boolean updatePetugasStatus(String username, String status) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_PETUGAS_STATUS, status);

            int rows = db.update(TABLE_PETUGAS, values, COLUMN_PETUGAS_USERNAME + "=?", new String[]{username});
            Log.d(TAG, "Update status petugas " + username + " menjadi " + status + ": " + (rows > 0 ? "berhasil" : "gagal"));
            return rows > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error saat update status petugas: " + e.getMessage());
            return false;
        }
    }

    // Method untuk reset database (untuk debugging)
    public void resetDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
        Log.d(TAG, "Database has been reset");
    }
}