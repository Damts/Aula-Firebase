plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.damts.aulafirebase"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.damts.aulafirebase"
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

    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    //Dependencias Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.13.0"))

    //Dependencia Firebase Analytics
    implementation("com.google.firebase:firebase-analytics")

    //Autenticação
    implementation("com.google.firebase:firebase-auth")

    //Banco de dados
    implementation("com.google.firebase:firebase-firestore")

    //Armazenamento
    implementation("com.google.firebase:firebase-storage")

    //Picasso
    implementation ("com.squareup.picasso:picasso:2.8")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}