package com.example.projekkhayalan.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.projekkhayalan.MainActivity;
import com.example.projekkhayalan.R;
import com.example.projekkhayalan.utils.AccessibilityHelper;
import com.example.projekkhayalan.utils.TextToSpeechManager;

import java.util.Locale;

public class HomeScreenActivity extends AppCompatActivity {

    private static final String TAG = "HomeScreenActivity";
    private CardView cardTunanetra;  // Sesuaikan dengan ID di layout
    private CardView cardTunarungu;  // Sesuaikan dengan ID di layout
    private CardView cardTunadaksa;  // Sesuaikan dengan ID di layout
    private CardView cardTunagrahita; // Sesuaikan dengan ID di layout

    private int disabilityType = 1;  // Default: tidak diketahui
    private TextToSpeech textToSpeech;
    private AccessibilityHelper accessibilityHelper;
    private SharedPreferences preferences;
    private boolean hasSpokenWelcomeMessage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Ambil jenis disabilitas yang tersimpan (default: 0)
        disabilityType = preferences.getInt("DISABILITY_TYPE", 0);

        Log.d(TAG, "Disability Type from preferences: " + disabilityType);

        initViews();
        setupAccessibilityFeatures();
        setupCardClickListeners();
    }

    private void initViews() {
        // Sesuaikan ID dengan layout XML Anda
        cardTunanetra = findViewById(R.id.cardDisasterInfo);    // ID untuk kartu tunanetra
        cardTunarungu = findViewById(R.id.cardRescue);          // ID untuk kartu tunarungu
        cardTunadaksa = findViewById(R.id.cardEmergencyCall);   // ID untuk kartu tunadaksa
        cardTunagrahita = findViewById(R.id.cardDisasterWarning); // ID untuk kartu tunagrahita
    }

    private void setupAccessibilityFeatures() {
        accessibilityHelper = new AccessibilityHelper(this, disabilityType);



        // Inisialisasi TextToSpeech HANYA untuk tunanetra
        if (disabilityType == 1) { // Tunanetra
            Log.d(TAG, "Initializing TTS for blind users");
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }

                    if (!hasSpokenWelcomeMessage) {
                        speakWelcomeMessage();
                        hasSpokenWelcomeMessage = true; // Set flag menjadi true
                    }
                }
            });
        } else {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
            }
            textToSpeech = null;
            Log.d(TAG, "TTS disabled for non-blind users (type: " + disabilityType + ")");
        }

        // Modifikasi UI berdasarkan jenis disabilitas
        accessibilityHelper.adjustUI();
    }

    private void speakWelcomeMessage() {
        if (textToSpeech != null) {
            String welcomeMessage = "Selamat datang di Aplikasi Siaga Bencana untuk Difabel. " +
                    "Pilih salah satu jenis disabilitas.";
            textToSpeech.speak(welcomeMessage, TextToSpeech.QUEUE_FLUSH, null, "welcome");
        }
    }

    private void setupCardClickListeners() {
        // Menentukan jenis disabilitas berdasarkan kartu yang dipilih
        cardTunanetra.setOnClickListener(v -> {
            setDisabilityType(1); // 1 = tunanetra
            navigateToMainApp();
        });

        cardTunarungu.setOnClickListener(v -> {
            setDisabilityType(2); // 2 = tunarungu
            navigateToMainApp();
        });

        cardTunadaksa.setOnClickListener(v -> {
            setDisabilityType(3); // 3 = tunadaksa
            navigateToMainApp();
        });

        cardTunagrahita.setOnClickListener(v -> {
            setDisabilityType(4); // 4 = tunagrahita
            navigateToMainApp();
        });
    }

    // Metode untuk mengubah jenis disabilitas
    private void setDisabilityType(int newDisabilityType) {
        if (disabilityType == 1 && newDisabilityType != 1) {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
                textToSpeech = null;
            }
        }

        // Set jenis disabilitas baru
        disabilityType = newDisabilityType;

        // Simpan ke SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("DISABILITY_TYPE", disabilityType);
        editor.apply();

        Log.d(TAG, "Disability Type changed to: " + disabilityType);

        // Perbarui fitur aksesibilitas
        setupAccessibilityFeatures();
    }

    // Navigasi ke aplikasi utama
    private void navigateToMainApp() {
        Intent intent = new Intent(HomeScreenActivity.this, MainActivity.class);
        intent.putExtra("DISABILITY_TYPE", disabilityType);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}