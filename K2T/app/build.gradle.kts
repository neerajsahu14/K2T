plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.app.k2t"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.app.k2t"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    defaultConfig {
        buildConfigField("String", "CLOUD_NAME", "\"${System.getenv("CLOUD_NAME") ?: ""}\"")
        buildConfigField("String", "API_KEY", "\"${System.getenv("API_KEY") ?: ""}\"")
        buildConfigField("String", "API_SECRET", "\"${System.getenv("API_SECRET") ?: ""}\"")
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
        buildConfig = true
    }
}

dependencies {
    implementation(libs.kotzilla.sdk)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.viewmodel.android)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.material.navigation)
    implementation (libs.play.services.auth)
    implementation (platform(libs.firebase.bom))
    // Hilt dependencies
    implementation(libs.hilt.android)
    implementation(libs.firebase.firestore)

    implementation(libs.androidx.room.runtime)
    implementation(libs.firebase.ai)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)
    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.android)

    implementation("io.coil-kt:coil-compose:2.3.0")
    implementation(libs.translate)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.androidx.core.splashscreen)

    // ExoPlayer (Media3)
    implementation(libs.androidx.media3.exoplayer) // Replace with the latest stable version
    implementation(libs.androidx.media3.ui)         // Replace with the latest stable version
    implementation(libs.androidx.media3.common)     // Replace with the latest stable version


    implementation(libs.androidx.runtime.livedata)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation("com.cloudinary:cloudinary-android:3.0.2")

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}
