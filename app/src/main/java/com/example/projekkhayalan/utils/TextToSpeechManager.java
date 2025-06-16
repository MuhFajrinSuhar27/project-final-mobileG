package com.example.projekkhayalan.utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.Locale;

/**
 * Utilitas untuk mengelola Text-to-Speech dengan konsisten di seluruh aplikasi
 * dan memastikan hanya aktif untuk disabilitas tertentu
 */
public class TextToSpeechManager {

    private static final String TAG = "TextToSpeechManager";

    /**
     * Inisialisasi TextToSpeech berdasarkan jenis disabilitas
     * @param context Context aplikasi
     * @param disabilityType Jenis disabilitas (1=Tunanetra, 2=Tunarungu, dst)
     * @return TextToSpeech object atau null jika bukan tunanetra
     */
    public static TextToSpeech initialize(Context context, int disabilityType) {
        if (disabilityType == 1) {
            Log.d(TAG, "Initializing TTS for blind users");
            return new TextToSpeech(context, status -> {
                if (status == TextToSpeech.SUCCESS) {
                    // Set bahasa Indonesia
                    int result = new TextToSpeech(context, null).setLanguage(new Locale("id", "ID"));
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        // Fallback ke bahasa Inggris
                        new TextToSpeech(context, null).setLanguage(Locale.ENGLISH);
                    }
                }
            });
        } else {
            Log.d(TAG, "TTS disabled for non-blind users (type: " + disabilityType + ")");
            return null;
        }
    }


    public static void speak(TextToSpeech tts, int disabilityType, String text, String utteranceId) {
        // Hanya berbicara jika TTS tidak null dan disabilityType == 1
        if (tts != null && disabilityType == 1) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
        } else {
            Log.d(TAG, "Speech not executed: tts=" + (tts != null) + ", disability=" + disabilityType);
        }
    }

    /**
     * Menutup dan membersihkan TTS
     */
    public static void shutdown(TextToSpeech tts) {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
    }

    /**
     * Menambahkan listener kemajuan ucapan
     */
    public static void setProgressListener(TextToSpeech tts, final TtsCallback callback) {
        if (tts != null) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    callback.onTtsStart();
                }

                @Override
                public void onDone(String utteranceId) {
                    callback.onTtsFinish();
                }

                @Override
                public void onError(String utteranceId) {
                    callback.onTtsError();
                }
            });
        }
    }

    /**
     * Interface callback untuk memantau status TTS
     */
    public interface TtsCallback {
        void onTtsStart();
        void onTtsFinish();
        void onTtsError();
    }
}