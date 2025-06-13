package com.example.projekkhayalan.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekkhayalan.R;
import com.example.projekkhayalan.adapters.MitigasiStepAdapter;
import com.example.projekkhayalan.models.MitigasiStep;
import com.example.projekkhayalan.utils.AccessibilityHelper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MitigasiDetailActivity extends AppCompatActivity implements MitigasiStepAdapter.MitigasiStepListener {

    private TextView textViewTitle;
    private TextView textViewGeneralInfo;
    private ImageView imageViewBencana;
    private RecyclerView recyclerViewSteps;
    private ProgressBar progressBarLoading;
    private ExtendedFloatingActionButton buttonEmergencyContact;

    private String disasterType;
    private int disabilityType;
    private TextToSpeech textToSpeech;
    private AccessibilityHelper accessibilityHelper;
    private MitigasiStepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitigasi_detail);

        // Get passed data
        disasterType = getIntent().getStringExtra("DISASTER_TYPE");
        disabilityType = getIntent().getIntExtra("DISABILITY_TYPE", 1);

        initViews();
        setupToolbar();
        setupAccessibilityFeatures();
        showLoading();

        // Simulate loading process
        new Handler().postDelayed(() -> {
            loadMitigasiContent();
            hideLoading();
            showContentWithAnimation();
        }, 1000);

        setupEmergencyButton();
    }

    private void initViews() {
        textViewTitle = findViewById(R.id.textViewMitigasiDetailTitle);
        textViewGeneralInfo = findViewById(R.id.textViewGeneralInfo);
        imageViewBencana = findViewById(R.id.imageViewBencana);
        recyclerViewSteps = findViewById(R.id.recyclerViewMitigasiSteps);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        buttonEmergencyContact = findViewById(R.id.buttonEmergencyContact);

        recyclerViewSteps.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void setupAccessibilityFeatures() {
        accessibilityHelper = new AccessibilityHelper(this, disabilityType);
        accessibilityHelper.registerTextView(textViewTitle);
        accessibilityHelper.registerTextView(textViewGeneralInfo);

        if (disabilityType == 1) { // Tunanetra (visually impaired)
            setupTextToSpeech();
        }

        accessibilityHelper.adjustUI();

        // Additional accessibility adjustments based on disability type
        if (disabilityType == 4) { // Tunagrahita (intellectually disabled)
            textViewTitle.setTextSize(32);
        }
    }

    private void setupTextToSpeech() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
                speakMitigasiDetail();
            }
        });
    }

    private void showLoading() {
        progressBarLoading.setVisibility(View.VISIBLE);
        recyclerViewSteps.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBarLoading.setVisibility(View.GONE);
        recyclerViewSteps.setVisibility(View.VISIBLE);
    }

    private void showContentWithAnimation() {
        recyclerViewSteps.setAlpha(0f);
        recyclerViewSteps.setTranslationY(300);
        recyclerViewSteps.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(500)
                .setListener(null);
    }

    private void loadMitigasiContent() {
        textViewTitle.setText("MITIGASI " + disasterType.toUpperCase());

        // Set general info based on disaster type
        setGeneralInfo();

        // Set image based on disaster type - using default drawables for now
        switch (disasterType) {
            case "Banjir":
                imageViewBencana.setImageResource(android.R.drawable.ic_dialog_info);
                break;
            case "Banjir Bandang":
                imageViewBencana.setImageResource(android.R.drawable.ic_dialog_alert);
                break;
            case "Gempa Bumi":
                imageViewBencana.setImageResource(android.R.drawable.ic_dialog_dialer);
                break;
            case "Tanah Longsor":
                imageViewBencana.setImageResource(android.R.drawable.ic_dialog_map);
                break;
        }

        // Load and display mitigation steps
        List<MitigasiStep> steps = getMitigasiSteps(disasterType);
        adapter = new MitigasiStepAdapter(steps, disabilityType, this);
        recyclerViewSteps.setAdapter(adapter);
    }

    private void setGeneralInfo() {
        String info = "";
        switch (disasterType) {
            case "Banjir":
                info = "Banjir adalah peristiwa yang terjadi ketika air menggenangi daratan yang biasanya kering. Ini terjadi karena curah hujan tinggi, luapan sungai, atau masalah drainase. Kenali tanda-tanda awal dan ikuti panduan evakuasi yang disarankan.";
                break;
            case "Banjir Bandang":
                info = "Banjir bandang adalah banjir dahsyat yang terjadi secara tiba-tiba dengan arus deras. Biasanya karena hujan lebat di dataran tinggi. Sangat berbahaya dan dapat menyapu bersih apa saja di jalurnya. Evakuasi segera ke tempat tinggi.";
                break;
            case "Gempa Bumi":
                info = "Gempa bumi adalah getaran atau guncangan yang terjadi di permukaan bumi akibat pelepasan energi dari dalam secara tiba-tiba. Jangan panik dan berlindung di tempat aman saat terjadi gempa.";
                break;
            case "Tanah Longsor":
                info = "Tanah longsor terjadi saat massa tanah bergerak menuruni lereng karena ketidakstabilan. Biasanya dipicu oleh hujan deras, gempa bumi, atau aktivitas manusia. Kenali tanda-tanda seperti retakan tanah dan pohon miring.";
                break;
        }
        textViewGeneralInfo.setText(info);
    }

    private void setupEmergencyButton() {
        buttonEmergencyContact.setOnClickListener(v -> {
            // Create circular reveal animation
            int cx = buttonEmergencyContact.getWidth() / 2;
            int cy = buttonEmergencyContact.getHeight() / 2;
            float finalRadius = (float) Math.hypot(cx, cy);

            Animator anim = ViewAnimationUtils.createCircularReveal(buttonEmergencyContact, cx, cy, finalRadius, 0);
            anim.setDuration(300);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    showEmergencyContactOptions();
                    buttonEmergencyContact.setVisibility(View.VISIBLE);

                    // Animate button back
                    Animator revealAnim = ViewAnimationUtils.createCircularReveal(
                            buttonEmergencyContact, cx, cy, 0, finalRadius);
                    revealAnim.setDuration(300);
                    buttonEmergencyContact.setVisibility(View.VISIBLE);
                    revealAnim.start();
                }
            });
            anim.start();
        });
    }

    private void showEmergencyContactOptions() {
        String[] emergencyNumbers = {"112", "119", "113", "110"};
        String[] emergencyLabels = {"Nomor Darurat Nasional", "Pemadam Kebakaran", "SAR/BASARNAS", "Polisi"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Kontak Darurat");

        builder.setItems(emergencyLabels, (dialog, which) -> {
            String number = emergencyNumbers[which];
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + number));
            startActivity(intent);
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private List<MitigasiStep> getMitigasiSteps(String disasterType) {
        List<MitigasiStep> steps = new ArrayList<>();

        switch (disasterType) {
            case "Banjir":
                steps.add(new MitigasiStep("Sebelum Banjir",
                        "• Siapkan tas darurat berisi barang penting\n" +
                                "• Kenali jalur evakuasi terdekat\n" +
                                "• Simpan barang berharga di tempat tinggi\n" +
                                "• Ikuti informasi cuaca dan peringatan banjir",
                        android.R.drawable.ic_menu_compass, 1));

                steps.add(new MitigasiStep("Saat Banjir",
                        "• Matikan listrik dari sumber utama\n" +
                                "• Jangan menyentuh air banjir (bisa beraliran listrik)\n" +
                                "• Segera evakuasi ke tempat yang lebih tinggi\n" +
                                "• Hindari berjalan atau menyetir di air banjir",
                        android.R.drawable.ic_menu_call, 2));

                steps.add(new MitigasiStep("Setelah Banjir",
                        "• Bersihkan rumah dari lumpur dan kotoran\n" +
                                "• Waspada terhadap air yang masih tergenang\n" +
                                "• Periksa struktur bangunan sebelum masuk\n" +
                                "• Buang makanan yang terkena air banjir",
                        android.R.drawable.ic_menu_help, 3));
                break;

            case "Banjir Bandang":
                steps.add(new MitigasiStep("Tanda-tanda Bahaya",
                        "• Hujan deras berkepanjangan di daerah hulu\n" +
                                "• Air sungai mendadak keruh dengan banyak sampah\n" +
                                "• Terdengar suara gemuruh dari arah hulu\n" +
                                "• Peningkatan volume air sungai secara drastis",
                        android.R.drawable.ic_dialog_alert, 1));

                steps.add(new MitigasiStep("Tindakan Cepat",
                        "• Segera lari ke tempat yang lebih tinggi\n" +
                                "• Prioritaskan keselamatan, tinggalkan barang-barang\n" +
                                "• Jangan menyeberangi aliran air\n" +
                                "• Bantu orang lain yang kesulitan bergerak",
                        android.R.drawable.ic_menu_directions, 2));

                steps.add(new MitigasiStep("Pencegahan",
                        "• Hindari tinggal di dekat aliran sungai di lereng gunung\n" +
                                "• Jangan membuang sampah ke sungai\n" +
                                "• Ikuti peringatan cuaca dan informasi dari BMKG\n" +
                                "• Berpartisipasi dalam kegiatan reboisasi",
                        android.R.drawable.ic_menu_info_details, 3));
                break;

            case "Gempa Bumi":
                steps.add(new MitigasiStep("Di Dalam Ruangan",
                        "• Berlindung di bawah meja yang kokoh\n" +
                                "• Lindungi kepala dan leher dengan tangan\n" +
                                "• Jauhi jendela dan barang yang bisa jatuh\n" +
                                "• Tetap di dalam sampai guncangan berhenti",
                        android.R.drawable.ic_menu_sort_by_size, 1));

                steps.add(new MitigasiStep("Di Luar Ruangan",
                        "• Jauhi bangunan, pohon, dan kabel listrik\n" +
                                "• Carilah tempat terbuka yang luas\n" +
                                "• Lindungi kepala dengan tas atau tangan\n" +
                                "• Waspada terhadap benda-benda yang berjatuhan",
                        android.R.drawable.ic_menu_mylocation, 2));

                steps.add(new MitigasiStep("Setelah Gempa",
                        "• Periksa kondisi diri dan orang sekitar\n" +
                                "• Waspada terhadap gempa susulan\n" +
                                "• Periksa kebocoran gas dan kerusakan listrik\n" +
                                "• Hindari menggunakan lift",
                        android.R.drawable.ic_menu_manage, 3));
                break;

            case "Tanah Longsor":
                steps.add(new MitigasiStep("Tanda-tanda Awal",
                        "• Retakan pada tanah atau bangunan\n" +
                                "• Pohon yang miring atau tumbang\n" +
                                "• Air muncul dari lereng atau air keruh\n" +
                                "• Bunyi gemuruh dan getaran",
                        android.R.drawable.ic_dialog_alert, 1));

                steps.add(new MitigasiStep("Saat Terjadi",
                        "• Segera evakuasi menjauh dari arah longsor\n" +
                                "• Bergerak ke area terbuka yang lebih tinggi\n" +
                                "• Peringatkan orang lain di sekitar\n" +
                                "• Jika terjebak, jaga posisi meringkuk",
                        android.R.drawable.ic_menu_send, 2));

                steps.add(new MitigasiStep("Pencegahan",
                        "• Hindari tinggal di lereng curam\n" +
                                "• Tanam tumbuhan dengan akar kuat di lereng\n" +
                                "• Buat sistem drainase yang baik\n" +
                                "• Perhatikan peringatan cuaca ekstrem",
                        android.R.drawable.ic_menu_edit, 3));
                break;
        }

        return steps;
    }

    private void speakMitigasiDetail() {
        if (textToSpeech != null && disabilityType == 1) {
            String infoToSpeak = "";

            switch (disasterType) {
                case "Banjir":
                    infoToSpeak = "Mitigasi banjir. Sebelum banjir: Siapkan tas darurat, kenali jalur evakuasi, " +
                            "simpan barang di tempat tinggi. Saat banjir: Matikan listrik, jangan sentuh air, " +
                            "segera evakuasi ke tempat tinggi. Setelah banjir: Bersihkan rumah dari lumpur dan air tergenang.";
                    break;

                case "Banjir Bandang":
                    infoToSpeak = "Mitigasi banjir bandang. Perhatikan tanda bahaya: Hujan deras berkepanjangan, " +
                            "air sungai keruh dengan banyak sampah, suara gemuruh. Tindakan cepat: Segera lari ke tempat tinggi, " +
                            "tinggalkan barang-barang, jangan menyeberangi aliran air.";
                    break;

                case "Gempa Bumi":
                    infoToSpeak = "Mitigasi gempa bumi. Di dalam ruangan: Berlindung di bawah meja kokoh, " +
                            "lindungi kepala dan leher. Di luar ruangan: Jauhi bangunan, pohon, dan kabel listrik, " +
                            "cari tempat terbuka. Setelah gempa: Periksa kondisi diri dan waspada gempa susulan.";
                    break;

                case "Tanah Longsor":
                    infoToSpeak = "Mitigasi tanah longsor. Tanda-tanda awal: Retakan tanah, pohon miring, " +
                            "air keruh, bunyi gemuruh. Saat terjadi: Segera evakuasi menjauh dari arah longsor, " +
                            "bergerak ke area terbuka yang lebih tinggi.";
                    break;
            }

            textToSpeech.speak(infoToSpeak, TextToSpeech.QUEUE_FLUSH, null, "mitigasi_detail");
        }
    }

    @Override
    public void onMitigasiStepClicked(MitigasiStep step) {
        // Handle step click - show detail dialog with animations
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(step.getTitle());
        builder.setMessage(step.getDescription());
        builder.setPositiveButton("Tutup", null);

        if (disabilityType == 1) { // Tunanetra
            if (textToSpeech != null) {
                textToSpeech.speak(step.getTitle() + ". " + step.getDescription(),
                        TextToSpeech.QUEUE_FLUSH, null, "step_detail");
            }
        }

        builder.show();
    }

    @Override
    public void onReadMoreClicked(MitigasiStep step) {
        Snackbar.make(recyclerViewSteps, "Informasi lengkap akan tersedia segera", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onListenAudioClicked(MitigasiStep step) {
        // In a real app, you would play audio instructions
        if (textToSpeech != null) {
            textToSpeech.speak(step.getTitle() + ". " + step.getDescription(),
                    TextToSpeech.QUEUE_FLUSH, null, "step_audio");

            Toast.makeText(this, "Memutar audio untuk " + step.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // Add exit animation
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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