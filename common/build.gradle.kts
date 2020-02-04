import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

val ideaActive = System.getProperty("idea.active") == "true"

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("kotlinx-serialization")
}

android {
    compileSdkVersion(29)
    buildToolsVersion = "28.0.3"
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
    }
}

kotlin {
    jvm()
    android()

    val iosArm32 = iosArm32("iosArm32")
    val iosArm64 = iosArm64("iosArm64")
    val iosX64 = iosX64("iosX64")
    if (ideaActive) {
        iosX64("ios")
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.2-1.3.60")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:0.14.0")
                implementation("io.ktor:ktor-client-core:1.3.0-beta-2")
                implementation("io.ktor:ktor-client-serialization:1.3.0-beta-2")
            }
        }

        val mobileMain by creating {
            dependsOn(commonMain)
        }

        val jvmMain by getting {
            dependencies {
                api("org.jetbrains.kotlin:kotlin-stdlib:1.3.61")
                api("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
                implementation("androidx.appcompat:appcompat:1.1.0")
                implementation("com.google.firebase:firebase-firestore:21.4.0")

                api("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.14.0")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2-1.3.60")
                api("io.ktor:ktor-client-core-jvm:1.3.0-beta-2")
                api("io.ktor:ktor-client-serialization-jvm:1.3.0-beta-2")
            }
        }

        val androidMain by getting {
            dependsOn(mobileMain)
            dependsOn(jvmMain)

            dependencies {

            }
        }

        val iosMain = if (ideaActive) {
            getByName("iosMain")
        } else {
            create("iosMain")
        }

        iosMain.apply {
            dependsOn(mobileMain)

            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-native:1.3.2-1.3.60")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:0.14.0")

                implementation("io.ktor:ktor-client-ios:1.3.0-beta-2")
                implementation("io.ktor:ktor-client-serialization-native:1.3.0-beta-2")
            }
        }

        val iosArm32Main by getting
        val iosArm64Main by getting
        val iosX64Main by getting

        configure(listOf(iosArm32Main, iosArm64Main, iosX64Main)) {
            dependsOn(iosMain)
        }
    }

    val frameworkName = "StormyAPI"

    configure(listOf(iosArm32, iosArm64, iosX64)) {
        compilations {
            val main by getting {
                extraOpts("-Xobjc-generics")
            }
        }

        binaries.framework {
            export("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.1")
            baseName = frameworkName
        }
    }

//    sourceSets["commonMain"].dependencies {
//        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
//    }
//
//    sourceSets["androidMain"].dependencies {
//        implementation("org.jetbrains.kotlin:kotlin-stdlib")
//    }

    tasks.register<org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask>("debugFatFramework") {
        baseName = frameworkName
        group = "Universal framework"
        description = "Builds a universal (fat) debug framework"

        from(iosX64.binaries.getFramework("DEBUG"))
    }

    tasks.register<org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask>("releaseFatFramework") {
        baseName = frameworkName
        group = "Universal framework"
        description = "Builds a universal (release) debug framework"

        from(iosArm64.binaries.getFramework("RELEASE"), iosArm32.binaries.getFramework("RELEASE"))
    }


//val packForXcode by tasks.creating(Sync::class) {
//    val targetDir = File(buildDir, "xcode-frameworks")
//
//    /// selecting the right configuration for the iOS
//    /// framework depending on the environment
//    /// variables set by Xcode build
//    val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
//    val framework = kotlin.targets
//        .getByName<KotlinNativeTarget>("ios")
//        .binaries.getFramework(mode)
//    inputs.property("mode", mode)
//    dependsOn(framework.linkTask)
//
//    from({ framework.outputDirectory })
//    into(targetDir)
//
//    /// generate a helpful ./gradlew wrapper with embedded Java path
//    doLast {
//        val gradlew = File(targetDir, "gradlew")
//        gradlew.writeText("#!/bin/bash\n"
//            + "export 'JAVA_HOME=${System.getProperty("java.home")}'\n"
//            + "cd '${rootProject.rootDir}'\n"
//            + "./gradlew \$@\n")
//        gradlew.setExecutable(true)
//    }
}