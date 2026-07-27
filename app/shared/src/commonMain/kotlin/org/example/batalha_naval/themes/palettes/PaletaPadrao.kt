package org.example.batalha_naval.themes.palettes

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme

// Aqui ficam APENAS as definições de cores puras.
// Cores do fundo:
internal val azulProfundo = Color(0xFF0B192C)
internal val azulOceano = Color(0xFF1A5276)
internal val azulClaro = Color(0xFFD4E6F1)
internal val marromMadeira = Color(0xFF8F642D)
// Cores do texto:
internal val brancoTexto = Color(0xFFFFFFFF)
internal val branco = Color(0xFFF5F5DC)
internal val vermelho = Color(0xFFFF0000)
internal val cinza  = Color(0xFF808080)



// Daqui pra baixo são as cores padrão da paleta.
internal val PaletaBatalhaNaval = lightColorScheme(
    primary = azulOceano,
    onPrimary = brancoTexto,
    background = azulProfundo,
    onBackground = azulClaro
)
// Se no futuro você quiser adicionar novas paletas, coloque em outro arquivo!