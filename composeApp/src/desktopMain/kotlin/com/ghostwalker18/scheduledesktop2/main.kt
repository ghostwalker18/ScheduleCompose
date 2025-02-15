package com.ghostwalker18.scheduledesktop2

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.favicon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Schedule Desktop 2",
        icon = painterResource(Res.drawable.favicon)
    ) {
        App()
    }
}