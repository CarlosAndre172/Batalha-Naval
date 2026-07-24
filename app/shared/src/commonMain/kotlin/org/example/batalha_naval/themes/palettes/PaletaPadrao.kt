package org.example.batalha_naval.themes.palettes

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme

// Aqui ficam APENAS as definições de cores puras.
val AzulProfundo = Color(0xFF0B192C)
val AzulOceano = Color(0xFF1A5276)
val AzulClaro = Color(0xFFD4E6F1)
val BrancoTexto = Color(0xFFFFFFFF)
val marromMadeira = Color(0xC2955C)
val branco = Color(0xFFF5F5DC)


// Daqui pra baixo são as paletas.
internal val PaletaBatalhaNaval = lightColorScheme(
    primary = AzulOceano,
    onPrimary = BrancoTexto,
    background = AzulProfundo,
    onBackground = AzulClaro
)

// Se no futuro você quiser adicionar novas paletas, coloque em outro arquivo!