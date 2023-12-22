plugins {
    alias(libs.plugins.com.android.application)
    alias(libs.plugins.org.jetbrains.kotlin.android)
//    alias(libs.plugins.secrets.gradle.plugin)
    alias(libs.plugins.com.google.devtools.ksp)
    alias(libs.plugins.com.dagger.hilt)
//    alias(libs.plugins.com.google.services)
//    alias(libs.plugins.com.firebase.crashlytics)
    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = libs.versions.app.namespace.get()
    compileSdk = libs.versions.compile.sdk.version.get().toInt()

    defaultConfig {
        applicationId = libs.versions.app.namespace.get()
        minSdk = libs.versions.min.sdk.version.get().toInt()
        targetSdk = libs.versions.target.sdk.version.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
//        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlin.compiler.extension.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        disable += "UnusedResources"
        disable += "Instantiatable"
    }
}

dependencies {

    implementation(projects.base)
    coreLibraryDesugaring(libs.desugar)
    kapt(libs.hilt.compiler)
//    ksp(libs.room.compiler)
    ksp(libs.glide.ksp)

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material3)
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.permissions)
    implementation(libs.landscapist.bom)
    implementation(libs.landscapist.glide)
    implementation(libs.landscapist.placeholder)
    implementation(libs.constraintlayout.compose)
    implementation(libs.lifecycle.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.constraintlayout.compose)
    implementation(libs.lottie.compose)
    implementation(libs.cloudy)
    implementation(libs.logging.interceptor)

    implementation(libs.timber)
    implementation(libs.hilt.android)
    implementation(libs.datastore.preferences)
    implementation(libs.retrofit)
    implementation(libs.gson)
//    implementation(libs.room.ktx)
//    implementation(libs.room.runtime)
//    implementation(libs.exoplayer.ui)
//    implementation(libs.exoplayer.core)
//    implementation(libs.exoplayer.dash)
//    implementation(libs.exoplayer.mediasession)
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.analytics)
//    implementation(libs.crashlytics)

    androidTestImplementation(platform(libs.compose.bom))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}