plugins {
    id("com.android.application")
    id("kotlin-multiplatform")
    id("kotlin-android-extensions")
    id("kotlinx-serialization")
    id("com.google.gms.google-services")
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "28.0.3"
    defaultConfig {
        applicationId = "com.semyon.keyholder"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        val release by getting {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
}

kotlin {
    android()

    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":common"))

                implementation("androidx.appcompat:appcompat:1.1.0")
                implementation("androidx.recyclerview:recyclerview:1.1.0")
                implementation("androidx.viewpager2:viewpager2:1.0.0")
                implementation("androidx.constraintlayout:constraintlayout:1.1.3")
                implementation("com.google.android.material:material:1.0.0")
                implementation("com.tbuonomo.andrui:viewpagerdotsindicator:4.1.2")
                implementation("androidx.multidex:multidex:2.0.0")
                implementation("androidx.core:core-ktx:+")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.2-1.3.60")

                implementation("com.google.firebase:firebase-analytics:17.2.2")
                implementation("com.google.firebase:firebase-core:17.2.2")
                implementation("com.google.firebase:firebase-firestore:21.4.0")

                implementation("io.ktor:ktor-client-android:1.3.0-beta-2")
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
            }
        }
    }
}