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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        debug {
            // The Android Gradle Plugin automatically signs debug APKs with its debug key.
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
}
