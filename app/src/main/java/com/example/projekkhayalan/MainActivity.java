package com.example.projekkhayalan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projekkhayalan.activities.ProfileActivity;
import com.example.projekkhayalan.activities.SosActivity;
import com.example.projekkhayalan.api.WeatherApiClient;
import com.example.projekkhayalan.api.WeatherApiService;
import com.example.projekkhayalan.fragments.TutorialFragment;
import com.example.projekkhayalan.models.WeatherResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button buttonSos;


    private TextView textViewCity;
    private TextView textViewTime;
    private TextView textViewWeatherCondition;
    private TextView textViewTemperatureBig;
    private TextView textViewWeatherDetails;
    private TextView textViewAlertInfo;

    private int disabilityType;
    private String selectedFeature;
    private TextToSpeech textToSpeech;
    private static final String TAG = "MainActivity";

    // Tambahkan flag untuk mencegah recursive calls
    private boolean isNavigatingProgrammatically = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get intent data
        disabilityType = getIntent().getIntExtra("DISABILITY_TYPE", 1);
        selectedFeature = getIntent().getStringExtra("SELECTED_FEATURE");

        initViews();
        setupBottomNavigation();
        setupSosButton();
        loadWeatherData(); // Load weather data


        setupAccessibilityFeatures();
    }

    private void initViews() {
        // Inisialisasi views untuk UI cuaca baru
        textViewCity = findViewById(R.id.textViewCity);
        textViewTime = findViewById(R.id.textViewTime);
        textViewWeatherCondition = findViewById(R.id.textViewWeatherCondition);
        textViewTemperatureBig = findViewById(R.id.textViewTemperatureBig);
        textViewWeatherDetails = findViewById(R.id.textViewWeatherDetails);
        textViewAlertInfo = findViewById(R.id.textViewAlertInfo);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        buttonSos = findViewById(R.id.buttonSos);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Hindari pemrosesan ulang jika navigasi sedang dilakukan secara programmatic
            if (isNavigatingProgrammatically) {
                isNavigatingProgrammatically = false;
                return true;
            }

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                // Jika ada fragment di backstack, kembalikan ke tampilan utama
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    showMainContent(false); // Jangan set selected item lagi
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("DISABILITY_TYPE", disabilityType);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_tutorial) {
                showFragmentHideMainContent();
                return true;
            }
            return false;
        });
    }

    private void setupSosButton() {
        buttonSos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SosActivity.class);
            intent.putExtra("DISABILITY_TYPE", disabilityType);
            startActivity(intent);
        });
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
                    speakWelcome();
                }
            });
        }

        // For hearing impaired users, use strong visual cues
        if (disabilityType == 2) { // Tunarungu
            textViewCity.setTextSize(40);
            textViewWeatherCondition.setTextSize(36);
            textViewAlertInfo.setTextSize(20);
        }

        // For users with mobility impairment, make buttons larger
        if (disabilityType == 3) { // Tunadaksa
            buttonSos.setPadding(32, 32, 32, 32);
        }
    }

    private void speakWelcome() {
        if (textToSpeech != null && disabilityType == 1) {
            String welcomeMessage = "Selamat datang di aplikasi. ";
            textToSpeech.speak(welcomeMessage, TextToSpeech.QUEUE_FLUSH, null, "welcome");
        }
    }

    private void loadWeatherData() {
        // Tampilkan data loading
        textViewCity.setText("Memuat...");
        textViewTime.setText("");
        textViewWeatherCondition.setText("Memuat data cuaca");
        textViewTemperatureBig.setText("--°");
        textViewWeatherDetails.setText("T:--° R:--%");
        textViewAlertInfo.setText("Memuat informasi peringatan...");

        // Buat instance dari WeatherApiService
        WeatherApiService apiService = WeatherApiClient.getWeatherService();

        // Lakukan panggilan API dengan kode wilayah yang Anda berikan
        Call<WeatherResponse> call = apiService.getWeatherForecast("73.71.14.1001");

        // Eksekusi panggilan API secara asynchronous
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Mendapatkan data respons
                    WeatherResponse weatherData = response.body();
                    displayWeatherData(weatherData);
                } else {
                    // Menampilkan pesan error jika ada masalah dengan respons
                    textViewCity.setText("Error");
                    textViewWeatherCondition.setText("Gagal memuat data cuaca");
                    textViewAlertInfo.setText("Gagal memuat data cuaca. Kode: " + response.code());
                    Log.e(TAG, "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // Menampilkan pesan error jika panggilan gagal
                textViewCity.setText("Error");
                textViewWeatherCondition.setText("Koneksi gagal");
                textViewAlertInfo.setText("Gagal terhubung ke server BMKG. Periksa koneksi Anda.");
                Log.e(TAG, "Failure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void displayWeatherData(WeatherResponse weatherData) {
        try {
            // Pastikan data tidak null dan memiliki struktur yang valid
            if (weatherData != null && weatherData.getLokasi() != null &&
                    weatherData.getData() != null && !weatherData.getData().isEmpty()) {

                // Ambil lokasi
                WeatherResponse.LocationInfo lokasi = weatherData.getLokasi();

                // Ambil data cuaca dari array 2D - ambil data terbaru (hari ini jam ini)
                WeatherResponse.DataItem dataItem = weatherData.getData().get(0);
                if (dataItem.getCuaca() != null && !dataItem.getCuaca().isEmpty() &&
                        !dataItem.getCuaca().get(0).isEmpty()) {

                    WeatherResponse.WeatherItem currentWeather = dataItem.getCuaca().get(0).get(0);

                    // Set data ke UI seperti di screenshot
                    textViewCity.setText(lokasi.getKotkab());
                    textViewTime.setText(formatLocalTime(currentWeather.getLocalDatetime()));
                    textViewWeatherCondition.setText(currentWeather.getWeatherDescription());
                    textViewTemperatureBig.setText(currentWeather.getTemperature() + "°");

                    // Format T:31° R:26° (suhu dan kelembaban)
                    String weatherDetails = String.format("T:%d° R:%d%%",
                            currentWeather.getTemperature(),
                            currentWeather.getHumidity());
                    textViewWeatherDetails.setText(weatherDetails);

                    // Tambahkan informasi peringatan jika ada hujan atau cuaca ekstrem
                    updateAlertInfo(currentWeather);

                    // Jika pengguna tunanetra, bacakan informasi cuaca
                    if (disabilityType == 1 && textToSpeech != null) {
                        String speechText = "Kondisi cuaca di " + lokasi.getKotkab() + " saat ini adalah " +
                                currentWeather.getWeatherDescription() +
                                " dengan suhu " + currentWeather.getTemperature() + " derajat celcius.";
                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_ADD, null, "weather_info");
                    }
                } else {
                    textViewCity.setText("Error");
                    textViewWeatherCondition.setText("Data tidak tersedia");
                    textViewAlertInfo.setText("Data cuaca tidak tersedia untuk lokasi ini.");
                }
            } else {
                textViewCity.setText("Error");
                textViewWeatherCondition.setText("Data tidak tersedia");
                textViewAlertInfo.setText("Data cuaca tidak tersedia saat ini.");
            }
        } catch (Exception e) {
            textViewCity.setText("Error");
            textViewWeatherCondition.setText("Terjadi kesalahan");
            textViewAlertInfo.setText("Terjadi kesalahan: " + e.getMessage());
            Log.e(TAG, "Error displaying weather data", e);
        }
    }

    private String formatLocalTime(String datetimeStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH.mm", new Locale("id", "ID"));
            Date date = inputFormat.parse(datetimeStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date: " + e.getMessage());
            return datetimeStr;
        }
    }

    private void showFragmentHideMainContent() {
        // Buat instance fragment tutorial
        TutorialFragment tutorialFragment = TutorialFragment.newInstance(disabilityType);

        // Dapatkan referensi view dari layout
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        ScrollView mainScrollView = findViewById(R.id.scrollViewMain);
        Button sosButton = findViewById(R.id.buttonSos);

        // Set visibility untuk fragment container
        contentFrame.setVisibility(View.VISIBLE);

        // Sembunyikan konten utama
        if (mainScrollView != null) mainScrollView.setVisibility(View.GONE);
        sosButton.setVisibility(View.GONE);

        // Tambahkan fragment ke container
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, tutorialFragment);
        transaction.addToBackStack("tutorial");
        transaction.commit();

        Toast.makeText(this, "Menampilkan tutorial mitigasi bencana", Toast.LENGTH_SHORT).show();
    }

    // Metode yang diperbaiki untuk menampilkan kembali konten utama
    private void showMainContent(boolean updateSelectedItem) {
        // Dapatkan referensi view dari layout
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        ScrollView mainScrollView = findViewById(R.id.scrollViewMain);
        Button sosButton = findViewById(R.id.buttonSos);

        // Sembunyikan fragment container
        contentFrame.setVisibility(View.GONE);

        // Tampilkan kembali konten utama
        if (mainScrollView != null) mainScrollView.setVisibility(View.VISIBLE);
        sosButton.setVisibility(View.VISIBLE);

        // Perbarui selected item hanya jika diminta
        if (updateSelectedItem) {
            isNavigatingProgrammatically = true;
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }

    // Overload untuk backward compatibility
    private void showMainContent() {
        showMainContent(true);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            showMainContent(true);
        } else {
            super.onBackPressed();
        }
    }

    private void updateAlertInfo(WeatherResponse.WeatherItem weather) {
        String weatherDesc = weather.getWeatherDescription().toLowerCase();

        if (weatherDesc.contains("hujan") ||
                weatherDesc.contains("petir") ||
                weatherDesc.contains("badai")) {

            String alertMessage = "PERINGATAN: Prakiraan cuaca menunjukkan " +
                    weather.getWeatherDescription() +
                    ". Harap waspada dan hindari keluar rumah jika tidak diperlukan.";

            textViewAlertInfo.setText(alertMessage);

            // Bacakan peringatan untuk pengguna tunanetra
            if (disabilityType == 1 && textToSpeech != null) {
                textToSpeech.speak(alertMessage, TextToSpeech.QUEUE_ADD, null, "alert_info");
            }
        } else if (weatherDesc.contains("berawan")) {
            textViewAlertInfo.setText("Kondisi cuaca berawan. Tidak ada peringatan khusus saat ini.");
        } else {
            textViewAlertInfo.setText("Tidak ada peringatan cuaca untuk saat ini.");
        }
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