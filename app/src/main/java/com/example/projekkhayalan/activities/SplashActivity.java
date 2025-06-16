package com.example.projekkhayalan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projekkhayalan.R;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION = 2500; // 2.5 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logoImageView = findViewById(R.id.splash_logo);
        TextView appNameTextView = findViewById(R.id.textViewAppName);
        TextView appDescTextView = findViewById(R.id.textViewAppDescription);

        Animation fadeInLogo = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation fadeInText = AnimationUtils.loadAnimation(this, R.anim.fade_in_delayed);

        logoImageView.startAnimation(fadeInLogo);
        appNameTextView.startAnimation(fadeInText);
        appDescTextView.startAnimation(fadeInText);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeScreenActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}