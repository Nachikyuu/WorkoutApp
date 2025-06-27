plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // ksp Plugin für Kotlin Symbol Processing (KSP) für Room als Speichermethode
    // (Alternative zu SharedPreference)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.workoutapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.workoutapp"
        minSdk = 26
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.support.annotations)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Standard Compose Abhängigkeiten
    implementation(platform(libs.androidx.compose.bom))

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // ViewModel und LiveData (Lifecycle)
    implementation(libs.androidx.lifecycle.viewmodel.compose) // ViewModel mit Compose
    implementation(libs.androidx.lifecycle.livedata.ktx) // LiveData

    // Room (für lokale Datenbank - optional, aber empfohlen für deinen Fortschritt)
    val roomVersion = "2.6.1"
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler) // Diese Zeile ist korrekt für KSP
    implementation(libs.androidx.room.ktx)


    implementation(libs.androidx.lifecycle.viewmodel.compose.v291) // Für viewModel()
    implementation(libs.androidx.lifecycle.livedata.ktx.v291) // Für LiveData KTX Erweiterungen

    // Wichtig für observeAsState mit livedata:
    implementation(libs.androidx.runtime.livedata)


    // Für Bilder (AsyncImage von Coil - sehr beliebt)
    implementation(libs.coil.compose)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.espresso.core.v351)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}