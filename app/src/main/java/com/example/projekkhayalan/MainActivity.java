package com.example.projekkhayalan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private TextView textViewLastUpdated;

    private int disabilityType;
    private String selectedFeature;
    private TextToSpeech textToSpeech;
    private static final String TAG = "MainActivity";

    // Kode request untuk SOS
    private static final int REQUEST_SOS_CODE = 101;
    private boolean isSosReturn = false; // Flag untuk menandai kembali dari SOS

    private boolean isFirstLoad = true;
    private boolean isTtsInProgress = false;

    // Flag untuk menandai Fragment aktif
    private boolean isFragmentActive = false;

    private Handler weatherRefreshHandler = new Handler(Looper.getMainLooper());
    private Runnable weatherRefreshRunnable;
    private static final long WEATHER_REFRESH_INTERVAL = 300000;

    private Handler clockHandler = new Handler(Looper.getMainLooper());
    private Runnable clockRunnable;
    private static final long CLOCK_UPDATE_INTERVAL = 1000;

    private boolean useRealTimeClock = true; // true kalo mau gunakan jam real-time, false ambil dari api

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
        setupAccessibilityFeatures();
        loadWeatherData();
        setupWeatherAutoRefresh();

        if (useRealTimeClock) {
            startClock();
        }
    }

    private void initViews() {
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
            if (isNavigatingProgrammatically) {
                isNavigatingProgrammatically = false;
                return true;
            }

            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    showMainContent(false);
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("DISABILITY_TYPE", disabilityType);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_tutorial) {
                // Log untuk debugging
                Log.d("MainActivity", "Tutorial menu dipilih");
                // Pastikan fragment ditampilkan dengan benar
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
            // Gunakan startActivityForResult agar bisa mendeteksi ketika kembali dari SOS
            startActivityForResult(intent, REQUEST_SOS_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Cek apakah ini kembali dari SOS Activity
        if (requestCode == REQUEST_SOS_CODE && resultCode == RESULT_OK) {
            // Cek apakah dari SOS
            if (data != null && data.getBooleanExtra("FROM_SOS", false)) {
                isSosReturn = true;
                // Log untuk debug
                Log.d(TAG, "Kembali dari SOS Activity");
            }
        }
    }

    // Menambahkan fitur jam real-time jika diaktifkan
    private void startClock() {
        clockRunnable = new Runnable() {
            @Override
            public void run() {
                updateCurrentTime();
                clockHandler.postDelayed(this, CLOCK_UPDATE_INTERVAL);
            }
        };
        clockHandler.post(clockRunnable);
    }

    // Update jam real-time
    private void updateCurrentTime() {
        if (textViewTime != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm", new Locale("id", "ID"));
            String currentTime = timeFormat.format(new Date());
            textViewTime.setText(currentTime);
        }
    }

    private void setupWeatherAutoRefresh() {
        weatherRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Auto refresh data cuaca");
                isFirstLoad = false;
                loadWeatherData();
                // Jadwal refresh berikutnya setiap 5 menit
                weatherRefreshHandler.postDelayed(this, WEATHER_REFRESH_INTERVAL);
            }
        };
        weatherRefreshHandler.postDelayed(weatherRefreshRunnable, WEATHER_REFRESH_INTERVAL);
    }

    private void setupAccessibilityFeatures() {
        Log.d(TAG, "Setting up accessibility features, disability type: " + disabilityType);

        if (disabilityType == 1) { // Tunanetra
            Log.d(TAG, "Initializing TTS for blind users");
            textToSpeech = new TextToSpeech(this, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Fallback ke bahasa Inggris
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }

                    // Tambahkan listener untuk mendeteksi kapan TTS selesai
                    textToSpeech.setOnUtteranceProgressListener(new android.speech.tts.UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                            isTtsInProgress = true;
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            isTtsInProgress = false;
                        }

                        @Override
                        public void onError(String utteranceId) {
                            isTtsInProgress = false;
                        }
                    });

                    speakWelcome();
                }
            });
        } else {
            // Pastikan textToSpeech adalah null untuk jenis disabilitas selain tunanetra
            Log.d(TAG, "TTS disabled for non-blind users (type: " + disabilityType + ")");
            textToSpeech = null;
            isTtsInProgress = false;
        }

        // Penyesuaian UI untuk disabilitas lainnya
        if (disabilityType == 2) { // Tunarungu
            buttonSos.setPadding(12, 12, 12, 12);
        }

        if (disabilityType == 3) { // Tunadaksa
            buttonSos.setPadding(12, 12, 12, 12);
        }
    }

    private void speakWelcome() {
        if (textToSpeech != null && disabilityType == 1) {
            String welcomeMessage = "Selamat datang di aplikasi. ";
            textToSpeech.speak(welcomeMessage, TextToSpeech.QUEUE_FLUSH, null, "welcome");
        }
    }

    private void loadWeatherData() {
        if (textViewCity.getText().equals("Error")) {
            textViewCity.setText("Memuat...");
            if (!useRealTimeClock) {
                textViewTime.setText("");
            }
            textViewWeatherCondition.setText("Memuat data cuaca");
            textViewTemperatureBig.setText("--°");
            textViewWeatherDetails.setText("T:--° R:--%");
            textViewAlertInfo.setText("Memuat informasi peringatan...");
        }

        WeatherApiService apiService = WeatherApiClient.getWeatherService();

        Call<WeatherResponse> call = apiService.getWeatherForecast("73.71.14.1001");

        // Log waktu request
        Log.d(TAG, "Memanggil API cuaca: " + new Date().toString());

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();
                    displayWeatherData(weatherData);

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", new Locale("id", "ID"));
                    String currentTime = sdf.format(new Date());

                    Log.d(TAG, "API berhasil dimuat: " + response.code());
                } else {
                    textViewCity.setText("Error");
                    textViewWeatherCondition.setText("Gagal memuat data cuaca");
                    textViewAlertInfo.setText("Gagal memuat data cuaca. Kode: " + response.code());
                    Log.e(TAG, "Error: " + response.message() + ", kode: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textViewCity.setText("Error");
                textViewWeatherCondition.setText("Koneksi gagal");
                String errorMsg = "Gagal terhubung ke server BMKG. Periksa koneksi Anda.";
                if (t.getMessage() != null) {
                    errorMsg += "\nDetail error: " + t.getMessage();
                }
                textViewAlertInfo.setText(errorMsg);
                Log.e(TAG, "Failure: " + t.getMessage(), t);
                t.printStackTrace();
            }
        });
    }

    private void displayWeatherData(WeatherResponse weatherData) {
        try {
            if (weatherData != null && weatherData.getLokasi() != null &&
                    weatherData.getData() != null && !weatherData.getData().isEmpty()) {

                WeatherResponse.LocationInfo lokasi = weatherData.getLokasi();

                WeatherResponse.DataItem dataItem = weatherData.getData().get(0);
                if (dataItem.getCuaca() != null && !dataItem.getCuaca().isEmpty() &&
                        !dataItem.getCuaca().get(0).isEmpty()) {

                    WeatherResponse.WeatherItem currentWeather = dataItem.getCuaca().get(0).get(0);

                    textViewCity.setText(lokasi.getKotkab());

                    if (!useRealTimeClock) {
                        textViewTime.setText(formatLocalTime(currentWeather.getLocalDatetime()));
                    }

                    textViewWeatherCondition.setText(currentWeather.getWeatherDescription());
                    textViewTemperatureBig.setText(currentWeather.getTemperature() + "°");

                    // Format T:31° R:26° (suhu dan kelembaban)
                    String weatherDetails = String.format("T:%d° R:%d%%",
                            currentWeather.getTemperature(),
                            currentWeather.getHumidity());
                    textViewWeatherDetails.setText(weatherDetails);

                    updateAlertInfo(currentWeather);

                    // Log waktu update data terakhir
                    Log.d(TAG, "Data cuaca diperbarui pada: " + new Date().toString());

                    // Jangan bacakan informasi cuaca jika kembali dari SOS atau fragment aktif
                    if (!isSosReturn && disabilityType == 1 && textToSpeech != null &&
                            (isFirstLoad || !isTtsInProgress) && !isFragmentActive) {

                        String speechText = "Kondisi cuaca di " + lokasi.getKotkab() + " saat ini adalah " +
                                currentWeather.getWeatherDescription() +
                                " dengan suhu " + currentWeather.getTemperature() + " derajat celcius.";
                        textToSpeech.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "weather_info");

                        // Tandai bahwa load pertama telah selesai
                        isFirstLoad = false;
                    } else if (isSosReturn) {
                        // Reset flag SOS return setelah digunakan
                        isSosReturn = false;
                        isFirstLoad = false;
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
        // Log untuk debugging
        Log.d("MainActivity", "Mencoba menampilkan tutorial fragment");

        TutorialFragment tutorialFragment = TutorialFragment.newInstance(disabilityType);

        // Dapatkan referensi view dari layout
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        if(contentFrame == null) {
            Log.e("MainActivity", "content_frame tidak ditemukan dalam layout!");
            Toast.makeText(this, "Terjadi kesalahan teknis", Toast.LENGTH_SHORT).show();
            return;
        }

        ScrollView mainScrollView = findViewById(R.id.scrollViewMain);
        Button sosButton = findViewById(R.id.buttonSos);

        // Set visibility untuk fragment container
        contentFrame.setVisibility(View.VISIBLE);

        // Sembunyikan konten utama
        if (mainScrollView != null) mainScrollView.setVisibility(View.GONE);
        sosButton.setVisibility(View.GONE);

        // Set flag fragment active to true
        isFragmentActive = true;

        // Tambahkan fragment ke container
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, tutorialFragment);
        transaction.addToBackStack("tutorial");
        transaction.commit();

        Toast.makeText(this, "Menampilkan tutorial mitigasi bencana", Toast.LENGTH_SHORT).show();
    }

    private void showMainContent(boolean updateSelectedItem) {
        // Dapatkan referensi view dari layout
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        ScrollView mainScrollView = findViewById(R.id.scrollViewMain);
        Button sosButton = findViewById(R.id.buttonSos);

        // Sembunyikan fragment container
        contentFrame.setVisibility(View.GONE);

        // Set flag fragment active to false
        isFragmentActive = false;

        // Tampilkan kembali konten utama
        if (mainScrollView != null) mainScrollView.setVisibility(View.VISIBLE);
        sosButton.setVisibility(View.VISIBLE);

        // Perbarui selected item hanya jika diminta
        if (updateSelectedItem) {
            isNavigatingProgrammatically = true;
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

            // Jangan jalankan TTS "Kembali ke beranda" jika kembali dari SOS atau bukan tunanetra
            if (!isSosReturn && disabilityType == 1 && textToSpeech != null && !isTtsInProgress) {
                textToSpeech.speak("Kembali ke beranda", TextToSpeech.QUEUE_FLUSH, null, "back_to_home");
            }
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

            // Jangan bacakan peringatan jika kembali dari SOS atau fragment aktif
            if (!isSosReturn && disabilityType == 1 && textToSpeech != null &&
                    !isTtsInProgress && !isFirstLoad && !isFragmentActive) {
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
        // Bersihkan handler auto-refresh
        if (weatherRefreshHandler != null && weatherRefreshRunnable != null) {
            weatherRefreshHandler.removeCallbacks(weatherRefreshRunnable);
        }

        // Bersihkan handler jam
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Update clock jika diperlukan
        if (useRealTimeClock && clockHandler != null && clockRunnable != null) {
            clockHandler.post(clockRunnable);
        }

        // Hanya muat ulang data cuaca jika ini bukan pertama kali,
        // bukan kembali dari SOS, dan fragment tidak aktif
        if (!isFirstLoad && !isSosReturn && !isFragmentActive) {
            loadWeatherData();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (clockHandler != null && clockRunnable != null) {
            clockHandler.removeCallbacks(clockRunnable);
        }
    }
}