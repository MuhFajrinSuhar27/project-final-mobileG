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
            disasterType = "bencana umum";
        } else {
            disasterType = disasterType.trim().toLowerCase(); // ✅ normalisasi ulang
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

        // Set general information - juga bisa dimodifikasi untuk menerima disabilityType jika perlu
        setGeneralInfo(disasterType);

        // Load mitigation steps dengan parameter disabilityType
        List<MitigasiStep> steps = getMitigasiSteps(disasterType, disabilityType);
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
            case "banjir bandang":
                imageResource = R.drawable.ic_launcher_background; // ganti dengan gambar banjir bandang
                break;
            case "tanah longsor":
                imageResource = R.drawable.ic_launcher_background; // ganti dengan gambar tanah longsor
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
            case "banjir bandang":
                info = "Banjir bandang adalah jenis banjir yang terjadi dengan sangat cepat dengan volume air yang sangat besar dan deras. Biasanya disebabkan oleh hujan lebat atau jebolnya bendungan/tanggul.";
                break;
            case "tanah longsor":
                info = "Tanah longsor adalah pergerakan massa tanah, batuan, atau campuran keduanya yang bergerak menuruni lereng akibat gangguan kestabilan lereng. Sering dipicu oleh hujan deras atau gempa.";
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

    private List<MitigasiStep> getMitigasiSteps(String disasterType, int disabilityType) {
        List<MitigasiStep> steps = new ArrayList<>();

        // Langkah-langkah mitigasi berdasarkan jenis disabilitas
        switch (disabilityType) {
            case 1: // TUNANETRA
                if (disasterType.equalsIgnoreCase("banjir")) {
                    steps.add(new MitigasiStep("Sebelum Banjir - Tunanetra",
                            "• Hafal rute evakuasi dengan menghitung langkah dan meraba penanda khusus\n" +
                                    "• Siapkan tas darurat dengan braille atau penanda taktil\n" +

                                    "• Tentukan orang yang akan membantu Anda saat evakuasi",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir - Tunanetra",
                            "• Gunakan tongkat untuk mendeteksi kedalaman air\n" +
                                    "• Fokus pada suara untuk petunjuk arah dan instruksi\n" +
                                    "• Tetap dekat dengan dinding untuk orientasi\n" +
                                    "• Minta bantuan orang lain untuk navigasi",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir - Tunanetra",
                            "• Berhati-hati dengan reruntuhan yang mungkin tidak terdeteksi\n" +
                                    "• Minta bantuan untuk inspeksi rumah\n" +
                                    "• Perhatikan bau gas dan suara bocoran air",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("gempa bumi")) {
                    steps.add(new MitigasiStep("Sebelum Gempa - Tunanetra",
                            "• Hafalkan tata letak ruangan dan rute evakuasi dengan meraba\n" +
                                    "• Pasang penanda taktil/suara pada jalur evakuasi\n" +
                                    "• Susun perabotan agar tidak menghalangi jalur evakuasi\n" +
                                    "• Simpan tongkat cadangan di beberapa lokasi strategis",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Gempa - Tunanetra",
                            "• Berlindung di bawah meja kuat dan jauh dari kaca\n" +
                                    "• Gunakan tangan untuk melindungi kepala dan leher\n" +
                                    "• Deteksi suara retakan dan perubahan struktur bangunan\n" +
                                    "• Tunggu instruksi verbal atau minta bantuan setelah guncangan berhenti",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Gempa - Tunanetra",
                            "• Gunakan tongkat untuk mendeteksi reruntuhan dan benda jatuh\n" +
                                    "• Ikuti dinding dan gunakan teknik pelindung saat bergerak\n" +
                                    "• Berkomunikasi dengan suara jika terperangkap\n" +
                                    "• Minta bantuan untuk evakuasi dan inspeksi kerusakan",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("banjir bandang")) {
                    steps.add(new MitigasiStep("Sebelum Banjir Bandang - Tunanetra",
                            "• Hafalkan rute evakuasi dengan petunjuk taktil dan suara\n" +
                                    "• Siapkan sistem peringatan suara yang keras\n" +
                                    "• Kenali suara-suara alam seperti gemuruh air sebagai tanda\n" +
                                    "• Simpan tongkat navigasi di beberapa lokasi strategis",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir Bandang - Tunanetra",
                            "• Segera bergerak ke lokasi tinggi saat mendengar peringatan\n" +
                                    "• Fokus pada pendengaran untuk mendeteksi arah air\n" +
                                    "• Gunakan tongkat untuk merasakan perubahan aliran air\n" +
                                    "• Panggil bantuan dengan suara keras atau peluit",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir Bandang - Tunanetra",
                            "• Tetap di tempat aman hingga ada konfirmasi verbal\n" +
                                    "• Gunakan tongkat untuk mendeteksi lubang dan puing berbahaya\n" +
                                    "• Minta bantuan untuk navigasi area yang terdampak\n" +
                                    "• Perhatikan suara retakan atau longsor susulan",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("tanah longsor")) {
                    steps.add(new MitigasiStep("Sebelum Tanah Longsor - Tunanetra",
                            "• Kenali tanda-tanda awal longsor: suara retakan, gemuruh\n" +
                                    "• Pasang sensor getaran dengan peringatan suara\n" +
                                    "• Hafal jalur evakuasi dengan menghitung langkah\n" +
                                    "• Siapkan tongkat navigasi tahan benturan",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Tanah Longsor - Tunanetra",
                            "• Evakuasi horizontal (menyamping) bukan ke bawah lereng\n" +
                                    "• Kendalikan kepanikan dengan fokus pada pendengaran\n" +
                                    "• Lindungi kepala dan area vital dari puing\n" +
                                    "• Gunakan suara untuk memberitahu lokasi Anda",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Tanah Longsor - Tunanetra",
                            "• Gunakan tongkat untuk mendeteksi ketidakstabilan tanah\n" +
                                    "• Hindari area yang masih bersuara gemuruh atau bergerak\n" +
                                    "• Minta bantuan untuk navigasi area berbahaya\n" +
                                    "• Laporkan lokasi Anda dengan deskripsi suara yang jelas",
                            R.drawable.ic_launcher_background, 3));
                }
                break;

            case 2: // TUNARUNGU
                if (disasterType.equalsIgnoreCase("banjir")) {
                    steps.add(new MitigasiStep("Sebelum Banjir - Tunarungu",
                            "• Pasang sistem peringatan bencana dengan lampu berkedip\n" +
                                    "• Simpan ponsel tahan air untuk komunikasi darurat\n" +
                                    "• Buat rencana evakuasi visual dengan peta dan simbol\n" +
                                    "• Siapkan kartu komunikasi untuk meminta bantuan",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir - Tunarungu",
                            "• Perhatikan pergerakan air dan reaksi orang lain\n" +
                                    "• Gunakan senter untuk memberi sinyal minta bantuan\n" +
                                    "• Tunjukkan kartu identifikasi yang menyatakan Anda tunarungu\n" +
                                    "• Pergunakan aplikasi pesan teks untuk komunikasi darurat",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir - Tunarungu",
                            "• Gunakan aplikasi atau notepad untuk komunikasi dengan petugas\n" +
                                    "• Perhatikan informasi visual dari berita atau pemberitahuan\n" +
                                    "• Minta bantuan tetangga atau kerabat untuk informasi audio",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("gempa bumi")) {
                    steps.add(new MitigasiStep("Sebelum Gempa - Tunarungu",
                            "• Pasang alarm gempa visual dengan lampu strobo\n" +
                                    "• Susun perabotan agar tidak menghalangi jalur evakuasi\n" +
                                    "• Siapkan kartu identifikasi disabilitas pendengaran\n" +
                                    "• Download aplikasi peringatan bencana dengan notifikasi visual",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Gempa - Tunarungu",
                            "• Perhatikan pergerakan barang dan reaksi orang di sekitar\n" +
                                    "• Berlindung di bawah meja kuat dan jauh dari kaca\n" +
                                    "• Gunakan lampu senter untuk menarik perhatian penyelamat\n" +
                                    "• Bawa alat tulis untuk komunikasi darurat",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Gempa - Tunarungu",
                            "• Perhatikan kerusakan struktur secara visual\n" +
                                    "• Gunakan komunikasi tertulis dengan petugas penyelamat\n" +
                                    "• Cek berita dan informasi dengan teks/caption\n" +
                                    "• Bergabung dengan kelompok tunarungu untuk dukungan",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("banjir bandang")) {
                    steps.add(new MitigasiStep("Sebelum Banjir Bandang - Tunarungu",
                            "• Pasang sistem peringatan visual dengan lampu berkedip kuat\n" +
                                    "• Pantau perubahan cuaca dengan aplikasi visual\n" +
                                    "• Siapkan kartu komunikasi darurat tahan air\n" +
                                    "• Koordinasi dengan tetangga untuk sinyal visual evakuasi",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir Bandang - Tunarungu",
                            "• Perhatikan perubahan arus air dan reaksi orang sekitar\n" +
                                    "• Gunakan senter atau lampu darurat untuk sinyal SOS\n" +
                                    "• Bawa alat tulis tahan air untuk komunikasi\n" +
                                    "• Tunjukkan kartu identitas yang menjelaskan kondisi Anda",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir Bandang - Tunarungu",
                            "• Perhatikan instruksi visual dari petugas\n" +
                                    "• Dokumentasikan kerusakan dengan foto untuk klaim\n" +
                                    "• Gunakan aplikasi pesan untuk komunikasi dengan keluarga\n" +
                                    "• Perhatikan tanda-tanda visual longsor susulan",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("tanah longsor")) {
                    steps.add(new MitigasiStep("Sebelum Tanah Longsor - Tunarungu",
                            "• Pasang sistem peringatan getaran dan visual\n" +
                                    "• Perhatikan tanda-tanda visual seperti retakan tanah\n" +
                                    "• Siapkan peta evakuasi visual dengan jalur aman\n" +
                                    "• Simpan ponsel dengan aplikasi peringatan getaran",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Tanah Longsor - Tunarungu",
                            "• Perhatikan perubahan pada tanah atau pohon yang miring\n" +
                                    "• Lari horizontal (menyamping) dari arah longsor\n" +
                                    "• Gunakan lampu senter untuk menarik perhatian\n" +
                                    "• Cari area terbuka jauh dari tebing dan pohon",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Tanah Longsor - Tunarungu",
                            "• Gunakan kamera ponsel untuk dokumentasi dan komunikasi\n" +
                                    "• Hindari area yang masih berisiko longsor susulan\n" +
                                    "• Tampilkan kartu identitas disabilitas kepada petugas\n" +
                                    "• Catat informasi bantuan secara tertulis",
                            R.drawable.ic_launcher_background, 3));
                }
                break;

            case 3: // TUNADAKSA
                if (disasterType.equalsIgnoreCase("banjir")) {
                    steps.add(new MitigasiStep("Sebelum Banjir - Tunadaksa",
                            "• Identifikasi rute evakuasi yang aksesibel kursi roda\n" +
                                    "• Siapkan alat bantu mobilitas cadangan di tempat tinggi\n" +
                                    "• Pastikan pengaman kursi roda berfungsi dengan baik",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir - Tunadaksa",
                            "• Gunakan pelampung khusus untuk pengguna kursi roda jika tersedia\n" +
                                    "• Pindah ke lantai yang lebih tinggi jika memungkinkan\n" +
                                    "• Gunakan aplikasi SOS yang dapat diakses",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir - Tunadaksa",
                            "• Periksa peralatan mobilitas dari kerusakan\n" +
                                    "• Hindari area dengan reruntuhan yang sulit dilewati\n" +
                                    "• Catat kontak penyedia alat bantu mobilitas untuk perbaikan",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("gempa bumi")) {
                    steps.add(new MitigasiStep("Sebelum Gempa - Tunadaksa",
                            "• Identifikasi area perlindungan yang dapat diakses kursi roda\n" +
                                    "• Siapkan alat bantu mobilitas manual cadangan\n" +
                                    "• Tentukan cara untuk melindungi kepala tanpa harus turun dari kursi roda",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Gempa - Tunadaksa",
                            "• Kunci roda kursi jika menggunakan kursi roda\n" +
                                    "• Lindungi kepala dengan bantal atau tas\n" +
                                    "• Hindari jendela dan benda yang mungkin jatuh",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Gempa - Tunadaksa",
                            "• Periksa jalan untuk aksesibilitas kursi roda sebelum bergerak\n" +
                                    "• Komunikasikan kebutuhan spesifik kepada petugas\n" +
                                    "• Periksa alat bantu mobilitas dari kerusakan",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("banjir bandang")) {
                    steps.add(new MitigasiStep("Sebelum Banjir Bandang - Tunadaksa",
                            "• Siapkan jalur evakuasi yang rata dan bebas hambatan\n" +
                                    "• Identifikasi tempat tinggi yang dapat diakses kursi roda\n" +
                                    "• Buat kesepakatan dengan orang terdekat untuk bantuan evakuasi",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir Bandang - Tunadaksa",
                            "• Prioritaskan mencapai tempat tinggi daripada membawa barang\n" +
                                    "• Minta bantuan untuk evakuasi cepat saat tanda bahaya terdengar\n" +
                                    "• Koordinasikan dengan penolong cara terbaik memindahkan Anda",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir Bandang - Tunadaksa",
                            "• Periksa kursi roda atau alat bantu mobilitas dari kerusakan\n" +
                                    "• Hindari melewati area berlumpur dan tidak stabil\n" +
                                    "• Informasikan kebutuhan aksesibilitas di pengungsian",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("tanah longsor")) {
                    steps.add(new MitigasiStep("Sebelum Tanah Longsor - Tunadaksa",
                            "• Pelajari tanda-tanda awal tanah longsor\n" +
                                    "• Identifikasi jalur evakuasi datar dan stabil\n" +
                                    "• Pasang sinyal bantuan khusus di rumah",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Tanah Longsor - Tunadaksa",
                            "• Minta bantuan segera untuk evakuasi horizontal\n" +
                                    "• Jika sendiri, lepaskan kursi roda dan bergerak dengan tangan\n" +
                                    "• Beri tahu petugas lokasi dan kondisi Anda",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Tanah Longsor - Tunadaksa",
                            "• Tunggu bantuan di area stabil\n" +
                                    "• Jelaskan kebutuhan mobilitas khusus kepada petugas\n" +
                                    "• Minta evaluasi kondisi jalur aksesibel sebelum kembali",
                            R.drawable.ic_launcher_background, 3));
                }
                break;

            case 4: // TUNAGRAHITA
                if (disasterType.equalsIgnoreCase("banjir")) {
                    steps.add(new MitigasiStep("Sebelum Banjir - Tunagrahita",
                            "• SIAPKAN tas darurat berwarna terang dengan foto isinya\n" +
                                    "• HAFALKAN nomor telepon penting\n" +
                                    "• LATIH evakuasi dengan langkah sederhana\n" +
                                    "• KENAKAN gelang identitas",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir - Tunagrahita",
                            "• PERGI ke tempat tinggi\n" +
                                    "• JANGAN sentuh air\n" +
                                    "• IKUTI petunjuk penolong\n" +
                                    "• PEGANG kartu identitas",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir - Tunagrahita",
                            "• TUNGGU sampai air surut\n" +
                                    "• TETAP bersama pendamping\n" +
                                    "• MINUM air bersih saja\n" +
                                    "• MINTA bantuan jika terpisah",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("gempa bumi")) {
                    steps.add(new MitigasiStep("Sebelum Gempa - Tunagrahita",
                            "• LATIH gerakan 'BERLINDUNG' di bawah meja kuat\n" +
                                    "• PELAJARI gambar tempat aman di setiap ruangan\n" +
                                    "• HAFALKAN nama dan nomor telepon keluarga\n" +
                                    "• KENAKAN gelang atau kalung identitas",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Gempa - Tunagrahita",
                            "• BERLINDUNG di bawah meja kuat\n" +
                                    "• JAUH dari jendela dan lemari\n" +
                                    "• LINDUNGI kepala dengan bantal atau tas\n" +
                                    "• TUNGGU sampai guncangan berhenti",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Gempa - Tunagrahita",
                            "• IKUTI pendamping ke luar rumah\n" +
                                    "• JAUH dari bangunan rusak\n" +
                                    "• TUNJUKKAN gelang identitas kepada petugas\n" +
                                    "• TETAP dengan kelompok pengungsi",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("banjir bandang")) {
                    steps.add(new MitigasiStep("Sebelum Banjir Bandang - Tunagrahita",
                            "• LIHAT gambar cara naik ke tempat tinggi\n" +
                                    "• PAKAI gelang identitas warna terang\n" +
                                    "• SIMAK bunyi sirine peringatan\n" +
                                    "• INGAT nama pendamping",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir Bandang - Tunagrahita",
                            "• LARI cepat ke tempat tinggi\n" +
                                    "• PANGGIL bantuan dengan keras\n" +
                                    "• JAUHI aliran air deras\n" +
                                    "• PEGANG tali pengaman jika ada",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir Bandang - Tunagrahita",
                            "• DIAM di tempat aman\n" +
                                    "• TUNJUK gelang identitas ke petugas\n" +
                                    "• JANGAN sentuh air kotor\n" +
                                    "• TUNGGU pendamping datang",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("tanah longsor")) {
                    steps.add(new MitigasiStep("Sebelum Tanah Longsor - Tunagrahita",
                            "• LIHAT gambar tempat aman jauh dari bukit\n" +
                                    "• INGAT jalan ke tempat aman\n" +
                                    "• PAKAI gelang identitas\n" +
                                    "• IKUTI latihan evakuasi",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Tanah Longsor - Tunagrahita",
                            "• LARI ke samping bukit, bukan ke bawah\n" +
                                    "• JAUH dari pohon dan tiang listrik\n" +
                                    "• LINDUNGI kepala dengan tas\n" +
                                    "• TERIAK minta tolong",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Tanah Longsor - Tunagrahita",
                            "• JANGAN kembali ke area longsor\n" +
                                    "• TETAP bersama kelompok\n" +
                                    "• TUNJUK gelang identitas pada petugas\n" +
                                    "• IKUTI arahan dengan gambar",
                            R.drawable.ic_launcher_background, 3));
                }
                break;

            default: // UMUM - menggunakan yang sudah ada
                if (disasterType.equalsIgnoreCase("banjir")) {
                    steps.add(new MitigasiStep("Sebelum Banjir",
                            "• Kenali jalur evakuasi terdekat\n• Simpan barang berharga di tempat tinggi\n• Ikuti informasi cuaca dan peringatan banjir",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir",
                            "• Jauhi area banjir dan arus deras\n• Naik ke tempat yang lebih tinggi\n• Hubungi petugas darurat jika diperlukan",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir",
                            "• Pastikan keamanan sebelum kembali ke rumah\n• Bersihkan rumah dan periksa kerusakan\n• Laporkan kerusakan infrastruktur",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("gempa bumi")) {
                    steps.add(new MitigasiStep("Sebelum Gempa",
                            "• Amankan perabotan yang dapat jatuh\n• Kenali struktur aman untuk berlindung\n• Siapkan tas darurat dan perlengkapan P3K",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Gempa",
                            "• Drop, Cover, Hold On (Merunduk, Berlindung, Berpegangan)\n• Jauhi jendela dan benda yang dapat jatuh\n• Keluar gedung setelah guncangan berhenti",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Gempa",
                            "• Periksa cedera dan berikan pertolongan\n• Periksa kerusakan gas, listrik, dan air\n• Ikuti informasi dari petugas berwenang",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("banjir bandang")) {
                    steps.add(new MitigasiStep("Sebelum Banjir Bandang",
                            "• Kenali tanda-tanda peringatan dini\n• Siapkan rute evakuasi tercepat\n• Siapkan tas darurat yang mudah dibawa",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Banjir Bandang",
                            "• Segera evakuasi ke tempat tinggi\n• Jangan menyeberangi aliran air\n• Jauhi jembatan dan area rendah",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Banjir Bandang",
                            "• Hindari area terdampak\n• Waspadai banjir susulan\n• Ikuti instruksi petugas untuk kembali",
                            R.drawable.ic_launcher_background, 3));
                }
                else if (disasterType.equalsIgnoreCase("tanah longsor")) {
                    steps.add(new MitigasiStep("Sebelum Tanah Longsor",
                            "• Kenali tanda-tanda peringatan: retakan tanah, pohon miring\n• Waspadai hujan deras berkepanjangan\n• Siapkan rencana evakuasi keluarga",
                            R.drawable.ic_launcher_background, 1));

                    steps.add(new MitigasiStep("Saat Tanah Longsor",
                            "• Evakuasi secara horizontal dari arah longsor\n• Waspada suara gemuruh dan getaran\n• Jangan kembali ke area berbahaya",
                            R.drawable.ic_launcher_background, 2));

                    steps.add(new MitigasiStep("Setelah Tanah Longsor",
                            "• Periksa struktur bangunan sebelum masuk\n• Hindari area longsor karena risiko longsor susulan\n• Laporkan kerusakan infrastruktur",
                            R.drawable.ic_launcher_background, 3));
                }
                break;
        }


        if (steps.isEmpty()) {
            steps.add(new MitigasiStep("Persiapan Umum",
                    "• Siapkan tas darurat\n• Kenali jalur evakuasi\n• Simpan kontak darurat\n• Ikuti informasi resmi",
                    R.drawable.ic_launcher_background, 1));
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