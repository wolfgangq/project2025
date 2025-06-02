plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.tradition.mobilevtkproject"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.tradition.mobilevtkproject"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.4.0-beta.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
    }

    buildFeatures{
        //noinspection DataBindingWithoutKapt
        buildConfig = true
        dataBinding = true
        viewBinding = true
        prefab = false
    }
    buildTypes {
        debug {
            isDebuggable = true
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "none"
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }
    kotlinOptions {
        jvmTarget = "18"
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            excludes += setOf(
                "**/META-INF/AL2.0",
                "**/META-INF/LGPL2.1",
                "**/libmaps-mobile.so",
                "**/libjni.so"
            )
        }
    }

    //buildToolsVersion = "34.0.0"
}

dependencies {
    implementation("com.yandex.android:maps.mobile:4.16.0-lite") {
        exclude(group = "com.yandex.android", module = "maps-http-client")
        exclude(group = "com.yandex.android", module = "maps-mobile")
        exclude(group = "com.yandex.android", module = "runtime")
    }

    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material.icons.extended)

    implementation(libs.glide)
    implementation(libs.androidx.recyclerview)

    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.appcheck.playintegrity)
    implementation(libs.firebase.appcheck.debug)
    implementation(libs.firebase.config)
    implementation(libs.firebase.messaging.ktx)
    //implementation(libs.firebase.ai)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.kotlin.stdlib)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.fragment)

    implementation(libs.play.services.location)

    implementation(libs.core)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
