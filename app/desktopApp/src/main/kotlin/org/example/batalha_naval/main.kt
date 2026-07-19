package org.example.batalha_naval

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Batalha-Naval",
    ) {
        App()
    }
}