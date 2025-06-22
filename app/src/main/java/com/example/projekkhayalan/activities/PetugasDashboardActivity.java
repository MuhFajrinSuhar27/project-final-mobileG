package com.example.projekkhayalan.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekkhayalan.R;
import com.example.projekkhayalan.adapters.SosCallAdapter;
import com.example.projekkhayalan.database.DatabaseHelper;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class PetugasDashboardActivity extends AppCompatActivity implements SosCallAdapter.SosCallActionListener {

    private static final String TAG = "PetugasDashboardActivity";
    private TextView textViewNamaPetugas;
    private TextView textViewSpecialization;
    private RecyclerView recyclerViewActiveCalls;
    private TextView textViewNoActiveCalls;
    private Button buttonUpdateStatus;
    private Button buttonLogout;

    private String username;
    private String namaPetugas;
    private String areaTugas;
    private String spesialisasi;
    private String statusPetugas = "tersedia";

    private DatabaseHelper dbHelper;
    private SosCallAdapter sosCallAdapter;

    private Handler refreshHandler = new Handler();
    private Runnable refreshRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_petugas_dashboard);

        // Inisialisasi DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Dapatkan username dari intent
        username = getIntent().getStringExtra("USERNAME_PETUGAS");
        if (username == null) {
            Toast.makeText(this, "Error: Username tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        Log.d(TAG, "Username petugas: " + username);

        initViews();
        loadPetugasData();
        setupToolbar();
        setupClickListeners();
        setupAutoRefresh();
        loadActiveSosCalls(); // Load data SOS
    }

    private void setupAutoRefresh() {
        refreshRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Auto refresh panggilan SOS");
                loadActiveSosCalls();
                refreshHandler.postDelayed(this, 30000); // Refresh setiap 30 detik
            }
        };
        refreshHandler.postDelayed(refreshRunnable, 30000);
    }

    private void initViews() {
        textViewNamaPetugas = findViewById(R.id.textViewNamaPetugas);
        textViewSpecialization = findViewById(R.id.textViewSpecialization);

        recyclerViewActiveCalls = findViewById(R.id.recyclerViewActiveCalls);
        textViewNoActiveCalls = findViewById(R.id.textViewNoActiveCalls);
        buttonUpdateStatus = findViewById(R.id.buttonUpdateStatus);
        buttonLogout = findViewById(R.id.buttonLogout);

        // Setup RecyclerView
        recyclerViewActiveCalls.setLayoutManager(new LinearLayoutManager(this));
        Log.d(TAG, "Views initialized");
    }

    private void loadPetugasData() {
        Log.d(TAG, "Loading data petugas untuk: " + username);
        Cursor cursor = dbHelper.getPetugasData(username);

        if (cursor != null && cursor.moveToFirst()) {
            namaPetugas = cursor.getString(cursor.getColumnIndexOrThrow("nama_lengkap"));
            areaTugas = cursor.getString(cursor.getColumnIndexOrThrow("area_tugas"));
            spesialisasi = cursor.getString(cursor.getColumnIndexOrThrow("spesialisasi"));
            statusPetugas = cursor.getString(cursor.getColumnIndexOrThrow("status"));

            Log.d(TAG, "Data petugas ditemukan - Nama: " + namaPetugas + ", Area: " + areaTugas +
                    ", Spesialisasi: " + spesialisasi + ", Status: " + statusPetugas);

            textViewNamaPetugas.setText(namaPetugas);
            textViewSpecialization.setText(spesialisasi != null ? spesialisasi : "Tidak ada spesialisasi");


            updateStatusButtonText();

            cursor.close();
        } else {
            Log.e(TAG, "Error: Data petugas tidak ditemukan untuk username: " + username);
            Toast.makeText(this, "Error: Data petugas tidak ditemukan", Toast.LENGTH_SHORT).show();
            if (cursor != null) cursor.close();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Petugas Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        buttonUpdateStatus.setOnClickListener(v -> {
            showStatusUpdateDialog();
        });

        buttonLogout.setOnClickListener(v -> {
            finish();
        });
    }

    private void showStatusUpdateDialog() {
        String[] statuses = {"tersedia", "sibuk", "istirahat", "tidak aktif"};
        int checkedItem = 0;

        // Cari indeks status saat ini
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(statusPetugas)) {
                checkedItem = i;
                break;
            }
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Update Status")
                .setSingleChoiceItems(statuses, checkedItem, null)
                .setPositiveButton("Update", (dialog, which) -> {
                    int selectedPosition = ((androidx.appcompat.app.AlertDialog) dialog).getListView().getCheckedItemPosition();
                    String newStatus = statuses[selectedPosition];

                    // Update status di database
                    boolean success = dbHelper.updatePetugasStatus(username, newStatus);
                    if (success) {
                        statusPetugas = newStatus;
                        updateStatusButtonText();
                        Toast.makeText(PetugasDashboardActivity.this,
                                "Status berhasil diperbarui menjadi " + newStatus,
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PetugasDashboardActivity.this,
                                "Gagal memperbarui status",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void updateStatusButtonText() {
        buttonUpdateStatus.setText("Status: " + statusPetugas);

        // Update warna button berdasarkan status
        int colorResId;
        switch (statusPetugas) {
            case "tersedia":
                colorResId = android.R.color.holo_green_dark;
                break;
            case "sibuk":
                colorResId = android.R.color.holo_orange_dark;
                break;
            case "istirahat":
                colorResId = android.R.color.holo_blue_dark;
                break;
            default:
                colorResId = android.R.color.darker_gray;
        }

        buttonUpdateStatus.setBackgroundColor(getResources().getColor(colorResId));
    }

    private void loadActiveSosCalls() {
        Log.d(TAG, "Loading panggilan SOS aktif");


        Cursor cursor = dbHelper.getAllActiveSosCalls();

        if (cursor != null) {
            Log.d(TAG, "Jumlah panggilan SOS aktif: " + cursor.getCount());

            if (cursor.getCount() > 0) {
                recyclerViewActiveCalls.setVisibility(View.VISIBLE);
                textViewNoActiveCalls.setVisibility(View.GONE);

                if (sosCallAdapter == null) {
                    sosCallAdapter = new SosCallAdapter(this, cursor);
                    recyclerViewActiveCalls.setAdapter(sosCallAdapter);
                    Log.d(TAG, "Adapter dibuat baru");
                } else {
                    sosCallAdapter.swapCursor(cursor);
                    Log.d(TAG, "Cursor di-swap");
                }
            } else {
                recyclerViewActiveCalls.setVisibility(View.GONE);
                textViewNoActiveCalls.setVisibility(View.VISIBLE);
                textViewNoActiveCalls.setText("Tidak ada panggilan SOS aktif");
                Log.d(TAG, "Tidak ada panggilan SOS aktif");

                if (cursor != null) {
                    cursor.close();
                }
            }
        } else {
            recyclerViewActiveCalls.setVisibility(View.GONE);
            textViewNoActiveCalls.setVisibility(View.VISIBLE);
            textViewNoActiveCalls.setText("Error mengakses data panggilan SOS");
            Log.e(TAG, "Error: cursor null saat mengambil panggilan SOS");
        }
    }

    @Override
    public void onSosCallCompleted(long sosCallId) {
        Toast.makeText(this, "Panggilan SOS berhasil ditandai selesai", Toast.LENGTH_SHORT).show();
        loadActiveSosCalls(); // Reload data
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActiveSosCalls();


        if (username != null) {
            dbHelper.updatePetugasLastLogin(username);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (refreshHandler != null && refreshRunnable != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method debug untuk memaksa reload data
    private void debugForceDatabaseLoad() {
        dbHelper.resetDatabase(this);


        dbHelper = new DatabaseHelper(this);

        Toast.makeText(this, "Database direset. Restart aplikasi.", Toast.LENGTH_SHORT).show();
        finish();
    }
}