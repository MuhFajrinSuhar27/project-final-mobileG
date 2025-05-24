package com.example.projekkhayalan.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.projekkhayalan.R;
import com.example.projekkhayalan.utils.LocationTracker;

import java.util.Locale;

public class SosActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String EMERGENCY_NUMBER = "112";

    private TextView textViewSosDescription;
    private TextView textViewSosStatus;
    private TextView textViewLocationInfo;
    private ProgressBar progressBarSos;
    private Button buttonCallDirect;
    private Button buttonCancelSos;

    private TextToSpeech textToSpeech;
    private LocationTracker locationTracker;
    private int disabilityType;
    private CountDownTimer sosTimer;
    private boolean isEmergencyMessageSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);

        disabilityType = getIntent().getIntExtra("DISABILITY_TYPE", 1);

        initViews();
        setupAccessibilityFeatures();
        initLocationTracker();
        startEmergencyProtocol();
    }

    private void initViews() {
        textViewSosDescription = findViewById(R.id.textViewSosDescription);
        textViewSosStatus = findViewById(R.id.textViewSosStatus);
        textViewLocationInfo = findViewById(R.id.textViewLocationInfo);
        progressBarSos = findViewById(R.id.progressBarSos);
        buttonCallDirect = findViewById(R.id.buttonCallDirect);
        buttonCancelSos = findViewById(R.id.buttonCancelSos);

        buttonCallDirect.setOnClickListener(v -> callEmergencyNumber());
        buttonCancelSos.setOnClickListener(v -> cancelEmergency());
    }

    private void setupAccessibilityFeatures() {
        // Set up text-to-speech for visually impaired users
        if (disabilityType == 1) { // Tunanetra
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                    speakEmergencyInfo();
                }
            });
        }

        // For hearing impaired users, use strong visual cues
        if (disabilityType == 2) { // Tunarungu
            // Meningkatkan ukuran teks dan kontras
            textViewSosDescription.setTextSize(24);
            textViewSosStatus.setTextSize(20);
        }

        // For users with mobility impairment, make buttons larger
        if (disabilityType == 3) { // Tunadaksa
            buttonCallDirect.setPadding(24, 24, 24, 24);
            buttonCancelSos.setPadding(24, 24, 24, 24);
        }

        // For users with cognitive impairments, make text simpler
        if (disabilityType == 4) { // Tunagrahita
            textViewSosDescription.setText("BANTUAN DARURAT SEDANG DIKIRIM");
            textViewSosStatus.setText("TUNGGU SEBENTAR...");
        }
    }

    private void speakEmergencyInfo() {
        if (textToSpeech != null && disabilityType == 1) {
            String emergencyMessage = "Mode darurat aktif. Sinyal bantuan sedang dikirim. Lokasi Anda sedang dikirim ke layanan darurat. " +
                    "Tekan tombol di bagian tengah untuk menghubungi petugas darurat secara langsung.";
            textToSpeech.speak(emergencyMessage, TextToSpeech.QUEUE_FLUSH, null, "emergency");
        }
    }

    private void initLocationTracker() {
        locationTracker = new LocationTracker(this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            getAndSendLocation();
        }
    }

    private void getAndSendLocation() {
        if (locationTracker != null) {
            locationTracker.getCurrentLocation(location -> {
                if (location != null) {
                    String locationInfo = "Lokasi Anda: " + location.getLatitude() +
                            ", " + location.getLongitude() +
                            "\nLokasi ini sedang dikirim ke layanan darurat";
                    textViewLocationInfo.setText(locationInfo);
                    sendEmergencyMessage(location);
                } else {
                    textViewLocationInfo.setText("Tidak dapat mendapatkan lokasi Anda. Silakan gunakan tombol panggil untuk bantuan segera.");
                }
            });
        }
    }

    private void sendEmergencyMessage(Location location) {
        // Simulate sending emergency message
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            isEmergencyMessageSent = true;
            textViewSosStatus.setText("Sinyal darurat telah dikirim! Petugas akan segera menghubungi Anda.");

            if (disabilityType == 1 && textToSpeech != null) { // Tunanetra
                textToSpeech.speak("Sinyal darurat telah dikirim! Petugas akan segera menghubungi Anda.",
                        TextToSpeech.QUEUE_FLUSH, null, "sent");
            }
        }, 3000);
    }

    private void startEmergencyProtocol() {
        sosTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / 1000);
                textViewSosStatus.setText("Menghubungi layanan darurat dalam " + secondsRemaining + " detik...");

                if (disabilityType == 1 && textToSpeech != null && secondsRemaining == 5) {
                    textToSpeech.speak("Peringatan: Panggilan otomatis ke layanan darurat dalam 5 detik.",
                            TextToSpeech.QUEUE_FLUSH, null, "countdown");
                }
            }

            @Override
            public void onFinish() {
                if (!isFinishing()) {
                    callEmergencyNumber();
                }
            }
        }.start();
    }

    private void callEmergencyNumber() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + EMERGENCY_NUMBER));

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    PERMISSION_REQUEST_CODE);
        } else {
            startActivity(callIntent);
        }
    }

    private void cancelEmergency() {
        if (sosTimer != null) {
            sosTimer.cancel();
        }

        Toast.makeText(this, "Permintaan bantuan dibatalkan", Toast.LENGTH_SHORT).show();

        if (disabilityType == 1 && textToSpeech != null) {
            textToSpeech.speak("Permintaan bantuan darurat dibatalkan",
                    TextToSpeech.QUEUE_FLUSH, null, "cancel");
        }

        // Delay before finishing to ensure message is heard
        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    getAndSendLocation();
                } else if (permissions[0].equals(Manifest.permission.CALL_PHONE)) {
                    callEmergencyNumber();
                }
            } else {
                Toast.makeText(this, "Izin diperlukan untuk fungsi darurat", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (sosTimer != null) {
            sosTimer.cancel();
        }
        super.onDestroy();
    }
}