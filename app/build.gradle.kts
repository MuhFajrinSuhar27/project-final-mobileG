plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.projekkhayalan"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.projekkhayalan"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation("com.google.code.gson:gson:2.10.1")


    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime:2.7.0")

    // Kemungkinan dibutuhkan dari list sebelumnya
    implementation ("de.hdodenhof:circleimageview:3.1.0") // Jika menggunakan CircleImageView
    implementation("com.google.android.gms:play-services-location:21.0.1") // Jika menggunakan lokasi
    implementation ("androidx.gridlayout:gridlayout:1.0.0") // Jika menggunakan GridLayout
}