package com.example.projekkhayalan.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.projekkhayalan.MainActivity;
import com.example.projekkhayalan.R;
import com.example.projekkhayalan.database.DatabaseHelper;
import com.example.projekkhayalan.utils.LocationTracker;

import java.io.IOException;
import java.util.List;
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

    }

    private void initViews() {
        textViewSosDescription = findViewById(R.id.textViewSosDescription);
        textViewSosStatus = findViewById(R.id.textViewSosStatus);
        textViewLocationInfo = findViewById(R.id.textViewLocationInfo);
        progressBarSos = findViewById(R.id.progressBarSos);
        buttonCallDirect = findViewById(R.id.buttonCallDirect);
        buttonCancelSos = findViewById(R.id.buttonCancelSos);


        buttonCancelSos.setOnClickListener(v -> cancelEmergency());
    }

    private void setupAccessibilityFeatures() {
        Log.d("SosActivity", "Setting up accessibility features, disability type: " + disabilityType);
        
        // Text-to-Speech hanya untuk tunanetra
        if (disabilityType == 1) { // Tunanetra
            Log.d("SosActivity", "Initializing TTS for blind users");
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                    speakEmergencyInfo();
                }
            });
        } else {
            // Pastikan textToSpeech adalah null untuk jenis disabilitas selain tunanetra
            Log.d("SosActivity", "TTS disabled for non-blind users (type: " + disabilityType + ")");
            textToSpeech = null;
        }
    
        // Penyesuaian untuk disabilitas lain
        if (disabilityType == 2) { // Tunarungu
            textViewSosDescription.setTextSize(24);
            textViewSosStatus.setTextSize(20);
        }
    
        if (disabilityType == 3) { // Tunadaksa
            buttonCallDirect.setPadding(24, 24, 24, 24);
            buttonCancelSos.setPadding(24, 24, 24, 24);
        }
    
        if (disabilityType == 4) { // Tunagrahita
            textViewSosDescription.setText("BANTUAN DARURAT SEDANG DIKIRIM");
            textViewSosStatus.setText("TUNGGU SEBENTAR...");
        }
    }

    private void speakEmergencyInfo() {
        if (textToSpeech != null && disabilityType == 1) {
            String emergencyMessage = "Mode darurat aktif. Sinyal bantuan sedang dikirim. Lokasi Anda sedang dikirim ke layanan darurat, Mohon Ditunggu ";

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
                            "\nLokasi ini sedang dikirim ke layanan darurat, Mohon di tunggu";
                    textViewLocationInfo.setText(locationInfo);
                    sendEmergencyMessage(location);
                } else {
                    textViewLocationInfo.setText("Tidak dapat mendapatkan lokasi Anda. Silakan gunakan tombol panggil untuk bantuan segera.");
                }
            });
        }
    }



    private void sendEmergencyMessage(Location location) {
        try {
            // Simpan lokasi ke database
            DatabaseHelper dbHelper = new DatabaseHelper(this);

            // Untuk alamat, gunakan koordinat sebagai fallback
            String alamat = "Lokasi koordinat: " + location.getLatitude() + ", " + location.getLongitude();

            // Coba dapatkan alamat sebenarnya menggunakan Geocoder
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i));
                        if (i < address.getMaxAddressLineIndex()) sb.append(", ");
                    }
                    alamat = sb.toString();
                }
            } catch (IOException e) {
                Log.e("SosActivity", "Error saat mengambil alamat: " + e.getMessage());
            }

            Log.d("SosActivity", "Mencoba menyimpan SOS call dengan koordinat: " +
                    location.getLatitude() + ", " + location.getLongitude());

            // Simpan ke database
            long sosCallId = dbHelper.saveSosCall(location.getLatitude(), location.getLongitude(), alamat);

            if (sosCallId > 0) {
                Log.d("SosActivity", "SOS Call berhasil disimpan dengan ID: " + sosCallId);
            } else {
                Log.e("SosActivity", "Gagal menyimpan SOS call!");
            }

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                isEmergencyMessageSent = true;

                startReturnToMainTimer(5);
            }, 6000);
        } catch (Exception e) {
            Log.e("SosActivity", "Error saat mengirim emergency message: " + e.getMessage());
            e.printStackTrace();


            startReturnToMainTimer(4);
        }
    }
    private CountDownTimer returnToMainTimer;


    private void backToMain() {
        Intent intent = new Intent(SosActivity.this, MainActivity.class);
        intent.putExtra("DISABILITY_TYPE", disabilityType);
        startActivity(intent);
        finish();
    }
    private void startReturnToMainTimer(int seconds) {
        if (returnToMainTimer != null) {
            returnToMainTimer.cancel();
        }
    
        final long totalTime = seconds * 1000;
        final long interval = 1000;
    
        returnToMainTimer = new CountDownTimer(totalTime, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                textViewSosStatus.setText("Kembali ke halaman utama dalam " + secondsLeft + " detik...");
            }
    
            @Override
            public void onFinish() {
                // Ganti kode yang ada dengan panggilan ke backToMain()
                backToMain();
            }
        }.start();
    }
    
    // Modifikasi method cancelEmergency() untuk menggunakan backToMain()
    private void cancelEmergency() {
        if (sosTimer != null) {
            sosTimer.cancel();
        }
    
        Toast.makeText(this, "Permintaan bantuan dibatalkan", Toast.LENGTH_SHORT).show();
    
        if (disabilityType == 1 && textToSpeech != null) {
            textToSpeech.speak("Permintaan bantuan darurat dibatalkan",
                    TextToSpeech.QUEUE_FLUSH, null, "cancel");
        }
        startReturnToMainTimer(2);
    }





    protected void onDestroy() {
        if (returnToMainTimer != null) {returnToMainTimer.cancel();
            returnToMainTimer = null;
        }
        if (textToSpeech != null) {textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (sosTimer != null) {sosTimer.cancel();
        }super.onDestroy();
    }
}