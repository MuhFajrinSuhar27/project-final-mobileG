package com.example.projekkhayalan.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.projekkhayalan.R;
import com.example.projekkhayalan.activities.MitigasiDetailActivity;
import com.example.projekkhayalan.utils.AccessibilityHelper;

import java.util.Locale;

public class TutorialFragment extends Fragment {

    private TextView textViewTitle;
    private CardView cardBanjir;
    private CardView cardBanjirBandang;
    private CardView cardGempaBumi;
    private CardView cardTanahLongsor;

    private int disabilityType;
    private TextToSpeech textToSpeech;
    private AccessibilityHelper accessibilityHelper;

    public TutorialFragment() {
    }

    public static TutorialFragment newInstance(int disabilityType) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt("DISABILITY_TYPE", disabilityType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            disabilityType = getArguments().getInt("DISABILITY_TYPE", 1);
        }
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_tutorial_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupAccessibilityFeatures();
        setupCardClickListeners();
    }



    private void initViews(View view) {
        textViewTitle = view.findViewById(R.id.textViewMitigasiTitle);
        cardBanjir = view.findViewById(R.id.cardBanjir);
        cardBanjirBandang = view.findViewById(R.id.cardBanjirBandang);
        cardGempaBumi = view.findViewById(R.id.cardGempaBumi);
        cardTanahLongsor = view.findViewById(R.id.cardTanahLongsor);
    }

    private void setupAccessibilityFeatures() {
        accessibilityHelper = new AccessibilityHelper(requireContext(), disabilityType);

        // TTS hanya untuk tunanetra
        if (disabilityType == 1) {
            textToSpeech = new TextToSpeech(requireContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(new Locale("id", "ID"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        textToSpeech.setLanguage(Locale.ENGLISH);
                    }
                    speakMitigasiInfo();
                }
            });
        } else {
            textToSpeech = null;
        }

        // Penyesuaian UI lainnya berdasarkan disabilitas
        if (disabilityType == 2) {
            textViewTitle.setTextSize(6);
        } else if (disabilityType == 3) {
            ViewGroup.LayoutParams paramsBanjir = cardBanjir.getLayoutParams();
            paramsBanjir.height = dpToPx(60);
            cardBanjir.setLayoutParams(paramsBanjir);
            // ... kode lain untuk tunadaksa
        }
    }

    private int dpToPx(int dp) {
        float density = requireContext().getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void speakMitigasiInfo() {
        if (textToSpeech != null && disabilityType == 1) {
            String mitigasiInfo = "Mitigasi Bencana. Pilih jenis bencana untuk melihat rekomendasi aksi. " +
                    "Tersedia informasi untuk banjir, banjir bandang, gempa bumi, dan tanah longsor.";
            textToSpeech.speak(mitigasiInfo, TextToSpeech.QUEUE_FLUSH, null, "mitigasi_intro");
        }
    }

    private void setupCardClickListeners() {
        cardBanjir.setOnClickListener(v -> {
            showMitigasiDetail("Banjir");
            if (textToSpeech != null && disabilityType == 1) {
                speakMitigasiDetail("Banjir");
            }
        });

        cardBanjirBandang.setOnClickListener(v -> {
            showMitigasiDetail("banjir bandang");
            if (textToSpeech != null && disabilityType == 1) {
                speakMitigasiDetail("banjir bandang");
            }
        });

        cardGempaBumi.setOnClickListener(v -> {
            showMitigasiDetail("Gempa Bumi");
            if (textToSpeech != null && disabilityType == 1) {
                speakMitigasiDetail("Gempa Bumi");
            }
        });

        cardTanahLongsor.setOnClickListener(v -> {
            showMitigasiDetail("tanah longsor");
            if (textToSpeech != null && disabilityType == 1) {
                speakMitigasiDetail("tanah longsor");
            }
        });
    }

    private void showMitigasiDetail(String disasterType) {
        Toast.makeText(requireContext(), "Menampilkan mitigasi untuk " + disasterType, Toast.LENGTH_SHORT).show();

        // Buat Intent untuk membuka MitigasiDetail
        Intent intent = new Intent(requireContext(), MitigasiDetailActivity.class);


        // Normalisasi string ke lowercase untuk konsistensi
        String normalizedDisasterType = disasterType.toLowerCase();


        // Tambahkan data jenis bencana dan tipe disabilitas
        intent.putExtra("DISASTER_TYPE", normalizedDisasterType);
        intent.putExtra("DISABILITY_TYPE", disabilityType);

        // Mulai activity MitigasiDetail
        startActivity(intent);
    }

    private void speakMitigasiDetail(String disasterType) {
        String infoToSpeak = "";

        switch (disasterType) {
            case "Banjir":
                infoToSpeak = "Rekomendasi mitigasi banjir. " +
                        "• Sebelum banjir: Hafal rute evakuasi dengan menghitung langkah dan meraba penanda khusus\n" +
                        "• Saat Banjir Tetap dekat dengan dinding untuk orientasi\n" +
                        "• Setelah Banjir: Berhati-hati dengan reruntuhan yang mungkin tidak terdeteksi";
                break;

            case "banjir bandang":
                infoToSpeak = "Rekomendasi mitigasi banjir bandang. " +
                        "Sebelum banjir bandang: Hindari tinggal di dekat aliran sungai di lereng gunung. " +
                        "Kenali tanda-tanda seperti hujan deras berkepanjangan dan suara gemuruh. " +
                        "Saat terjadi: Segera lari ke tempat yang lebih tinggi tanpa membawa barang apapun. " +
                        "Jangan menyeberangi aliran air.";
                break;

            case "Gempa Bumi":
                infoToSpeak = "Rekomendasi mitigasi gempa bumi. " +
                        "Saat gempa: Jika di dalam ruangan, berlindung di bawah meja yang kokoh. " +
                        "Lindungi kepala dan leher dengan tangan. " +
                        "Jika di luar ruangan, jauhi bangunan, pohon, dan kabel listrik. " +
                        "Setelah gempa: Periksa kerusakan, waspada gempa susulan.";
                break;

            case "Tanah Longsor":
                infoToSpeak = "Rekomendasi mitigasi tanah longsor. " +
                        "Sebelum longsor: Perhatikan tanda-tanda seperti retakan tanah, pohon miring, dan air yang keruh. " +
                        "Saat hujan deras, waspada terhadap suara gemuruh dan getaran. " +
                        "Jika terjadi: Segera evakuasi menjauh dari arah longsor secepat mungkin.";
                break;
        }

        textToSpeech.speak(infoToSpeak, TextToSpeech.QUEUE_FLUSH, null, "mitigasi_detail");
    }

    

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}