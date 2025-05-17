import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.devtools.ksp)
    // required for generate injected code
    alias(libs.plugins.hilt.android)
    id("kotlin-parcelize")
}

android {
    namespace = "org.akhsaul.core"
    compileSdk = 35

    defaultConfig {
        val localProperties = loadProperties(rootProject.file("local.properties").toString())
        buildConfigField("String", "BASE_URL", "${localProperties["baseUrl"]}")
        buildConfigField("String", "HOST_NAME", "${localProperties["hostName"]}")
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // TODO Menerapkan obfuscation dengan ProGuard.
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
    }
}

dependencies {
    // generate all injected code with Hilt
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.android)

    // contains annotation for HiltWorker
    implementation(libs.androidx.hilt.common)

    // contains WorkerFactory for AssistedInject
    implementation(libs.androidx.hilt.work)

    // generate injected constructor for worker
    ksp(libs.androidx.hilt.compiler)

    implementation(libs.androidx.work.runtime.ktx)

    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    implementation(libs.androidx.startup.runtime)

    implementation(libs.coil)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.gson)

    //implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    //implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}