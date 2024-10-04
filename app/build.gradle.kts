import java.util.Properties

plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.appscrip.olamapdemo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.appscrip.olamapdemo"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

//        val localProperties = Properties()
//        val localPropertiesFile = rootProject.file("local.properties")
//
//        if (localPropertiesFile.exists()) {
//            localProperties.load(localPropertiesFile.inputStream())
//
//            val clientId = localProperties.getProperty("CLIENT_ID")
//            val clientSecret = localProperties.getProperty("CLIENT_SECRET")
//            val apiKey = localProperties.getProperty("API_KEY")
//
//            buildConfigField("String", "CLIENT_ID", "\"${clientId}\"")
//            buildConfigField("String", "CLIENT_SECRET", "\"${clientSecret}\"")
//            buildConfigField("String","API_KEY","\"${apiKey}\"")
//        }

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.play.services.location)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.retrofit)
    implementation(libs.moshi.converter)
    ksp(libs.moshi.kotlin.codegen)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)
    implementation(libs.moe.android.sdk)
    implementation(libs.android.sdk)
    implementation(libs.android.sdk.directions.models)
    implementation(libs.android.sdk.services)
    implementation(libs.android.sdk.turf)
    implementation(libs.android.plugin.markerview.v9)
    implementation(libs.android.plugin.annotation.v9)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.lifecycle.extensions)
    kapt(libs.lifecycle.compiler)
    implementation(files("libs/maps-1.0.68.aar"))
    implementation (libs.okhttpprofiler)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

}