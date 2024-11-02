plugins {
    alias(libs.plugins.android.application)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mini_pekkas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mini_pekkas"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true // for dataBinding support
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // UI testing
    testImplementation("junit:junit")
    androidTestImplementation("androidx.test.ext:junit")
    androidTestImplementation("androidx.test.espresso:espresso-core")

    // Implement android.jar for javadoc, replace with your own filepath
    // implementation(files("/home/ryan/Android/Sdk/platforms/android-34/android.jar"))

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation("com.google.firebase:firebase-firestore")
    // for the qr code
    implementation("com.google.zxing:core:3.4.1") // Core library
    implementation("com.google.zxing:javase:3.4.1")// Optional, for the MatrixToImageWriter
    implementation("com.google.zxing:android-core:3.3.0") // Android-specific library
    implementation("com.google.zxing:android-integration:3.3.0") // Optional, for integration with Android
}
