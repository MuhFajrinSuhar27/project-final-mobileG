package com.example.projekkhayalan.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {

    @SerializedName("lokasi")
    private LocationInfo lokasi;

    @SerializedName("data")
    private List<DataItem> data;

    public LocationInfo getLokasi() {
        return lokasi;
    }

    public List<DataItem> getData() {
        return data;
    }

    public static class LocationInfo {
        @SerializedName("adm1")
        private String adm1;

        @SerializedName("adm2")
        private String adm2;

        @SerializedName("adm3")
        private String adm3;

        @SerializedName("adm4")
        private String adm4;

        @SerializedName("provinsi")
        private String provinsi;

        @SerializedName("kotkab")
        private String kotkab;

        @SerializedName("kecamatan")
        private String kecamatan;

        @SerializedName("desa")
        private String desa;

        @SerializedName("lon")
        private double longitude;

        @SerializedName("lat")
        private double latitude;

        @SerializedName("timezone")
        private String timezone;

        // Getters
        public String getAdm4() { return adm4; }
        public String getProvinsi() { return provinsi; }
        public String getKotkab() { return kotkab; }
        public String getKecamatan() { return kecamatan; }
        public String getDesa() { return desa; }
    }

    public static class DataItem {
        @SerializedName("lokasi")
        private LocationInfo lokasi;

        @SerializedName("cuaca")
        private List<List<WeatherItem>> cuaca;

        public LocationInfo getLokasi() { return lokasi; }
        public List<List<WeatherItem>> getCuaca() { return cuaca; }
    }

    public static class WeatherItem {
        @SerializedName("datetime")
        private String datetime;

        @SerializedName("t")
        private int temperature;

        @SerializedName("weather")
        private int weatherCode;

        @SerializedName("weather_desc")
        private String weatherDescription;

        @SerializedName("weather_desc_en")
        private String weatherDescriptionEn;

        @SerializedName("hu") // kelembapan
        private int humidity;

        @SerializedName("ws") // kecepatan angin
        private double windSpeed;

        @SerializedName("wd") // arah angin
        private String windDirection;

        @SerializedName("wd_to")
        private String windDirectionTo;

        @SerializedName("image")
        private String imageUrl;

        @SerializedName("local_datetime")
        private String localDatetime;

        // Getters
        public String getDatetime() { return datetime; }
        public int getTemperature() { return temperature; }
        public String getWeatherDescription() { return weatherDescription; }
        public int getHumidity() { return humidity; }
        public double getWindSpeed() { return windSpeed; }
        public String getWindDirection() { return windDirection; }
        public String getWindDirectionTo() { return windDirectionTo; }
        public String getImageUrl() { return imageUrl; }
        public String getLocalDatetime() { return localDatetime; }
    }
}