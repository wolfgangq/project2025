plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.tradition.mobilevtkproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tradition.mobilevtkproject"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.22"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures{
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }
    //buildToolsVersion = "34.0.0"
}

dependencies {
    implementation("com.yandex.android:maps.mobile:4.14.0-full")

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material.icons.extended)

    implementation(libs.glide)
    implementation(libs.androidx.recyclerview)

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.analytics)
    implementation("com.google.firebase:firebase-auth-ktx:23.2.0")

    /*implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)*/

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.kotlin.stdlib)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment)

    implementation(libs.play.services.maps)
    implementation("com.google.android.gms:play-services-location:21.3.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
