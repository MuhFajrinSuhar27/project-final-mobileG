package com.example.projekkhayalan.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.projekkhayalan.R;

import java.util.ArrayList;
import java.util.List;

public class AccessibilityHelper {
    private Context context;
    private int disabilityType;
    private boolean largerText = false;
    private boolean highContrast = false;
    private List<TextView> registeredTextViews = new ArrayList<>();
    private List<Button> registeredButtons = new ArrayList<>();
    private List<EditText> registeredEditTexts = new ArrayList<>();

    public AccessibilityHelper(Context context, int disabilityType) {
        this.context = context;
        this.disabilityType = disabilityType;
    }

    public void setLargerText(boolean largerText) {
        this.largerText = largerText;
        applyTextSizeToRegistered();
    }

    public void setHighContrast(boolean highContrast) {
        this.highContrast = highContrast;
        applyContrastToRegistered();
    }

    public void registerTextView(TextView textView) {
        registeredTextViews.add(textView);
        applyAccessibilityFeatures(textView);
    }

    public void registerButton(Button button) {
        registeredButtons.add(button);
        applyAccessibilityFeatures(button);
    }

    public void registerEditText(EditText editText) {
        registeredEditTexts.add(editText);
        applyAccessibilityFeatures(editText);
    }

    public void adjustUI() {
        // Penerapan penyesuaian UI berdasarkan jenis disabilitas

        // Untuk tunanetra: peningkatan kontras dan ukuran
        if (disabilityType == 1) { // Tunanetra
            setHighContrast(true);
            setLargerText(true);
        }

        // Untuk tunarungu: lebih banyak visual cues
        else if (disabilityType == 2) { // Tunarungu
            // Implementasi spesifik untuk tunarungu bisa ditambahkan
        }

        // Untuk tunadaksa: tombol lebih besar, kemudahan interaksi
        else if (disabilityType == 3) { // Tunadaksa
            makeTouchTargetsLarger();
        }

        // Untuk tunagrahita: UI lebih sederhana, teks lebih mudah dipahami
        else if (disabilityType == 4) { // Tunagrahita
            setLargerText(true);
            // Simplifikasi UI (bisa ditambahkan implementasi khusus)
        }
    }

    private void applyTextSizeToRegistered() {
        for (TextView textView : registeredTextViews) {
            applyTextSize(textView);
        }
        for (Button button : registeredButtons) {
            applyTextSize(button);
        }
        for (EditText editText : registeredEditTexts) {
            applyTextSize(editText);
        }
    }

    private void applyContrastToRegistered() {
        for (TextView textView : registeredTextViews) {
            applyContrast(textView);
        }
        for (Button button : registeredButtons) {
            applyContrast(button);
        }
        for (EditText editText : registeredEditTexts) {
            applyContrast(editText);
        }
    }

    public void applyTextSize(TextView textView) {
        if (largerText) {
            float currentSize = textView.getTextSize();
            textView.setTextSize(currentSize * 0.05f + 4); // Peningkatan ukuran teks
        }
    }

    public void applyContrast(TextView textView) {
        if (highContrast) {
            textView.setTextColor(Color.BLACK);
            if (textView.getParent() instanceof View) {
                ((View) textView.getParent()).setBackgroundColor(Color.WHITE);
            }
        }
    }

    private void makeTouchTargetsLarger() {
        for (Button button : registeredButtons) {
            button.setPadding(
                    button.getPaddingLeft() + 16,
                    button.getPaddingTop() + 16,
                    button.getPaddingRight() + 16,
                    button.getPaddingBottom() + 16
            );
        }
    }

    public void applyAccessibilityFeatures(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (largerText) {
                applyTextSize(textView);
            }
            if (highContrast) {
                applyContrast(textView);
            }
        } else if (view instanceof Button) {
            Button button = (Button) view;
            if (largerText) {
                applyTextSize(button);
            }
            if (highContrast) {
                applyContrast(button);
            }
            if (disabilityType == 3) { // Tunadaksa
                button.setPadding(
                        button.getPaddingLeft() + 16,
                        button.getPaddingTop() + 16,
                        button.getPaddingRight() + 16,
                        button.getPaddingBottom() + 16
                );
            }
        } else if (view instanceof EditText) {
            EditText editText = (EditText) view;
            if (largerText) {
                applyTextSize(editText);
            }
            if (highContrast) {
                applyContrast(editText);
            }
        }
    }
}