package com.example.projekkhayalan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.projekkhayalan.MainActivity;
import com.example.projekkhayalan.R;
import com.example.projekkhayalan.utils.AccessibilityHelper;

import java.util.Locale;

public class HomeScreenActivity extends AppCompatActivity {

    private CardView cardRescue;
    private CardView cardEmergencyCall;
    private CardView cardDisasterInfo;
    private CardView cardDisasterWarning;

    private int disabilityType;
    private TextToSpeech textToSpeech;
    private AccessibilityHelper accessibilityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Dapatkan jenis disabilitas dari intent
        disabilityType = getIntent().getIntExtra("DISABILITY_TYPE", 1);

        initViews();
        setupAccessibilityFeatures();
        setupCardClickListeners();
    }

    private void initViews() {
        cardRescue = findViewById(R.id.cardRescue);
        cardEmergencyCall = findViewById(R.id.cardEmergencyCall);
        cardDisasterInfo = findViewById(R.id.cardDisasterInfo);
        cardDisasterWarning = findViewById(R.id.cardDisasterWarning);
    }

    private void setupAccessibilityFeatures() {
        accessibilityHelper = new AccessibilityHelper(this, disabilityType);

        // Inisialisasi TextToSpeech untuk tunanetra
        if (disabilityType == 1) { // Tunanetra
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Fallback ke bahasa Inggris jika bahasa Indonesia tidak tersedia
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                    speakWelcomeMessage();
                }
            });
        }

        // Modifikasi UI berdasarkan jenis disabilitas
        accessibilityHelper.adjustUI();
    }

    private void speakWelcomeMessage() {
        if (textToSpeech != null && disabilityType == 1) {
            String welcomeMessage = "Selamat datang di Aplikasi Siaga Bencana untuk Difabel. " +
                    "Pilih salah satu fitur yang tersedia.";
            textToSpeech.speak(welcomeMessage, TextToSpeech.QUEUE_FLUSH, null, "welcome");
        }
    }

    private void setupCardClickListeners() {
        // Menentukan fitur yang akan diakses berdasarkan kartu yang dipilih
        cardRescue.setOnClickListener(v -> {
            navigateToMainActivity("rescue");
        });

        cardEmergencyCall.setOnClickListener(v -> {
            navigateToMainActivity("emergency_call");
        });

        cardDisasterInfo.setOnClickListener(v -> {
            navigateToMainActivity("disaster_info");
        });

        cardDisasterWarning.setOnClickListener(v -> {
            navigateToMainActivity("disaster_warning");
        });
    }

    private void navigateToMainActivity(String selectedFeature) {
        // Buat intent untuk navigasi ke MainActivity
        Intent intent = new Intent(HomeScreenActivity.this, MainActivity.class);

        // Tambahkan data yang diperlukan
        intent.putExtra("DISABILITY_TYPE", disabilityType);
        intent.putExtra("SELECTED_FEATURE", selectedFeature);

        // Mulai aktivitas
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