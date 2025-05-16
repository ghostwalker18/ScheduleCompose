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

import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import java.io.FileInputStream
import java.net.URL
import java.util.*

plugins {
    kotlin("plugin.serialization") version "2.1.10"
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
        val desktopMain by getting

        desktopMain.dependencies {
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(compose.desktop.currentOs)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.swing)
            implementation("com.alphacephei:vosk:0.3.45")
        }

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
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
            implementation("com.russhwolf:multiplatform-settings:1.3.0")
            implementation("com.github.pjfanning:excel-streaming-reader:5.0.2")
            implementation("org.apache.xmlbeans:xmlbeans:3.1.0")
            implementation("javax.xml.stream:stax-api:1.0")
            implementation("com.fasterxml:aalto-xml:1.2.2")
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha11")
            implementation("com.squareup.retrofit2:retrofit:2.11.0")
            implementation("org.jsoup:jsoup:1.16.1")
            implementation("com.squareup.okhttp3:okhttp"){
                version{
                    strictly("3.14.9")
                }
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlin.test.junit)
        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.firebase.common.ktx)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)
            implementation(libs.rustore.universalpush)
            implementation(libs.rustore.universalfcm)
            implementation(libs.rustore.universalrustore)
            implementation(libs.appmetrica.analitycs)
            implementation("androidx.preference:preference:1.2.1")
            implementation("com.google.guava:guava:33.4.0-android")
            implementation("com.google.firebase:firebase-messaging:22.0.0")
            implementation("com.google.android.gms:play-services-base:17.5.0")
            implementation ("net.java.dev.jna:jna:5.13.0@aar")
            implementation ("com.alphacephei:vosk-android:0.3.47@aar")
            implementation (project(":models"))
        }

        androidInstrumentedTest.dependencies{
            implementation("androidx.test.ext:junit:1.1.5")
            implementation("org.mockito:mockito-core:4.11.0")
            implementation("androidx.test.espresso:espresso-core:3.5.1")
        }
    }
}

compose.resources {
    customDirectory(
        sourceSetName = "desktopMain",
        directoryProvider = provider { layout.projectDirectory.dir(
            "src/desktopMain/desktopResources"
        ) }
    )

    customDirectory(
        sourceSetName = "androidMain",
        directoryProvider = provider { layout.projectDirectory.dir(
            "src/androidMain/androidResources"
        ) }
    )
}

compose.desktop {

    application {

        mainClass =  BuildNames.mainPackageName + ".MainKt"

        buildTypes.release.proguard {
            configurationFiles.from("proguard-rules-desktop.pro")
            version.set("7.4.0")
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = BuildNames.desktopAppName
            description = BuildNames.desktopAppDescription
            packageVersion = BuildNames.desktopAppVersion
            copyright = BuildNames.desktopCopyright
            licenseFile.set(rootProject.file("LICENSE"))
            vendor = BuildNames.desktopAppVendor

            linux {
                iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
            }

            windows {
                iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
                shortcut = true
            }
        }
    }
}

android {
    namespace = BuildNames.mainPackageName
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

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
        targetSdk = BuildNames.androidTargetSdk

        applicationId = BuildNames.mainPackageName
        versionCode = BuildNames.androidVersionCode
        versionName = BuildNames.androidAppVersion

        ndkVersion = "25.2.9519653"
        ndk {
            abiFilters += mutableSetOf("armeabi-v7a", "arm64-v8a", "x86_64", "x86")
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules-android.pro"
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
    add("kspAndroid", libs.androidx.room.compiler)
}

/*
 *  Конфигурация для плагина документации Dokka
 */
tasks.withType<DokkaTask>().configureEach {

    val appName = BuildNames.docsAppName
    val url = BuildNames.githubURL

    pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
        footerMessage = BuildNames.desktopCopyright
        homepageLink = url
    }

    dokkaSourceSets {
        named("commonMain") {
            moduleName.set(appName)

            includes.from("src/commonMain/commonMainInfo.md")

            sourceLink {
                localDirectory.set(file("src/commonMain/kotlin"))
                remoteUrl.set(URL(url))
                remoteLineSuffix.set("#L")
            }
        }
    }

    dokkaSourceSets {
        named("androidMain") {
            moduleName.set(appName)

            includes.from("src/androidMain/androidMainInfo.md")

            sourceLink {
                localDirectory.set(file("src/androidMain/kotlin"))
                remoteUrl.set(URL(url))
                remoteLineSuffix.set("#L")
            }
        }
    }

    dokkaSourceSets {
        named("desktopMain") {
            moduleName.set(appName)

            includes.from("src/desktopMain/desktopMainInfo.md")

            sourceLink {
                localDirectory.set(file("src/desktopMain/kotlin"))
                remoteUrl.set(URL(url))
                remoteLineSuffix.set("#L")
            }
        }
    }
}