/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.io.FileInputStream
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.android.application)
    id("io.appmetrica.analytics")
    id("com.google.gms.google-services")
    id("org.jetbrains.dokka") version "2.0.0"
}

val keystorePropertiesFile = rootProject.file("/signing.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists())
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val appMetricaPropertiesFile = rootProject.file("/appMetrica.properties")
val appMetricaProperties = Properties()
if (appMetricaPropertiesFile.exists()){
    appMetricaProperties.load(FileInputStream(appMetricaPropertiesFile))

    appmetrica {
        setPostApiKey(appMetricaProperties["apiKey"].toString())
        enableAnalytics = true
    }
}

kotlin {
    jvm("desktop")

    androidTarget {
        //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
    }

    sourceSets {
        val commonMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlin.coroutines)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.sqlite)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation("com.github.pjfanning:excel-streaming-reader:5.0.2")
            implementation("org.apache.xmlbeans:xmlbeans:3.1.0")
            implementation("javax.xml.stream:stax-api:1.0")
            implementation("com.fasterxml:aalto-xml:1.2.2")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
            implementation("com.squareup.retrofit2:retrofit:2.9.0")
            implementation("org.jsoup:jsoup:1.12.2")
            implementation("com.google.code.gson:gson:2.11.0")
        }

        val desktopMain by getting

        desktopMain.dependencies {
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(compose.desktop.currentOs)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.firebase.common.ktx)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation("com.google.guava:guava:33.4.0-android")
            implementation("io.appmetrica.analytics:analytics:7.5.0")
            implementation("ru.rustore.sdk:universalpush:6.5.0")
            implementation("ru.rustore.sdk:universalrustore:6.5.0")
            implementation("ru.rustore.sdk:universalfcm:6.5.0")
            implementation("com.google.firebase:firebase-messaging:22.0.0")
            implementation("com.google.android.gms:play-services-base:17.5.0")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.ghostwalker18.scheduledesktop2.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Schedule PCCE"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
            }
            /*windows {
                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
            }*/
        }
    }
}

android {
    namespace = "com.ghostwalker18.schedule"
    compileSdk = 35

    bundle {
        language {
            enableSplit = false
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(keystoreProperties["keystore"].toString())
            storePassword = keystoreProperties["keystorePassword"].toString()
            keyAlias = keystoreProperties["keyAlias"].toString()
            keyPassword = keystoreProperties["keyPassword"].toString()
        }
    }

    defaultConfig {
        minSdk = 26
        targetSdk = 35

        applicationId = "com.ghostwalker18.schedule"
        versionCode = 14
        versionName = "5.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isMinifyEnabled = false
            isDebuggable = true
            enableUnitTestCoverage = true
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies{
    implementation(libs.material)
    add("kspDesktop", libs.androidx.room.compiler)
}