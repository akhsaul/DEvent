plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    id("kotlin-parcelize")
}

android {
    namespace = "org.akhsaul.dicodingevent"
    compileSdk = 35

    defaultConfig {
        applicationId = "org.akhsaul.dicodingevent"
        minSdk = 27
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(project(":core"))
    // TODO Menerapkan Leak Canary  dan tidak ada memory leaks saat dianalisa.
    //debugImplementation(libs.leakcanary.android)
    //implementation(libs.androidx.startup.runtime)

    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.work.runtime.ktx)

    //ksp(libs.androidx.hilt.compiler)
    //implementation(libs.androidx.hilt.common)
    //implementation(libs.androidx.hilt.work)

    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.datastore.preferences)
    //implementation(libs.androidx.room.runtime)
    //ksp(libs.androidx.room.compiler)
    //implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.material3)
    //implementation(libs.coil.svg)
    implementation(libs.androidx.swiperefreshlayout)
    //implementation(libs.converter.gson)
    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.activity.ktx)
    //implementation(libs.retrofit)
    //implementation(libs.gson)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    //unitTest
    testImplementation(kotlin("test"))
    testImplementation("app.cash.turbine:turbine:1.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    testImplementation(libs.junit)

    //androidTest
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.6.1")
    androidTestImplementation("androidx.test.espresso:espresso-device:1.0.1")
}