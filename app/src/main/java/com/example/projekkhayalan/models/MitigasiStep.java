package com.example.projekkhayalan.models;

public class MitigasiStep {
    private String title;
    private String description;
    private int imageResId;
    private int stepNumber;
    private String audioUrl;

    public MitigasiStep(String title, String description, int imageResId) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.stepNumber = 0;
    }

    public MitigasiStep(String title, String description, int imageResId, int stepNumber) {
        this.title = title;
        this.description = description;
        this.imageResId = imageResId;
        this.stepNumber = stepNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getStepNumber() {
        return stepNumber;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}