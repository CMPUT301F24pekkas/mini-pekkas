plugins {
    alias(libs.plugins.android.application)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs")
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
    implementation(libs.firebase.storage)
    implementation(libs.uiautomator)
    implementation(libs.espresso.intents)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // UI testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.github.bumptech.glide:glide:4.15.1") // Check for latest version
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")

    // Implement android.jar for javadoc, replace with your own filepath
    // implementation(files("/home/ryan/Android/Sdk/platforms/android-34/android.jar"))
    // implementation(files("C:\\Users\\Arno\\AppData\\Local\\Android\\Sdk\\platforms\\android-34\\android.jar"));


    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    // https://firebase.google.com/docs/android/setup#available-libraries
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")

    // for the qr code
    implementation("com.journeyapps:zxing-android-embedded:4.3.0") {
        exclude(group = "com.google.zxing", module = "android-integration")
    }

    implementation("com.google.zxing:core:3.5.1") // Core library
    implementation("com.google.zxing:javase:3.4.1")// Optional, for the MatrixToImageWriter

    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:18.1.0")


    implementation("androidx.work:work-runtime:2.8.1") // For background tasks/scheduling
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.4")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.4")
}

