plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.newfrost.keyboard"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.newfrost.keyboard"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures { buildConfig = true }

    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
}
