plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

}




    // ...



android {
    namespace = "com.example.buildnote"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.buildnote"
        minSdk = 24
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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material:material-icons-extended:1.3.0")

    implementation ("androidx.compose.ui:ui:1.3.0")
    implementation ("androidx.compose.foundation:foundation:1.3.0")

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")

    implementation(libs.volley)
    implementation ("com.beust:klaxon:5.5")
    implementation ("androidx.compose.ui:ui:1.3.0")
    implementation ("androidx.compose.foundation:foundation:1.3.0")
    implementation ("androidx.compose.material3:material3:1.0.1")

    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    //implementation("androidx.navigation:navigation-common:2.8.1")

    implementation ("com.google.accompanist:accompanist-permissions:0.36.0")

    implementation("androidx.work:work-runtime-ktx:2.10.0")

    implementation ("io.coil-kt:coil-compose:2.3.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    dependencies {
        // Google Maps Compose Integration
        implementation ("com.google.maps.android:maps-compose:2.11.2")

        // Google Play Services (Maps SDK)
        implementation ("com.google.android.gms:play-services-maps:18.1.0")
    }



}

