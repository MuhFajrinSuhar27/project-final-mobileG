package com.example.projekkhayalan.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projekkhayalan.R;
import com.example.projekkhayalan.adapters.MitigasiStepAdapter;
import com.example.projekkhayalan.models.MitigasiStep;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MitigasiDetailActivity extends AppCompatActivity implements MitigasiStepAdapter.MitigasiStepListener {


    private ImageView imageViewBencana;
    private TextView textViewMitigasiDetailTitle;
    private TextView textViewGeneralInfo;
    private RecyclerView recyclerViewMitigasiSteps;
    private ExtendedFloatingActionButton buttonEmergencyContact;
    private ProgressBar progressBarLoading;

    private MitigasiStepAdapter adapter;
    private String disasterType;
    private int disabilityType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitigasi_detail);

        initViews();

        
        setupToolbar();
        getIntentData();
        setupRecyclerView();
        loadMitigasiData();
        setupClickListeners();
    }

    private void initViews() {

        imageViewBencana = findViewById(R.id.imageViewBencana);
        textViewMitigasiDetailTitle = findViewById(R.id.textViewMitigasiDetailTitle);
        textViewGeneralInfo = findViewById(R.id.textViewGeneralInfo);
        recyclerViewMitigasiSteps = findViewById(R.id.recyclerViewMitigasiSteps);
        buttonEmergencyContact = findViewById(R.id.buttonEmergencyContact);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        // Set title menjadi kosong untuk menghilangkan "Siaga Difabel"
        collapsingToolbar.setTitle("");


    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void getIntentData() {
        disasterType = getIntent().getStringExtra("DISASTER_TYPE");
        disabilityType = getIntent().getIntExtra("DISABILITY_TYPE", 0);

        if (disasterType == null) {
            disasterType = "Bencana Umum";
        }
    }

    private void setupRecyclerView() {
        recyclerViewMitigasiSteps.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMitigasiSteps.setHasFixedSize(true);
    }

    private void loadMitigasiData() {
        // Set title and image based on disaster type
        String title = "Mitigasi " + disasterType;

        textViewMitigasiDetailTitle.setText(title);

        // Set disaster image based on type
        setDisasterImage(disasterType);

        // Set general information
        setGeneralInfo(disasterType);

        // Load mitigation steps
        List<MitigasiStep> steps = getMitigasiSteps(disasterType);
        adapter = new MitigasiStepAdapter(steps, disabilityType, this);
        recyclerViewMitigasiSteps.setAdapter(adapter);
    }

    private void setDisasterImage(String disasterType) {
        int imageResource = R.drawable.ic_launcher_background; // default image

        switch (disasterType.toLowerCase()) {
            case "banjir":
                imageResource = R.drawable.ic_launcher_background; // ganti dengan image banjir
                break;
            case "gempa bumi":
                imageResource = R.drawable.ic_launcher_background; // ganti dengan image gempa
                break;
            case "tsunami":
                imageResource = R.drawable.ic_launcher_background; // ganti dengan image tsunami
                break;
            case "gunung berapi":
                imageResource = R.drawable.ic_launcher_background; // ganti dengan image gunung berapi
                break;
            case "kebakaran":
                imageResource = R.drawable.ic_launcher_background; // ganti dengan image kebakaran
                break;
        }

        imageViewBencana.setImageResource(imageResource);
    }

    private void setGeneralInfo(String disasterType) {
        String info = "";

        switch (disasterType.toLowerCase()) {
            case "banjir":
                info = "Banjir adalah peristiwa yang terjadi ketika aliran air yang berlebihan merendam daratan. Persiapan dan tindakan yang tepat dapat mengurangi risiko kerugian.";
                break;
            case "gempa bumi":
                info = "Gempa bumi adalah getaran atau guncangan yang terjadi di permukaan bumi akibat pelepasan energi dari dalam bumi secara tiba-tiba.";
                break;
            case "tsunami":
                info = "Tsunami adalah gelombang laut raksasa yang disebabkan oleh gempa bumi, letusan gunung berapi, atau longsor di dasar laut.";
                break;
            case "gunung berapi":
                info = "Letusan gunung berapi dapat mengeluarkan lava, abu vulkanik, dan gas beracun yang berbahaya bagi kehidupan di sekitarnya.";
                break;
            case "kebakaran":
                info = "Kebakaran dapat menyebar dengan cepat dan mengancam jiwa. Pencegahan dan tindakan cepat sangat penting untuk keselamatan.";
                break;
            default:
                info = "Bencana alam dapat terjadi kapan saja. Persiapan yang baik dan pengetahuan tentang mitigasi dapat menyelamatkan nyawa.";
                break;
        }

        textViewGeneralInfo.setText(info);
    }

    private List<MitigasiStep> getMitigasiSteps(String disasterType) {
        List<MitigasiStep> steps = new ArrayList<>();

        switch (disasterType.toLowerCase()) {
            case "banjir":
                steps.add(new MitigasiStep("Sebelum Banjir",
                        "Kenali jalur evakuasi terdekat\n• Simpan barang berharga di tempat tinggi\n• Ikuti informasi cuaca dan peringatan banjir",
                        R.drawable.ic_launcher_background, 1));

                steps.add(new MitigasiStep("Saat Banjir",
                        "• Jauhi area banjir dan arus deras\n• Naik ke tempat yang lebih tinggi\n• Hubungi petugas darurat jika diperlukan",
                        R.drawable.ic_launcher_background, 2));

                steps.add(new MitigasiStep("Setelah Banjir",
                        "• Pastikan keamanan sebelum kembali ke rumah\n• Bersihkan rumah dan periksa kerusakan\n• Laporkan kerusakan infrastruktur",
                        R.drawable.ic_launcher_background, 3));
                break;

            case "gempa bumi":
                steps.add(new MitigasiStep("Sebelum Gempa",
                        "• Identifikasi tempat aman di rumah (meja kokoh, kusen pintu)\n• Siapkan tas darurat\n• Amankan barang-barang yang mudah jatuh\n• Pelajari cara mematikan gas, listrik, dan air",
                        R.drawable.ic_launcher_background, 1));

                steps.add(new MitigasiStep("Saat Gempa",
                        "• COVER: Berlindung di bawah meja kokoh\n• HOLD ON: Pegang sampai guncangan berhenti\n• Jauhi jendela dan barang yang bisa jatuh",
                        R.drawable.ic_launcher_background, 2));

                steps.add(new MitigasiStep("Setelah Gempa",
                        "• Periksa luka dan berikan pertolongan pertama\n• Periksa kebocoran gas dan kerusakan struktural\n• Siap menghadapi gempa susulan\n• Ikuti instruksi petugas darurat",
                        R.drawable.ic_launcher_background, 3));
                break;

            default:
                steps.add(new MitigasiStep("Persiapan Umum",
                        "• Siapkan tas darurat\n• Kenali jalur evakuasi\n• Simpan kontak darurat\n• Ikuti informasi resmi",
                        R.drawable.ic_launcher_background, 1));
                break;
        }

        return steps;
    }

    private void setupClickListeners() {
        buttonEmergencyContact.setOnClickListener(v -> {
            Toast.makeText(this, "Membuka kontak darurat...", Toast.LENGTH_SHORT).show();

            // Buat dialog untuk kontak darurat
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Kontak Darurat");

            // Buat array untuk kontak darurat
            final String[] emergencyContacts = {
                "Nomor Darurat Nasional",
                "Pemadam Kebakaran",
                "Polisi"
            };

            // Set onClick listener untuk setiap kontak
            builder.setItems(emergencyContacts, (dialog, which) -> {
                String selectedContact = emergencyContacts[which];
                String phoneNumber = "";

                // Tentukan nomor telepon berdasarkan kontak yang dipilih
                switch (which) {
                    case 0: // Nomor Darurat Nasional
                        phoneNumber = "112";
                        break;
                    case 1: // Pemadam Kebakaran
                        phoneNumber = "113";
                        break;
                    case 2: // Polisi
                        phoneNumber = "110";
                        break;
                }

                // Lakukan panggilan ke nomor kontak (perlu izin)
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            });

            // Tambahkan tombol batal
            builder.setNegativeButton("BATAL", null);

            // Tampilkan dialog
            builder.show();
        });
    }

    @Override
    public void onMitigasiStepClicked(MitigasiStep step) {
        Toast.makeText(this, "Langkah: " + step.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReadMoreClicked(MitigasiStep step) {
        Toast.makeText(this, "Membaca detail: " + step.getTitle(), Toast.LENGTH_SHORT).show();
        // Implementasi untuk menampilkan detail lengkap
    }

    @Override
    public void onListenAudioClicked(MitigasiStep step) {
        Toast.makeText(this, "Memutar audio: " + step.getTitle(), Toast.LENGTH_SHORT).show();
        // Implementasi untuk memutar audio instruksi
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}