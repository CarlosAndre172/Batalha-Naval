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

// Cores do tabuleiro:
internal val laranjaAcerto = Color(0xFFE67E22)   // Casa onde tem uma embarcação.
internal val vermelhoAfundado = Color(0xFF922B21) // Embarcação destruída por completo.

// Cores da tela de posicionamento da frota:
internal val cinzaNavio = Color(0xFF5D6D7E)        // Navio que o jogador já encaixou.
internal val verdePosicionavel = Color(0xFF27AE60) // Prévia: dá pra colocar aqui.
internal val vermelhoBloqueado = Color(0xFFC0392B) // Prévia: não cabe aqui.

// Cores complementares:
internal val roxoEscuro = Color(0xFF6257C7)
internal val roxoClaro = Color(0xFF6257C7)

// Daqui pra baixo são as cores padrão da paleta.
internal val PaletaBatalhaNaval = lightColorScheme(
    primary = azulOceano,
    onPrimary = brancoTexto,
    background = azulProfundo,
    onBackground = azulClaro
)
// Se no futuro você quiser adicionar novas paletas, coloque em outro arquivo!