plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.room) apply(false)
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.android.application).apply(false)
    id("io.appmetrica.analytics") version "1.0.1" apply false
    id("org.jetbrains.dokka") version "2.0.0" apply false
}

buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
    }
}