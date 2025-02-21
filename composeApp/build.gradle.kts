import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    id("org.jetbrains.dokka") version "2.0.0"
}

kotlin {
    jvm("desktop")
    
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
            implementation(compose.materialIconsExtended)
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.ghostwalker18.scheduledesktop2.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.ghostwalker18.scheduledesktop2"
            packageVersion = "1.0.0"
        }
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies{
    add("kspDesktop", libs.androidx.room.compiler)
}