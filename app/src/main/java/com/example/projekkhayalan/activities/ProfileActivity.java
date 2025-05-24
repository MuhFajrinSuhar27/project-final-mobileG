package com.example.projekkhayalan.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.projekkhayalan.R;
import com.example.projekkhayalan.utils.AccessibilityHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components - menggunakan MaterialButton, bukan Button
    private ImageView imageViewProfile;
    private MaterialButton buttonChangePhoto;
    private TextInputEditText editTextName;
    private TextInputEditText editTextAddress;
    private TextInputEditText editTextEmergencyName1;
    private MaterialButton buttonAddMoreContacts;
    private MaterialButton buttonSaveProfile;

    private int disabilityType;
    private Uri selectedImageUri;
    private AccessibilityHelper accessibilityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        disabilityType = getIntent().getIntExtra("DISABILITY_TYPE", 1);

        initViews();
        setupToolbar();
        setupAccessibilityFeatures();
        loadUserProfile();
        setupClickListeners();
    }

    private void initViews() {
        imageViewProfile = findViewById(R.id.imageViewProfile);

        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextEmergencyName1 = findViewById(R.id.editTextEmergencyName1);
        buttonAddMoreContacts = findViewById(R.id.buttonAddMoreContacts);
        buttonSaveProfile = findViewById(R.id.buttonSaveProfile);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupAccessibilityFeatures() {
        accessibilityHelper = new AccessibilityHelper(this, disabilityType);

        // Tambahkan pengaturan standard untuk semua jenis pengguna
        if (imageViewProfile != null) {
            imageViewProfile.setContentDescription("Foto profil Anda. Ketuk untuk mengubah.");
        }

        // Pengaturan standar untuk text size dan padding
        if (buttonChangePhoto != null) {
            buttonChangePhoto.setText("Ganti Foto");
            buttonChangePhoto.setTextSize(16);
            buttonChangePhoto.setPadding(16, 16, 16, 16);
        }

        if (buttonSaveProfile != null) {
            buttonSaveProfile.setText("SIMPAN");
            buttonSaveProfile.setTextSize(16);
            buttonSaveProfile.setPadding(16, 16, 16, 16);
        }

        if (editTextName != null) {
            editTextName.setHint("Nama Lengkap");
        }

        if (editTextAddress != null) {
            editTextAddress.setHint("Alamat");
        }

        if (editTextEmergencyName1 != null) {
            editTextEmergencyName1.setHint("Nama Kontak Darurat");
        }

        try {

            if (accessibilityHelper != null) {
                accessibilityHelper.adjustUI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserProfile() {
        try {
            // Load user profile data from SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
            if (editTextName != null) {
                editTextName.setText(prefs.getString("name", ""));
            }
            if (editTextAddress != null) {
                editTextAddress.setText(prefs.getString("address", ""));
            }
            if (editTextEmergencyName1 != null) {
                editTextEmergencyName1.setText(prefs.getString("emergencyName1", ""));
            }

            // Load profile image (if available)
            String imageUriString = prefs.getString("profileImageUri", null);
            if (imageUriString != null && imageViewProfile != null) {
                selectedImageUri = Uri.parse(imageUriString);
                imageViewProfile.setImageURI(selectedImageUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupClickListeners() {
        // Untuk mengganti foto profil
        if (buttonChangePhoto != null) {
            buttonChangePhoto.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            });
        }

        // Untuk menyimpan data profil
        if (buttonSaveProfile != null) {
            buttonSaveProfile.setOnClickListener(v -> saveUserProfile());
        }

        // Untuk menambahkan kontak darurat lain
        if (buttonAddMoreContacts != null) {
            buttonAddMoreContacts.setOnClickListener(v -> {
                Snackbar.make(v, "Fitur ini akan tersedia dalam update berikutnya", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getResources().getColor(android.R.color.holo_blue_dark))
                        .setTextColor(getResources().getColor(android.R.color.white))
                        .show();
            });
        }
    }

    private void saveUserProfile() {
        try {
            String name = editTextName != null ? editTextName.getText().toString().trim() : "";
            String address = editTextAddress != null ? editTextAddress.getText().toString().trim() : "";
            String emergencyName = editTextEmergencyName1 != null ? editTextEmergencyName1.getText().toString().trim() : "";

            // Simple validation
            if (name.isEmpty() && editTextName != null) {
                editTextName.setError("Nama tidak boleh kosong");
                editTextName.requestFocus();
                return;
            }

            // Save to SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserProfile", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("name", name);
            editor.putString("address", address);
            editor.putString("emergencyName1", emergencyName);
            editor.putInt("disabilityType", disabilityType);

            // Save image URI if available
            if (selectedImageUri != null) {
                editor.putString("profileImageUri", selectedImageUri.toString());
            }

            editor.apply();

            // Provide feedback
            Toast.makeText(this, "Profil berhasil disimpan", Toast.LENGTH_SHORT).show();

            // Return to previous screen
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal menyimpan profil: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (imageViewProfile != null) {
                imageViewProfile.setImageURI(selectedImageUri);
            }
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
}