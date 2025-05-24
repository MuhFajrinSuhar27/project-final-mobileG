package com.example.projekkhayalan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.projekkhayalan.R;
import com.example.projekkhayalan.adapters.DisabilityTypeAdapter;
import com.example.projekkhayalan.models.DisabilityType;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private RecyclerView recyclerViewDisabilityTypes;
    private DisabilityTypeAdapter adapter;
    private List<DisabilityType> disabilityTypes;
    private int selectedDisabilityPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupDisabilityTypesRecyclerView();
        setupClickListeners();
    }

    private void initViews() {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister);
        recyclerViewDisabilityTypes = findViewById(R.id.recyclerViewDisabilityTypes);
    }

    private void setupDisabilityTypesRecyclerView() {
        disabilityTypes = new ArrayList<>();
        disabilityTypes.add(new DisabilityType(1, "Tunanetra", R.drawable.tunanetra));
        disabilityTypes.add(new DisabilityType(2, "Tunarungu", R.drawable.tunarungu));
        disabilityTypes.add(new DisabilityType(3, "Tunadaksa", R.drawable.tunadaksa));
        disabilityTypes.add(new DisabilityType(4, "Tunagrahita", R.drawable.tunagrahita));

        adapter = new DisabilityTypeAdapter(this, disabilityTypes);
        adapter.setOnItemClickListener((position, disabilityType) -> {
            selectedDisabilityPosition = position;
            adapter.setSelectedPosition(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(LoginActivity.this,
                    "Dipilih: " + disabilityType.getName(),
                    Toast.LENGTH_SHORT).show();
        });

        recyclerViewDisabilityTypes.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewDisabilityTypes.setAdapter(adapter);
    }

    private void setupClickListeners() {
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Mohon isi username dan password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedDisabilityPosition == -1) {
                Toast.makeText(LoginActivity.this, "Mohon pilih jenis disabilitas Anda", Toast.LENGTH_SHORT).show();
                return;
            }

            // Redirect ke halaman HomeScreen bukan MainActivity
            Intent intent = new Intent(LoginActivity.this, HomeScreenActivity.class);
            intent.putExtra("DISABILITY_TYPE", disabilityTypes.get(selectedDisabilityPosition).getId());
            startActivity(intent);
            finish();
        });

        textViewRegister.setOnClickListener(v -> {
            // Implementasi pendaftaran (tidak ditampilkan di contoh ini)
            Toast.makeText(LoginActivity.this, "Fitur registrasi akan segera hadir!", Toast.LENGTH_SHORT).show();
        });
    }
}