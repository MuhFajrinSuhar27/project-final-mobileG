package com.example.projekkhayalan.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekkhayalan.R;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSosCalls;
    private TextView textViewNoSosCalls;
    private Button buttonManageOfficers, buttonAddOfficer, buttonLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupToolbar();
        setupClickListeners();
        loadActiveSosCalls();
    }

    private void initViews() {
        recyclerViewSosCalls = findViewById(R.id.recyclerViewSosCalls);
        textViewNoSosCalls = findViewById(R.id.textViewNoSosCalls);
        buttonManageOfficers = findViewById(R.id.buttonManageOfficers);
        buttonAddOfficer = findViewById(R.id.buttonAddOfficer);
        buttonLogout = findViewById(R.id.buttonLogout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupClickListeners() {
        buttonManageOfficers.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur kelola petugas akan segera hadir", Toast.LENGTH_SHORT).show();
        });

        buttonAddOfficer.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur tambah petugas akan segera hadir", Toast.LENGTH_SHORT).show();
        });

        buttonLogout.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadActiveSosCalls() {
        // Untuk demo, tampilkan pesan tidak ada panggilan SOS
        recyclerViewSosCalls.setVisibility(android.view.View.GONE);
        textViewNoSosCalls.setVisibility(android.view.View.VISIBLE);

        // Setelah database lengkap, terapkan kode untuk mengambil data SOS
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}